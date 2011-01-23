package net.minestatus.minequery;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles Minequery requests.
 *
 * @author Kramer Campbell
 * @since 1.2
 */
public final class Request extends Thread {
    /**
     * The parent plugin object.
     */
    private final Minequery minequery;

    /**
     * The socket we are using to obtain a request.
     */
    private final Socket socket;

    /**
     * The logging utility.
     */
    private final Logger log = Logger.getLogger(getClass().getName());

    /**
     * Creates a new <code>QueryServer</code> object.
     *
     * @param minequery The parent plugin object
     * @param socket    The socket we are using to obtain a request
     */
    public Request(Minequery minequery, Socket socket) {
        this.minequery = minequery;
        this.socket = socket;
    }

    /**
     * Listens for a request.
     */
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read the request and handle it.
            handleRequest(socket, reader.readLine());

            // Finally close the socket.
            socket.close();
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Minequery server thread shutting down.", ex);
        }
    }

    /**
     * Handles a received request.
     *
     * @param request The request message
     * @throws java.io.IOException If an I/O error occurs
     */
    private void handleRequest(Socket socket, String request) throws IOException {
        // Handle a query request.
        if (request == null) {
            return;
        }

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
}
