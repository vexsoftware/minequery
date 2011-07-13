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

package net.minestatus.minequery.util;

import net.minestatus.minequery.Minequery;
import net.minestatus.minequery.util.helper.DataHelper;
import org.bukkit.util.config.ConfigurationNode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;

/**
 * Updates services defined in a configuration file.
 *
 * @author Kramer Campbell
 * @since 2.0
 */
public class Updater implements Runnable {
	@Override
	public void run() {
		Map<String, ConfigurationNode> services = Minequery.getInstance().getConfiguration().getNodes("updater.services");

		for (Map.Entry<String, ConfigurationNode> entry : services.entrySet()) {
			String service = entry.getKey();
			String key = entry.getValue().getString("key", "");
			String url = entry.getValue().getString("url", "");

			// Check if the key and URL are set for this service.
			if (key.equals("") || url.equals("")) {
				Minequery.getInstance().log(Level.WARNING, "Service " + service + " does not have a key or URL, skipping.");
				continue;
			}

			try {
				// Build the data to send to the service.
				StringBuilder data = new StringBuilder();
				data.append("key=").append(key);
				data.append("&data=").append(URLEncoder.encode(new JSONObject(DataHelper.getData()).toString(), "UTF-8"));

				// Establish a HTTP connection to the service.
				HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
				http.setRequestMethod("POST");
				http.setUseCaches(false);
				http.setConnectTimeout(1000);
				http.setAllowUserInteraction(false);
				http.setInstanceFollowRedirects(true);
				http.setRequestProperty("User-Agent", "Java " + System.getProperty("java.version") + "; Minequery " + Minequery.getInstance().getDescription().getVersion());
				http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				http.setDoOutput(true);
				http.setDoInput(true);

				// Send the data to the service.
				DataOutputStream out = new DataOutputStream(http.getOutputStream());
				out.writeBytes(data.toString());

				// Read the response from the service.
				BufferedInputStream in = new BufferedInputStream(http.getInputStream());

				byte buffer[] = new byte[1024];
				String respStr = "";
				while (in.read(buffer, 0, 1024) >= 0) {
					respStr += new String(buffer);
				}

				// Parse the JSON response into a JSON object.
				JSONObject resp = new JSONObject(respStr);
				if (!resp.getBoolean("success")) {
					Minequery.getInstance().log(Level.WARNING, "Service " + service + " rejected our request.");
				} else {
					Minequery.getInstance().log(Level.INFO, "Service " + service + " accepted our request.");
				}

				http.disconnect();
			} catch (UnsupportedEncodingException ex) {
				Minequery.getInstance().log(Level.SEVERE, "Encoding not supported.", ex);
			} catch (MalformedURLException ex) {
				Minequery.getInstance().log(Level.WARNING, "URL for service " + service + " is malformed.");
			} catch (IOException ex) {
				Minequery.getInstance().log(Level.WARNING, "Failed to communicate with the service " + service + ".", ex);
			} catch (JSONException ex) {
				Minequery.getInstance().log(Level.WARNING, "Failed to parse JSON.", ex);
			}
		}
	}
}
