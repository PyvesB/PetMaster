package com.hm.petmaster;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.hm.petmaster.command.HelpCommand;
import com.hm.petmaster.command.InfoCommand;
import com.hm.petmaster.language.Lang;
import com.hm.petmaster.listener.PlayerInteractListener;
import com.hm.petmaster.metrics.MetricsLite;

/**
 * Whose pet is this? A simple plugin to display the owner of a pet via a
 * hologram or a chat message. PetMaster is under GNU General Public License
 * version 3.
 * 
 * @since December 2015.
 * @version 1.0
 * @author DarkPyves
 */

public class PetMaster extends JavaPlugin implements Listener {

	// Plugin options and various parameters.
	private boolean disabled;
	private boolean chatMessage;
	private String chatHeader;
	private boolean hologramMessage;
	private boolean useHolographicDisplays;
	private int hologramDuration;

	// Plugin listeners.
	private PlayerInteractListener playerInteractListener;

	// Additional classes related to plugin commands.
	private HelpCommand helpCommand;
	private InfoCommand infoCommand;

	/**
	 * Constructor.
	 */
	public PetMaster() {

		disabled = false;
	}

	/**
	 * Called when server is launched or reloaded.
	 */
	public void onEnable() {

		loadLang();
		this.saveDefaultConfig();

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			this.getLogger().severe("Error while sending Metrics statistics.");
		}

		playerInteractListener = new PlayerInteractListener(this);

		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(playerInteractListener, this);

		extractParametersFromConfig();

		chatHeader = ChatColor.GRAY + "[" + ChatColor.GOLD + "\u265E" + ChatColor.GRAY + "] ";

		helpCommand = new HelpCommand(this);
		infoCommand = new InfoCommand(this);

		useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

		this.getLogger().info("PetMaster v" + this.getDescription().getVersion() + " has been enabled.");

	}

	/**
	 * Extract plugin parameters from the configuration file.
	 */
	private void extractParametersFromConfig() {

		chatMessage = this.getConfig().getBoolean("chatMessage", true);
		hologramMessage = this.getConfig().getBoolean("hologramMessage", true);
		hologramDuration = this.getConfig().getInt("hologramDuration", 50);
	}

	/**
	 * Load the lang.yml file.
	 */
	public void loadLang() {

		File lang = new File(getDataFolder(), "lang.yml");
		if (!lang.exists()) {
			try {
				getDataFolder().mkdir();
				lang.createNewFile();
				Reader defConfigStream = new InputStreamReader(this.getResource("lang.yml"), "UTF8");
				if (defConfigStream != null) {
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
					defConfig.save(lang);
					Lang.setFile(defConfig);
					return;
				}
			} catch (IOException e) {

				this.getLogger().severe("Error while creating language file.");
				e.printStackTrace();
			}
		}
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		for (Lang item : Lang.values()) {
			if (conf.getString(item.getPath()) == null) {
				conf.set(item.getPath(), item.getDefault());
			}
		}
		Lang.setFile(conf);
		try {
			conf.save(lang);
		} catch (IOException e) {

			this.getLogger().severe("Error while saving language file.");
			e.printStackTrace();
		}
	}

	/**
	 * Called when server is stopped or reloaded.
	 */
	public void onDisable() {

		this.getLogger().info("PetMaster has been disabled.");
	}

	/**
	 * Called when a player or the console enters a command.
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {

		if (!cmd.getName().equalsIgnoreCase("petm"))
			return false;

		if (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("help")) {

			helpCommand.getHelp(sender);

		} else if (args[0].equalsIgnoreCase("info")) {

			infoCommand.getInfo(sender);

		} else if (args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("petmaster.admin")) {
				try {

					loadLang();
					this.reloadConfig();
					extractParametersFromConfig();
					sender.sendMessage(chatHeader + Lang.CONFIGURATION_SUCCESSFULLY_RELOADED);
				} catch (Exception ex) {
					sender.sendMessage(chatHeader + Lang.CONFIGURATION_RELOAD_FAILED);
					ex.printStackTrace();
				}
			} else
				sender.sendMessage(chatHeader + Lang.NO_PERMS);
		}

		else if (sender.hasPermission("petmaster.admin")) {

			String action = args[0].toLowerCase();

			if (action.equals("disable")) {

				disabled = true;
				sender.sendMessage(chatHeader + Lang.PETMASTER_DISABLED);

			} else if (action.equals("enable")) {

				disabled = false;
				sender.sendMessage(chatHeader + Lang.PETMASTER_ENABLED);

			} else
				sender.sendMessage(chatHeader + Lang.MISUSED_COMMAND);

		} else
			sender.sendMessage(chatHeader + Lang.NO_PERMS);

		return true;
	}

	/**
	 * Various getters and setters.
	 */

	public boolean isDisabled() {

		return disabled;
	}

	public boolean isChatMessage() {

		return chatMessage;
	}

	public boolean isHoologramMessage() {

		return hologramMessage;
	}

	public String getChatHeader() {

		return chatHeader;
	}

	public boolean isUseHolographicDisplays() {

		return useHolographicDisplays;
	}

	public int getHologramDuration() {

		return hologramDuration;
	}

}
