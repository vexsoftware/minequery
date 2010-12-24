import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

public class MineQuery extends Plugin {
    private final String NAME = "MineQuery";
    private final String VERSION = "1.0";

    private Logger log;
    private PropertiesFile serverProperties;

    private boolean silent;
    private boolean verbose;
    private MineQueryServer queryServer;

    public void enable() {
        log = Logger.getLogger("Minecraft");
        log(NAME + " " + VERSION + " loaded.");

        serverProperties = new PropertiesFile("server.properties");

        int queryPort = serverProperties.getInt("minequery-port", 25566);
        silent = serverProperties.getBoolean("minequery-silent", false);
        verbose = serverProperties.getBoolean("minequery-verbose", false);

        queryServer = new MineQueryServer(queryPort);
        (new Thread(queryServer)).start();
    }

    public void disable() {
        try {
            if (queryServer.serverSocket != null) {
                queryServer.serverSocket.close();
            }
        } catch (IOException e) {
            log("[MineQuery] " + e.toString());
        }

        log(NAME + " " + VERSION + " unloaded.");
    }

    public void initialize() {
    }

    public void log(String message) {
        if (silent) {
            return;
        }

        log.info(message);
    }

    private class MineQueryServer extends Thread {
        public ServerSocket serverSocket;

        private int port;

        public MineQueryServer(int port) {
            this.port = port;
        }

        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                log("[MineQuery] Query server listening on port " + Integer.toString(port));

                while (!serverSocket.isClosed()) {
                    Socket connectionSocket = serverSocket.accept();
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    String in = inFromClient.readLine();

                    if (verbose) {
                        log("[MineQuery] Received " + in + " from " + connectionSocket.getRemoteSocketAddress().toString());
                    }

                    int serverPort = serverProperties.getInt("server-port");
                    int playerCount = etc.getServer().getPlayerList().size();
                    String[] playerList = new String[playerCount];

                    for (int i = 0; i < playerCount; i++) {
                        playerList[i] = etc.getServer().getPlayerList().get(i).getName();
                    }

                    String out = "SERVERPORT " + Integer.toString(serverPort) + "\n" +
                            "PLAYERCOUNT " + Integer.toString(playerCount) + "\n" +
                            "PLAYERLIST " + Arrays.toString(playerList) + "\n";

                    outToClient.writeBytes(out);
                    connectionSocket.close();
                }
            } catch (IOException e) {
                log("[MineQuery] " + e.toString());
                etc.getLoader().disablePlugin("MineQuery");
            }
        }
    }
}