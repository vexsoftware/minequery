package net.minestatus.minequery.net;

import net.minestatus.minequery.Minequery;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

/**
 * The main networking hub that listens for and responds to Minequery requests.
 * 
 * @author Blake Beaupain
 * @author Kramer Campbell
 * @since 1.0
 */
public final class QueryServer extends Thread {

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
	private ServerSocket listener;

	/**
	 * Creates a new <code>QueryServer</code> object.
	 * 
	 * @param host
	 * 			  The host that this server will bind to.
	 * @param port
	 *            The port that this server will bind on.
	 */
	public QueryServer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Starts the ServerSocket listener.
	 *
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public void startListener() throws IOException {
		// Initialize the listener.
		InetSocketAddress address;
		if (host.equalsIgnoreCase("ANY") || host.equalsIgnoreCase("0.0.0.0")) {
			Minequery.getInstance().log(Level.INFO, "Starting Minequery server on *:" + Integer.toString(port));
			address = new InetSocketAddress(port);
		} else {
			Minequery.getInstance().log(Level.INFO, "Starting Minequery server on " + host + ":" + Integer.toString(port));
			address = new InetSocketAddress(host, port);
		}
		listener = new ServerSocket();
		listener.bind(address);
	}

	@Override
	public void run() {
		try {
			while (!listener.isClosed()) {
				// Wait for and accept all incoming connections.
				Socket socket = getListener().accept();

				// Create a new thread to handle the request.
				(new Thread(new RequestHandler(socket))).start();
			}
		} catch (IOException ignored) {}
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
