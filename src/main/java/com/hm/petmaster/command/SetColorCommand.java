package com.hm.petmaster.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.hm.petmaster.PetMaster;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Class in charge of handling player requests to change ownership of a pet
 * (/petm setowner).
 *
 * @author Pyves
 *
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
		if (args.length == 2) {
			DyeColor color = DyeColor.valueOf(args[1]);
			if (!player.hasPermission("petmaster.setcolor")) {
				player.sendMessage(plugin.getChatHeader() + plugin.getPluginLang().getString("no-permissions",
					"You do not have the permission to do this."));
			} else if (plugin.getEnableDisableCommand().isDisabled()) {
				player.sendMessage(plugin.getChatHeader() + plugin.getPluginLang().getString("currently-disabled",
					"PetMaster is currently disabled, you cannot use this command."));
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
				} catch (IOException e) {
					plugin.getLogger().severe("Error while loading " + playerColorConfig.getName());
					plugin.getLogger().log(Level.SEVERE, "Verify your syntax by visiting yaml-online-parser.appspot.com and using the following logs: ", e);
				}
			}
		} else {
			player.sendMessage(plugin.getChatHeader()
				+ plugin.getPluginLang().getString("misused-command", "Misused command. Please type /petm."));
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
