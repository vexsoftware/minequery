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
 * @author Blake Beaupain
 * @since 1.0
 */
public final class QueryServer extends Thread {

    /**
     * The parent plugin object.
     */
    private final Minequery minequery;

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
    private final Logger log = Logger.getLogger(getClass().getName());

    /**
     * Creates a new <code>QueryServer</code> object.
     *
     * @param minequery The parent plugin object
     * @param port      The port that this server will run on
     * @throws IOException If an I/O error occurs
     */
    public QueryServer(Minequery minequery, int port) throws IOException {
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

                // Create a new thread to handle the request.
                (new Thread(new Request(getMinequery(), socket))).start();
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Minequery server thread shutting down.", ex);
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
