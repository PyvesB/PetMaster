package com.hm.petmaster.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.hm.petmaster.PetMaster;
import com.hm.petmaster.utils.UpdateChecker;

	/**
	 * Listener class to notify users about plugin updates.
	 * 
	 * @author Pyves
	 *
	 */
	public class PlayerConnectListener implements Listener {

		private PetMaster plugin;

		public PlayerConnectListener(PetMaster plugin) {

			this.plugin = plugin;
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		public void onPlayerJoin(PlayerJoinEvent event) {

			// Check if OP to display new version message if needed.
			if (plugin.getUpdateChecker() != null && plugin.getUpdateChecker().isUpdateNeeded()
					&& event.getPlayer().hasPermission("petmaster.admin")) {
				event.getPlayer().sendMessage(plugin.getChatHeader() + "Update available: v"
						+ plugin.getUpdateChecker().getVersion() + ". Download at one of the following:");
				event.getPlayer().sendMessage(ChatColor.GRAY + UpdateChecker.BUKKIT_DONWLOAD_URL);
				event.getPlayer().sendMessage(ChatColor.GRAY + UpdateChecker.SPIGOT_DONWLOAD_URL);
			}

		}
}
