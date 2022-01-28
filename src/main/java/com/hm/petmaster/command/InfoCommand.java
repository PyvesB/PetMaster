package com.hm.petmaster.command;

import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.hm.petmaster.PetMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Class in charge of displaying the plugin's extra information (/petm info).
 * 
 * @author Pyves
 * 
 */
public class InfoCommand {

	private final PetMaster plugin;

	public InfoCommand(PetMaster plugin) {
		this.plugin = plugin;
	}

	/**
	 * Displays information about the plugin.
	 * 
	 * @param sender
	 */
	public void getInfo(CommandSender sender) {
		List<Template> templates = new ArrayList<>();
		templates.add(Template.of("name", plugin.getDescription().getName()));
		templates.add(Template.of("version", plugin.getDescription().getVersion()));
		templates.add(Template.of("website", plugin.getDescription().getWebsite()));
		templates.add(Template.of("author", plugin.getDescription().getAuthors().get(0)));
		templates.add(Template.of("description-details", plugin.getPluginLang().getString("version-command-description-details",
				"Manage pets and display useful information via holograms, action bar or chat messages!")));
		templates.add(Template.of("state", plugin.getEnableDisableCommand().isDisabled() ? "NO" : "YES"));
		plugin.getMessageSender().sendMessage(sender, "version-command-name", templates);
		plugin.getMessageSender().sendMessage(sender, "version-command-version", templates);
		plugin.getMessageSender().sendMessage(sender, "version-command-website", templates);
		plugin.getMessageSender().sendMessage(sender, "version-command-author", templates);
		plugin.getMessageSender().sendMessage(sender, "version-command-description", templates);
		plugin.getMessageSender().sendMessage(sender, "version-command-enabled", templates);
	}
}
