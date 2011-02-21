package net.minestatus.minequery;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * The main networking hub that listens for and responds to Minequery requests.
 * 
 * @author Blake Beaupain
 * @author Kramer Campbell
 * @since 1.0
 */
public final class QueryServer extends Thread {

	/**
	 * The parent plugin object.
	 */
	private final Minequery minequery;

	/**
	 * The host that the server will listen on.
	 */
	private final String host;

	/**
	 * The QueryServer port.
	 */
	private final int port;

	/**
	 * The connection listener.
	 */
	private final ServerSocket listener;

	/**
	 * The logging utility.
	 */
	private final Logger log = Logger.getLogger("Minecraft");

	/**
	 * Creates a new <code>QueryServer</code> object.
	 * 
	 * @param minequery
	 *            The parent plugin object
	 * @param port
	 *            The port that this server will run on
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public QueryServer(Minequery minequery, String host, int port) throws IOException {
		this.minequery = minequery;
		this.host = host;
		this.port = port;

		// Initialize the listener.
		InetSocketAddress address;
		if (host.equalsIgnoreCase("ANY")) {
			log.info("Starting Minequery server on *:" + Integer.toString(port));
			address = new InetSocketAddress(port);
		} else {
			log.info("Starting Minequery server on " + host + ":" + Integer.toString(port));
			address = new InetSocketAddress(host, port);
		}
		listener = new ServerSocket();
		listener.bind(address);
	}

	@Override
	public void run() {
		try {
			while (true) {
				// Wait for and accept all incoming connections.
				Socket socket = getListener().accept();

				// Create a new thread to handle the request.
				(new Thread(new Request(getMinequery(), socket))).start();
			}
		} catch (IOException ex) {
			log.info("Stopping Minequery server");
		}
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
	 * Gets the <code>QueryServer</code> host.
	 * 
	 * @return The host, default ANY
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Gets the <code>QueryServer</code> port.
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
