package com.hm.petmaster.command;

import com.hm.petmaster.PetMaster;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Class in charge of handling player requests to change default color of a pets
 * collar. (/petm setcolor).
 */
public class SetColorCommand {

	private static final String COLOR_CONFIG_NAME = "color";

	private final PetMaster plugin;
	private final File playerColorConfig;

	public SetColorCommand(PetMaster plugin, File playerColorConfig) {
		this.plugin = plugin;
		this.playerColorConfig = playerColorConfig;
	}

	public void setColor(Player player, String[] args) {
		if (args.length != 2) {
			plugin.getMessageSender().sendMessage(player, "misused-command");
			return;
		}
		try {
			DyeColor color = DyeColor.valueOf(args[1].toUpperCase());
			if (!player.hasPermission("petmaster.setcolor")) {
				plugin.getMessageSender().sendMessage(player, "no-permissions");
			} else if (plugin.getEnableDisableCommand().isDisabled()) {
				plugin.getMessageSender().sendMessage(player, "currently-disabled");
			} else {
				try {
					if (!playerColorConfig.exists()) {
						playerColorConfig.createNewFile();
					}
					FileConfiguration config = YamlConfiguration.loadConfiguration(playerColorConfig);
					ConfigurationSection playerConfig = config.getConfigurationSection(player.getUniqueId().toString());
					if (playerConfig == null) {
						playerConfig = config.createSection(player.getUniqueId().toString());
					}
					playerConfig.set(COLOR_CONFIG_NAME, color.toString());
					config.save(playerColorConfig);

					plugin.getMessageSender().sendMessage(player, "color-successfully-set");
				} catch (IOException e) {
					plugin.getLogger().severe("Error while loading " + playerColorConfig.getName());
					plugin.getLogger().log(Level.SEVERE, "Verify your syntax by visiting yaml-online-parser.appspot.com and using the following logs: ", e);
				}
			}
		} catch (IllegalArgumentException ex) {
			StringBuilder colors = new StringBuilder();
			int length = DyeColor.values().length;
			for (int i = 0; i < length; ++i) {
				DyeColor color = DyeColor.values()[i];
				colors.append(color.name().toLowerCase());
				if (i < length - 1) {
					colors.append(' ');
				}
			}
			plugin.getMessageSender().sendMessage(
					player,
					"available-colors",
					Placeholder.component("colors", Component.text(colors.toString()))
			);
		}
	}

	public DyeColor getColor(UUID player) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(playerColorConfig);
		ConfigurationSection playerConfig = config.getConfigurationSection(player.toString());
		if (playerConfig != null) {
			String color = playerConfig.getString(COLOR_CONFIG_NAME);
			if (color != null) {
				return DyeColor.valueOf(color);
			}
		}
		return DyeColor.RED;
	}
}
