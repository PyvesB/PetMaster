package com.hm.petmaster.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hm.petmaster.PetMaster;

/**
 * Class in charge of reloading the plugin's configuration files (/petm reload).
 * 
 * @author Pyves
 * 
 */
public class ReloadCommand {

	private final PetMaster plugin;

	public ReloadCommand(PetMaster plugin) {
		this.plugin = plugin;
	}

	public void reload(CommandSender sender) {
		if (sender.hasPermission("petmaster.admin")) {
			plugin.reloadConfig();
			plugin.extractParametersFromConfig(false);
			if (plugin.getServer().getPluginManager().isPluginEnabled(plugin)) {
				if (sender instanceof Player) {
					sender.sendMessage(plugin.getChatHeader() + plugin.getPluginLang()
							.getString("configuration-successfully-reloaded", "Configuration successfully reloaded."));
				}
				plugin.getLogger().info("Configuration successfully reloaded.");
			} else {
				if (sender instanceof Player) {
					sender.sendMessage(
							plugin.getChatHeader() + plugin.getPluginLang().getString("configuration-reload-failed",
									"Errors while reloading configuration. Please view logs for more details."));
				}
				plugin.getLogger().severe("Errors while reloading configuration. Please view logs for more details.");
			}
		} else {
			sender.sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("no-permissions", "You do not have the permission to do this."));
		}
	}

}
