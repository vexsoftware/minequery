package net.minestatus.minequery.util;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import net.minestatus.minequery.Minequery;
import org.bukkit.plugin.Plugin;

public class PermissionsHelper {
	private static PermissionHandler permissionHandler;

	public static boolean isPermissionsAvailable() {
		setupPermissions();
		return permissionHandler != null;
	}

	public static PermissionHandler getPermissionHandler() {
		return permissionHandler;
	}

	public static void setupPermissions() {
		if (permissionHandler == null) {
			Plugin permissionsPlugin = Minequery.getInstance().getServer().getPluginManager().getPlugin("Permissions");

			if (permissionsPlugin != null) {
				permissionHandler = ((Permissions) permissionsPlugin).getHandler();
			}
		}
	}
}
