package net.minestatus.minequery;

import net.minestatus.minequery.net.QueryServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
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
		instance = this;
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
		loadConfiguration();

		try {
			log.info("Starting Minequery version " + getDescription().getVersion());

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

	private void loadConfiguration() {
		// Check if the plugin data folder exists.
		if (!getDataFolder().exists()) {
			if (!getDataFolder().mkdirs()) {
				log.warning("Failed to create plugin data folder.");
			}
		}

		// Check if the config file exists.
		try {
			File config = new File(getDataFolder() + "/config.yml");
			if (!config.exists()) {
				if (!config.createNewFile()) {
					throw new IOException();
				}

				// Default configuration.
				getConfiguration().setProperty("ip", "");
				getConfiguration().setProperty("port", 25566);
				getConfiguration().save();
			}
		} catch (IOException ex) {
			log.warning("Failed to create plugin configuration file.");
		}

		getConfiguration().load();
		serverIP = getServer().getIp();
		serverPort = getServer().getPort();
		minequeryIP = getConfiguration().getString("ip", serverIP);
		minequeryPort = getConfiguration().getInt("port", 25566);
		maxPlayers = getServer().getMaxPlayers();

		if (serverIP.equals("")) {
			// Assume if the server IP is blank that we're listening on ANY.
			serverIP = "ANY";
		}

		if (minequeryIP.equals("")) {
			// Assume if the Minequery IP is blank that we're listening on the same IP as the server IP.
			minequeryIP = serverIP;
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
