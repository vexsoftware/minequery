package net.minestatus.minequery;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A port of Minequery that works with the Bukkit plugin platform.
 *
 * @since 1.0
 * @author Blake Beaupain
 */
public final class Minequery extends JavaPlugin {

	/** The main configuration file. */
	public static final String CONFIG_FILE = "server.properties";

	/** The logging utility (used for error logging). */
	private final Logger log = Logger.getLogger(getClass().getName());

	/** The port of the Minecraft server. */
	private int serverPort;

	/** The maximum amount of players allowed on the Minecraft server. */
	private int maxPlayers;

	/** The main Minequery server. */
	private MinequeryServer server;

	/**
	 * Creates a new <code>Minequery</code> object. This constructor is
	 * inherited from {@link org.bukkit.plugin.java.JavaPlugin}.
	 *
	 * @param pluginLoader
	 * @param instance
	 * @param desc
     * @param folder
	 * @param plugin
	 * @param cLoader
	 */
	 public Minequery(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
	        super(pluginLoader, instance, desc, folder, plugin, cLoader);

		// Initialize the Minequery plugin.
		try {
			Properties props = new Properties();
			props.load(new FileReader(CONFIG_FILE));
			server = new MinequeryServer(this, Integer.parseInt(props.getProperty("minequery-port", "25566")));
			serverPort = Integer.parseInt(props.getProperty("server-port"));
			maxPlayers = Integer.parseInt(props.getProperty("max-players"));
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error initializing Minequery.", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	@Override
	public void onDisable() {
		try {
			server.getListener().close();
		} catch (IOException ex) {
			log.log(Level.WARNING, "Unable to close the MinequeryServer listener.", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	@Override
	public void onEnable() {
		if (server == null) {
			throw new IllegalStateException("Cannot enable - MinequeryServer not initialized.");
		}

		// Start the server normally.
		server.start();
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
	 * Gets the maximum amount of players the Minecraft server can hold.
	 *
	 * @return The maximum amount of players
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

}
