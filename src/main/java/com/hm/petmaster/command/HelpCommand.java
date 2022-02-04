package com.hm.petmaster.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hm.mcshared.particle.FancyMessageSender;
import com.hm.petmaster.PetMaster;

/**
 * Class in charge of displaying the plugin's help (/petm help).
 * 
 * @author Pyves
 * 
 */
public class HelpCommand {

	private final PetMaster plugin;

	public HelpCommand(PetMaster plugin) {
		this.plugin = plugin;
	}

	public void getHelp(CommandSender sender) {
		// Header.
		plugin.getMessageSender().sendMessage(sender, "petmaster-help-header");

		plugin.getMessageSender().sendMessage(sender, "petmaster-command-info");


		if (sender.hasPermission("petmaster.free")) {
			plugin.getMessageSender().sendMessage(sender, "petmaster-command-free");
		}

		if (sender.hasPermission("petmaster.admin")) {
			plugin.getMessageSender().sendMessage(sender, "petmaster-command-reload");

			plugin.getMessageSender().sendMessage(sender, "petmaster-command-enable");

			plugin.getMessageSender().sendMessage(sender, "petmaster-command-disable");
		}

		if (sender.hasPermission("petmaster.setowner")) {
			plugin.getMessageSender().sendMessage(sender, "petmaster-command-setowner");
		}
		
		if (sender.hasPermission("petmaster.setcolor")) {
			plugin.getMessageSender().sendMessage(sender, "petmaster-command-setcolor");
		}

		// Empty line.
		sender.sendMessage("");

		// Tip message.
		plugin.getMessageSender().sendMessage(sender, "petmaster-tip");
	}
}
