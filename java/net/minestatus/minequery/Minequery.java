package net.minestatus.minequery;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A port of Minequery that works with the Bukkit plugin platform.
 * 
 * @author Blake Beaupain
 * @author Kramer Campbell
 * @since 1.0
 */
public final class Minequery extends JavaPlugin {

	/**
	 * The main configuration file.
	 */
	private static final String CONFIG_FILE = "server.properties";

	/**
	 * The logging utility (used for error logging).
	 */
	private final Logger log = Logger.getLogger("Minecraft");

	/**
	 * The host that the server listens on (any by default).
	 */
	private String serverIP;

	/**
	 * The port of the Minecraft server.
	 */
	private int serverPort;

	/**
	 * The host of the Minequery server.
	 */
	private String minequeryIP;

	/**
	 * The port of the Minequery server.
	 */
	private int minequeryPort;

	/**
	 * The maximum amount of players allowed on the Minecraft server.
	 */
	private int maxPlayers;

	/**
	 * The main Minequery server.
	 */
	private QueryServer server;

	private static Minequery instance;

	/**
	 * Creates a new <code>Minequery</code> object.
	 */
	public Minequery() {
		try {
			// Initialize the Minequery plugin.
			Properties props = new Properties();
			props.load(new FileReader(CONFIG_FILE));
			serverIP = props.getProperty("server-ip", "ANY");
			serverPort = Integer.parseInt(props.getProperty("server-port", "25565"));
			minequeryIP = props.getProperty("minequery-ip");
			minequeryPort = Integer.parseInt(props.getProperty("minequery-port", "25566"));
			maxPlayers = Integer.parseInt(props.getProperty("max-players", "32"));

			// By default, "server-ip=" is set in server.properties which causes the default in getProperty() to not
			// apply. This checks if it's blank and sets it to "ANY" if so.
			if (serverIP.equals("")) {
				serverIP = "ANY";
			}

			// Is the minequery-ip property defined?
			if (minequeryIP != null) {
				// Just in case users use the same practice as above for "minequery-ip="
				if (minequeryIP.equals("")) {
					minequeryIP = "ANY";
				}
			} else {
				// Let's assume to use the same host as the Minecraft server.
				// For backwards compatibility.
				minequeryIP = serverIP;
			}

			instance = this;
		} catch (FileNotFoundException ex) {
			// Highly unlikely to ever get this exception as the server.properties file is created before hand.
			log.log(Level.SEVERE, "Could not find " + CONFIG_FILE, ex);
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error initializing Minequery", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	@Override
	public void onDisable() {
		log.info("Stopping Minequery server");

		try {
			if (server != null && server.getListener() != null)
				server.getListener().close();
		} catch (IOException ex) {
			log.log(Level.WARNING, "Unable to close the Minequery listener", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	@Override
	public void onEnable() {
		try {
			// Initialize a new server thread.
			server = new QueryServer(minequeryIP, minequeryPort);

			// Start the server listener.
			server.startListener();

			// Start listening for requests.
			server.start();
		} catch (BindException ex) {
			log.log(Level.SEVERE, "Minequery cannot bind to the port " + minequeryPort + ". Perhaps it's already in use?");
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error starting server listener", ex);
		}
	}

	/**
	 * Gets the host that the Minecraft server is running on.
	 *
	 * @return The Minecraft server host
	 */
	public String getServerIP() {
		return serverIP;
	}

	/**
	 * Gets the port that the Minecraft server is running on.
	 *
	 * @return The Minecraft server port
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Gets the host that the Minequery server is running on.
	 *
	 * @return The Minequery server host
	 */
	public String getMinequeryIP() {
		return minequeryIP;
	}

	/**
	 * Gets the port that the Minequery server is running on.
	 *
	 * @return The Minequery server port
	 */
	public int getMinequeryPort() {
		return minequeryPort;
	}

	/**
	 * Gets the maximum amount of players the Minecraft server can hold.
	 * 
	 * @return The maximum amount of players
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * Gets the Minequery plugin instance.
	 *
	 * @return The instance of Minequery
	 */
	public static Minequery getInstance() {
		return instance;
	}

}
