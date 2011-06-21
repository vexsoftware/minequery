package net.minestatus.minequery;

import net.minestatus.minequery.util.Updater;
import net.minestatus.minequery.net.QueryServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
	 * The logging utility.
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

	/**
	 * The main updater instance.
	 */
	private Updater updater;

	/**
	 * The instance of the plugin.
	 */
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
		log(Level.INFO, "Stopping Minequery server");

		try {
			if (server != null && server.getListener() != null)
				server.getListener().close();
		} catch (IOException ex) {
			log(Level.WARNING, "Unable to close the Minequery listener", ex);
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

		log(Level.INFO, "Starting Minequery version " + getDescription().getVersion());

		// Server mode
		if (getConfiguration().getBoolean("server.enabled", false)) {
			try {
				// Initialize a new server thread.
				server = new QueryServer(minequeryIP, minequeryPort);

				// Start the server listener.
				server.startListener();

				// Start listening for requests.
				server.start();
			} catch (BindException ex) {
				log(Level.SEVERE, "Could not bind to the port " + minequeryPort + ". Perhaps it's already in use?");
			} catch (IOException ex) {
				log(Level.SEVERE, "Error starting server listener", ex);
			}
		}

		// Updater mode
		if (getConfiguration().getBoolean("updater.enabled", false)) {
			// Schedule the updater.
			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleAtFixedRate(new Updater(), 0, 5, TimeUnit.SECONDS);

		}
	}

	private void loadConfiguration() {
		// Check if the plugin data folder exists.
		if (!getDataFolder().exists()) {
			if (!getDataFolder().mkdirs()) {
				log(Level.WARNING, "Failed to create plugin data folder.");
			}
		}

		// Check if the config file exists.
		try {
			File config = new File(getDataFolder() + "/config.yml");
			if (!config.exists()) {
				if (!config.createNewFile()) {
					throw new IOException();
				}

				// Set up the default configuration.

				// Server mode
				getConfiguration().setProperty("server.ip", "");
				getConfiguration().setProperty("server.port", 25566);
				getConfiguration().setProperty("server.enabled", true);

				// Updater mode
				getConfiguration().setProperty("updater.enabled", false);
				getConfiguration().setProperty("updater.services.minestatus.key", "");
				getConfiguration().setProperty("updater.services.minestatus.url", "");
				getConfiguration().setProperty("updater.services.myserverlist.key", "");
				getConfiguration().setProperty("updater.services.myserverlist.url", "");

				getConfiguration().save();
			}
		} catch (IOException ex) {
			log(Level.WARNING, "Failed to create plugin configuration file.");
		}

		getConfiguration().load();
		serverIP = getServer().getIp();
		serverPort = getServer().getPort();
		minequeryIP = getConfiguration().getString("server.ip", serverIP);
		minequeryPort = getConfiguration().getInt("server.port", 25566);
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
	 * Sends a message to the logger.
	 *
	 * @param level The severity of the message.
	 * @param msg The message to be sent.
	 */
	public void log(Level level, String msg) {
		log.log(level, "[Minequery] " + msg);
	}

	/**
	 * Sends a message to the logger along with the exception.
	 *
	 * @param level The severity of the message.
	 * @param msg The message to be sent.
	 * @param thrown The exception thrown.
	 */
	public void log(Level level, String msg, Throwable thrown) {
		log.log(level, "[Minequery] " + msg, thrown);
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
