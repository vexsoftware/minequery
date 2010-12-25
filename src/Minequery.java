import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * The Minequery plugin class.
 * @author Kramer Campbell
 */
public class Minequery extends Plugin {
    private final String NAME = "Minequery";
    private final String VERSION = "1.0";

    private Logger log;
    private PropertiesFile serverProperties;

    private boolean silent;
    private boolean verbose;
    private MinequeryServer queryServer;

    /**
     * Called when the plugin is enabled.
     */
    public void enable() {
        log = Logger.getLogger("Minecraft");
        log(NAME + " " + VERSION + " loaded.");

        serverProperties = new PropertiesFile("server.properties");

        int queryPort = serverProperties.getInt("minequery-port", 25566);
        silent = serverProperties.getBoolean("minequery-silent", false);
        verbose = serverProperties.getBoolean("minequery-verbose", false);

        queryServer = new MinequeryServer(queryPort);
        (new Thread(queryServer)).start();
    }

    /**
     * Called when the plugin is disabled.
     */
    public void disable() {
        try {
            if (queryServer.serverSocket != null) {
                queryServer.serverSocket.close();
            }
        } catch (IOException e) {
            log("[Minequery] " + e.toString());
        }

        log(NAME + " " + VERSION + " unloaded.");
    }

    /**
     * Called when the plugin is loaded.
     */
    public void initialize() {
    }

    /**
     * Writes a message to the log if silent mode is disabled.
     * @param message The message to write to the log.
     */
    public void log(String message) {
        if (silent) {
            return;
        }

        log.info(message);
    }

    /**
     * The query server class.
     */
    public class MinequeryServer extends Thread {
        /**
         * The query server socket.
         */
        public ServerSocket serverSocket;

        private int port;

        /**
         * Constructs the query server.
         * @param port The port the query server should run on.
         */
        public MinequeryServer(int port) {
            this.port = port;
        }

        /**
         * The query server thread.
         */
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                log("[Minequery] Query server listening on port " + Integer.toString(port));

                while (!serverSocket.isClosed()) {
                    Socket connectionSocket = serverSocket.accept();
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    String in = inFromClient.readLine();

                    if (verbose) {
                        log("[Minequery] Received " + in + " from " + connectionSocket.getRemoteSocketAddress().toString());
                    }

                    if (in != null) {
                        if (in.equalsIgnoreCase("QUERY")) {
                            int serverPort = serverProperties.getInt("server-port");
                            int playerCount = etc.getServer().getPlayerList().size();
                            String[] playerList = new String[playerCount];

                            for (int i = 0; i < playerCount; i++) {
                                playerList[i] = etc.getServer().getPlayerList().get(i).getName();
                            }

                            String out = "SERVERPORT " + Integer.toString(serverPort) + "\n" +
                                    "PLAYERCOUNT " + Integer.toString(playerCount) + "\n" +
                                    "PLAYERLIST " + Arrays.toString(playerList) + "\n";

                            try {
                                outToClient.writeBytes(out);
                            } catch (IOException ignored) {}
                        }
                    }

                    connectionSocket.close();
                }
            } catch (IOException e) {
                log("[Minequery] " + e.toString());
                etc.getLoader().disablePlugin("Minequery");
            }
        }
    }
}