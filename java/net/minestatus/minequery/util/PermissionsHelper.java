package net.minestatus.minequery.util;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import net.minestatus.minequery.Minequery;
import org.bukkit.plugin.Plugin;

/**
 * Helper for the Permissions plugin.
 *
 * @author Kramer Campbell
 */
public class PermissionsHelper {
	private static PermissionHandler permissionHandler;

	/**
	 * Checks if Permissions is available by first attempting to initializing the PermissionHandler
	 * if it hasn't already or wasn't able to do so.
	 *
	 * @return <code>true</code> if Permissions is available, <code>false</code> if not.
	 */
	public static boolean isPermissionsAvailable() {
		setupPermissions();
		return permissionHandler != null;
	}

	/**
	 * Gets the PermissionHandler instance.
	 *
	 * @return The PermissionHandler instance.
	 */
	public static PermissionHandler getPermissionHandler() {
		return permissionHandler;
	}

	/**
	 * Attempts to initialize the PermissionHandler.
	 */
	public static void setupPermissions() {
		if (permissionHandler == null) {
			Plugin permissionsPlugin = Minequery.getInstance().getServer().getPluginManager().getPlugin("Permissions");

			if (permissionsPlugin != null) {
				permissionHandler = ((Permissions) permissionsPlugin).getHandler();
			}
		}
	}
}
