package com.hm.petmaster.command;

import com.hm.petmaster.PetMaster;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

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
		List<TagResolver> templates = new ArrayList<>();
		templates.add(Placeholder.component("name", Component.text(plugin.getDescription().getName())));
		templates.add(Placeholder.component("version", Component.text(plugin.getDescription().getVersion())));
		templates.add(Placeholder.component("website", Component.text(
				plugin.getDescription().getWebsite() != null ? plugin.getDescription().getWebsite() : ""
		)));
		templates.add(Placeholder.component("author", Component.text(plugin.getDescription().getAuthors().get(0))));
		templates.add(Placeholder.component("description-details", Component.text(
				plugin.getPluginLang().getString(
						"version-command-description-details",
						"Manage pets and display useful information via holograms, action bar or chat messages!"
				)
		)));
		templates.add(Placeholder.component("state", Component.text(
				plugin.getEnableDisableCommand().isDisabled() ? "NO" : "YES")
		));
		plugin.getMessageSender().sendMessage(sender, "version-command-name", templates.toArray(new TagResolver[]{}));
		plugin.getMessageSender().sendMessage(sender, "version-command-version", templates.toArray(new TagResolver[]{}));
		plugin.getMessageSender().sendMessage(sender, "version-command-website", templates.toArray(new TagResolver[]{}));
		plugin.getMessageSender().sendMessage(sender, "version-command-author", templates.toArray(new TagResolver[]{}));
		plugin.getMessageSender().sendMessage(sender, "version-command-description", templates.toArray(new TagResolver[]{}));
		plugin.getMessageSender().sendMessage(sender, "version-command-enabled", templates.toArray(new TagResolver[]{}));
	}
}
