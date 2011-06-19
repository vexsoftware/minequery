package net.minestatus.minequery.net;

import com.nijiko.permissions.PermissionHandler;
import net.minestatus.minequery.Minequery;
import net.minestatus.minequery.util.PermissionsHelper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles Minequery requests.
 * 
 * @author Kramer Campbell
 * @author Blake Beaupain
 * @since 1.2
 */
public final class Request extends Thread {
	/**
	 * The socket we are using to obtain a request.
	 */
	private final Socket socket;

	/**
	 * The logging utility.
	 */
	private final Logger log = Logger.getLogger("Minecraft");

	/**
	 * Creates a new <code>QueryServer</code> object.
	 * 
	 * @param socket
	 *            The socket we are using to obtain a request
	 */
	public Request(Socket socket) {
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
			log.log(Level.SEVERE, "Minequery server thread shutting down", ex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Handles a received request.
	 * 
	 * @param socket
	 * 			  The client we are handling the request for.
	 * @param request
	 *            The request message.
	 * @throws java.io.IOException
	 *            If an I/O error occurs.
	 * @throws org.json.JSONException
	 * 			  If a JSON error occurs.
	 */
	private void handleRequest(Socket socket, String request) throws IOException, JSONException {
		if (request == null) {
			return;
		}

		// Handle a standard Minequery request.
		if (request.equalsIgnoreCase("QUERY")) {
			Minequery m = Minequery.getInstance();

			// Build the response.
			StringBuilder resp = new StringBuilder();
			resp.append("SERVERPORT ").append(m.getServerPort()).append("\n");
			resp.append("PLAYERCOUNT ").append(m.getServer().getOnlinePlayers().length).append("\n");
			resp.append("MAXPLAYERS ").append(m.getMaxPlayers()).append("\n");
			resp.append("PLAYERLIST ").append(new JSONArray(getPlayerList()).toString()).append("\n");
			resp.append("SERVERIP ").append(m.getServerIP()).append("\n");
			resp.append("EXTENDEDPLAYERLIST ").append(new JSONArray(getExtendedPlayerList()).toString()).append("\n");
			resp.append("PLUGINS ").append(new JSONArray(getPluginList()).toString()).append("\n");
			resp.append("VERSIONS ").append(new JSONObject(getVersions()).toString()).append("\n");

			// Send the response.
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(resp.toString());
		}

		// Handle a request, respond in JSON format.
		if (request.equalsIgnoreCase("QUERY_JSON")) {
			Minequery m = Minequery.getInstance();

			// Build the JSON response.
			Map<String, Object> items = new HashMap<String, Object>();
			items.put("serverIP", m.getServerIP());
			items.put("serverPort", m.getServerPort());
			items.put("playerCount", m.getServer().getOnlinePlayers().length);
			items.put("maxPlayers", m.getMaxPlayers());
			items.put("playerList", getPlayerList());
			items.put("extendedPlayerList", getExtendedPlayerList());
			items.put("plugins", getPluginList());
			items.put("versions", getVersions());

			StringBuilder resp = new StringBuilder();
			resp.append(new JSONObject(items).toString()).append("\n");

			// Send the JSON response.
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(resp.toString());
		}

		// Responses simply with the version of Minequery.
		if (request.equalsIgnoreCase("VERSION")) {
			Minequery m = Minequery.getInstance();

			// Build the response.
			StringBuilder resp = new StringBuilder();
			resp.append(m.getDescription().getVersion()).append("\n");

			// Send the response.
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeBytes(resp.toString());
		}

		// Different requests may be introduced in the future.
	}

	private String[] getPlayerList() {
		Minequery m = Minequery.getInstance();

		String[] playerList = new String[m.getServer().getOnlinePlayers().length];
		for (int i = 0; i < m.getServer().getOnlinePlayers().length; i++) {
			playerList[i] = m.getServer().getOnlinePlayers()[i].getName();
		}

		return playerList;
	}

	private List<Map<String, Object>> getExtendedPlayerList() {
		Minequery m = Minequery.getInstance();

		Player[] players = m.getServer().getOnlinePlayers();
		List<Map<String, Object>> playerList = new ArrayList<Map<String, Object>>();

		for (Player player : players) {
			Map<String, Object> playerMap = new HashMap<String, Object>();
			playerMap.put("name", player.getName());
			playerMap.put("displayName", player.getDisplayName());
			playerMap.put("health", player.getHealth());

			Map<String, Object> locationMap = new HashMap<String, Object>();
			locationMap.put("blockX", player.getLocation().getBlockX());
			locationMap.put("blockY", player.getLocation().getBlockY());
			locationMap.put("blockZ", player.getLocation().getBlockZ());
			locationMap.put("pitch", player.getLocation().getPitch());
			locationMap.put("x", player.getLocation().getX());
			locationMap.put("y", player.getLocation().getY());
			locationMap.put("z", player.getLocation().getZ());
			locationMap.put("world", player.getLocation().getWorld().getName());

			playerMap.put("location", locationMap);
			playerMap.put("isDead", player.isDead());
			playerMap.put("isSleeping", player.isSleeping());
			playerMap.put("isOp", player.isOp());

			// Permissions specific values.
			if (PermissionsHelper.isPermissionsAvailable()) {
				Map<String, Object> permissionsMap = new HashMap<String, Object>();
				PermissionHandler permissionHandler = PermissionsHelper.getPermissionHandler();
				permissionsMap.put("groups", permissionHandler.getGroups(player.getWorld().getName(), player.getName()));

				playerMap.put("permissions", permissionsMap);
			}

			playerList.add(playerMap);
		}

		return playerList;
	}

	private List<Map<String, String>> getPluginList() {
		Minequery m = Minequery.getInstance();

		Plugin[] plugins = m.getServer().getPluginManager().getPlugins();
		List<Map<String, String>> pluginList = new ArrayList<Map<String, String>>();

		for (Plugin plugin : plugins) {
			Map<String, String> pluginMap = new HashMap<String, String>();
			pluginMap.put("name", plugin.getDescription().getName());
			pluginMap.put("version", plugin.getDescription().getVersion());
			pluginList.add(pluginMap);
		}

		return pluginList;
	}

	private Map<String, String> getVersions() {
		Minequery m = Minequery.getInstance();

		Map<String, String> versions = new HashMap<String, String>();

		// Find the CraftBukkit build and the Minecraft version.
		String version = m.getServer().getVersion();
		Matcher matcher = Pattern.compile("git-Bukkit-.*-b(\\d+)jnks \\(MC: (.*)\\)").matcher(version);
		List<Object> matchList = new ArrayList<Object>();

		while (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				matchList.add(matcher.group(i));
			}
		}

		// One day the version string could completely change,
		// so check if we have at last two matches.
		if (matchList.size() > 1) {
			versions.put("craftbukkit", matchList.get(0).toString());
			versions.put("minecraft", matchList.get(1).toString());
		}

		// Add the Minequery version.
		versions.put("minequery", m.getDescription().getVersion());

		return versions;
	}
}
