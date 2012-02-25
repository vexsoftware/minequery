package net.minestatus.minequery;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

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
	public static final String CONFIG_FILE = "server.properties";

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
	 * The port of the Minequery server.
	 */
	private int port;

	/**
	 * The maximum amount of players allowed on the Minecraft server.
	 */
	private int maxPlayers;

	/**
	 * The main Minequery server.
	 */
	private QueryServer server;

	/**
	 * Creates a new <code>Minequery</code> object.
	 */
	public Minequery() {
		// Initialize the Minequery plugin.
		try {
			Properties props = new Properties();
			props.load(new FileReader(CONFIG_FILE));
			serverIP = props.getProperty("server-ip", "ANY");
			serverPort = Integer.parseInt(props.getProperty("server-port", "25565"));
			port = Integer.parseInt(props.getProperty("minequery-port", "25566"));
			maxPlayers = Integer.parseInt(props.getProperty("max-players", "32"));

			// By default, "server-ip=" is set in server.properties which causes the default in getProperty() to not
			// apply. This checks if it's blank and sets it to "ANY" if so.
			if (serverIP.equals("")) {
				serverIP = "ANY";
			}

			server = new QueryServer(this, serverIP, port);
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
		try {
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
		if (server == null) {
			throw new IllegalStateException("Cannot enable - Minequery not initialized");
		}

		// Start the server normally.
		server.start();
	}


    public int getPlayerAmount()
    {
        int amount = 0;
    	for(Player player : getServer().getOnlinePlayers())
    	{
    		if(!hiddenPlayers.contains(player.getName()))
    		{
    			amount++;
    		}
    	}
    	return amount;
    }
    
    public String getOnlinePlayers(boolean json)
    {
    	String result = "";
    	if(json)
    	{
    		StringBuilder resp = new StringBuilder();
    		int count = 0;
            Player players[] = getServer().getOnlinePlayers();
            int len$ = players.length;
            for(int i$ = 0; i$ < len$; i$++)
            {
                Player player = players[i$];
                if(!hiddenPlayers.contains(player.getName()))
                {
                	resp.append((new StringBuilder()).append("\"").append(player.getName()).append("\"").toString());
                    if(++count < getPlayerAmount())
                        resp.append(",");
                }
            }
            result = resp.toString();
    	}
    	else
    	{
    		String playerList[] = new String[getPlayerAmount()];
    		int i = 0;
            for(Player player : getServer().getOnlinePlayers())
            {
            	if(!hiddenPlayers.contains(player.getName()))
            	{
            		playerList[i] = player.getName();
            		i++;
            	}
            	
            }
            result = Arrays.toString(playerList);
    	}
    	return result;
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
	 * Gets the port that the Minequery server is running on.
	 *
	 * @return The Minecraft server port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Gets the maximum amount of players the Minecraft server can hold.
	 * 
	 * @return The maximum amount of players
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

    public void setPlayerVisiblity(Player player, boolean hide)
    {
        if(hide)
    	{
    		if(!hiddenPlayers.contains(player.getName()))
    		{
    			hiddenPlayers.add(player.getName());
    		}
    	}
    	else
    	{
    		if(hiddenPlayers.contains(player.getName()))
    		{
    			hiddenPlayers.remove(player.getName());
    		}
    	}
    }
}
