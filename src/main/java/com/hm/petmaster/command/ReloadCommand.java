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
					Player player = (Player)  sender;
					plugin.getMessageSender().sendMessage(player, "configuration-successfully-reloaded");
				}
				plugin.getLogger().info("Configuration successfully reloaded.");
			} else {
				if (sender instanceof Player) {
					Player player = (Player)  sender;
					plugin.getMessageSender().sendMessage(sender, "configuration-reload-failed");
				}
				plugin.getLogger().severe("Errors while reloading configuration. Please view logs for more details.");
			}
		} else {
			plugin.getMessageSender().sendMessage(sender, "no-permissions");
		}
	}

}
