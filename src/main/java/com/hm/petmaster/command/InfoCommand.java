package com.hm.petmaster.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.hm.petmaster.PetMaster;

/**
 * Class in charge of displaying the plugin's extra information (/petm info).
 * 
 * @author Pyves
 */
public class InfoCommand {

	private PetMaster plugin;

	public InfoCommand(PetMaster plugin) {

		this.plugin = plugin;
	}

	/**
	 * Display information about the plugin.
	 */
	public void getInfo(CommandSender sender) {

		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD
				+ plugin.getPluginLang().getString("version-command-name", "Name:") + " " + ChatColor.GRAY
				+ plugin.getDescription().getName());
		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD
				+ plugin.getPluginLang().getString("version-command-version", "Version:") + " " + ChatColor.GRAY
				+ plugin.getDescription().getVersion());
		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD
				+ plugin.getPluginLang().getString("version-command-website", "Website:") + " " + ChatColor.GRAY
				+ plugin.getDescription().getWebsite());
		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD
				+ plugin.getPluginLang().getString("version-command-author", "Author:") + " " + ChatColor.GRAY
				+ plugin.getDescription().getAuthors().get(0));
		sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD
				+ plugin.getPluginLang().getString("version-command-description", "Description:") + " " + ChatColor.GRAY
				+ plugin.getPluginLang().getString("version-command-description-details",
						"Whose pet is this? A simple plugin to change or display the owner of a pet via a hologram or a chat message."));
		if (plugin.isDisabled())
			sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD
					+ plugin.getPluginLang().getString("version-command-enabled", "Plugin enabled:") + " "
					+ ChatColor.GRAY + "NO");
		else
			sender.sendMessage(plugin.getChatHeader() + ChatColor.GOLD
					+ plugin.getPluginLang().getString("version-command-enabled", "Plugin enabled:") + " "
					+ ChatColor.GRAY + "YES");
	}
}
