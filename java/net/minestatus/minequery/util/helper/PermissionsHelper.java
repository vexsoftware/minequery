/*
 * Minequery
 * Copyright (C) 2011 Vex Software LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minestatus.minequery.util.helper;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import net.minestatus.minequery.Minequery;
import org.bukkit.plugin.Plugin;

/**
 * Helper for the Permissions plugin.
 *
 * @author Kramer Campbell
 * @since 2.0
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
