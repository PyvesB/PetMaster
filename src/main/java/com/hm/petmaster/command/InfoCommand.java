package com.hm.petmaster.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.hm.petmaster.PetMaster;
import com.hm.petmaster.language.Lang;

public class InfoCommand {

	private PetMaster plugin;

	public InfoCommand(PetMaster plugin) {

		this.plugin = plugin;
	}

	/**
	 * Display information about the plugin.
	 */
	public void getInfo(CommandSender sender) {

		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD + Lang.VERSION_COMMAND_NAME + " " + ChatColor.GRAY
				+ plugin.getDescription().getName());
		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD + Lang.VERSION_COMMAND_VERSION + " "
				+ ChatColor.GRAY + plugin.getDescription().getVersion());
		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD + Lang.VERSION_COMMAND_WEBSITE + " "
				+ ChatColor.GRAY + plugin.getDescription().getWebsite());
		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD + Lang.VERSION_COMMAND_AUTHOR + " " + ChatColor.GRAY
				+ plugin.getDescription().getAuthors().get(0));
		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD + Lang.VERSION_COMMAND_DESCRIPTION + " "
				+ ChatColor.GRAY + Lang.VERSION_COMMAND_DESCRIPTION_DETAILS);
		if (plugin.isDisabled())
			sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD + Lang.VERSION_COMMAND_ENABLED + " "
					+ ChatColor.GRAY + "NO");
		else
			sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD + Lang.VERSION_COMMAND_ENABLED + " "
					+ ChatColor.GRAY + "YES");
	}
}
