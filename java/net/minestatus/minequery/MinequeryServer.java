package net.minestatus.minequery;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main networking hub that listens for and responds to Minequery requests.
 *
 * @since 1.0
 * @author Blake Beaupain
 */
public final class MinequeryServer extends Thread {

	/** The parent plugin object. */
	private final Minequery minequery;

	/** The MinequeryServer port. */
	private final int port;

	/** The connection listener. */
	private final ServerSocket listener;

	/** The logging utility. */
	private final Logger log = Logger.getLogger(getClass().getName());

	/**
	 * Creates a new <code>MinequeryServer</code> object.
	 *
	 * @param minequery
	 *            The parent plugin object
	 * @param port
	 *            The port that this server will run on
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public MinequeryServer(Minequery minequery, int port) throws IOException {
		this.minequery = minequery;
		this.port = port;
		listener = new ServerSocket(port);
	}

	@Override
	public void run() {
		try {
			while (true) {
				// Wait for and accept all incoming connections.
				Socket socket = getListener().accept();

				// Read the request and handle it.
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				handleRequest(socket, reader.readLine());
				socket.close();
			}
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Minequery server thread shutting down.", ex);
		}
	}

	/**
	 * Handles a received request.
	 *
	 * @param request
	 *            The request message
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	private void handleRequest(Socket socket, String request) throws IOException {
		// Handle a query request.
		if (request.equalsIgnoreCase("QUERY")) {
			Minequery m = getMinequery();

            String[] playerList = new String[m.getServer().getOnlinePlayers().length];
            for (int i = 0; i < m.getServer().getOnlinePlayers().length; i++) {
                playerList[i] = m.getServer().getOnlinePlayers()[i].getName();
            }

			// Build the response.
			StringBuilder resp = new StringBuilder();
			resp.append("SERVERPORT " + m.getServerPort() + "\n");
			resp.append("PLAYERCOUNT " + m.getServer().getOnlinePlayers().length + "\n");
			resp.append("MAXPLAYERS " + m.getMaxPlayers() + "\n");
			resp.append("PLAYERLIST " + Arrays.toString(playerList) + "\n");

			// Send the response.
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(resp.toString());
		}

		// Different requests may be introduced in the future.
	}

	/**
	 * Gets the <code>Minequery</code> parent plugin object.
	 *
	 * @return The Minequery object
	 */
	public Minequery getMinequery() {
		return minequery;
	}

	/**
	 * Gets the <code>MinequeryServer</code> port.
	 *
	 * @return The port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the listening <code>ServerSocket</code>.
	 *
	 * @return The server socket
	 */
	public ServerSocket getListener() {
		return listener;
	}

}
