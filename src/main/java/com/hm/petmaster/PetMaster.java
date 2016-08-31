package com.hm.petmaster;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import com.hm.petmaster.command.HelpCommand;
import com.hm.petmaster.command.InfoCommand;
import com.hm.petmaster.listener.PlayerConnectListener;
import com.hm.petmaster.listener.PlayerInteractListener;
import com.hm.petmaster.listener.PlayerQuitListener;
import com.hm.petmaster.utils.FileManager;
import com.hm.petmaster.utils.UpdateChecker;
import com.hm.petmaster.utils.YamlManager;

import net.milkbowl.vault.economy.Economy;

/**
 * Whose pet is this? A plugin to change or display the owner of a pet via a hologram or a chat message.
 * 
 * PetMaster is under GNU General Public License version 3. Please visit the plugin's GitHub for more information :
 * https://github.com/PyvesB/PetMaster
 * 
 * Official plugin's server: hellominecraft.fr
 * 
 * Bukkit project page: dev.bukkit.org/bukkit-plugins/pet-master
 * 
 * Spigot project page: spigotmc.org/resources/pet-master.15904
 * 
 * @since December 2015.
 * @version 1.2
 * @author DarkPyves
 */

public class PetMaster extends JavaPlugin implements Listener {

	// Used for Vault plugin integration.
	private Economy economy;

	// Plugin options and various parameters.
	private boolean disabled;
	private boolean chatMessage;
	private String chatHeader;
	private boolean hologramMessage;
	private int hologramDuration;
	private int changeOwnerPrice;
	private boolean successfulLoad;

	// Fields related to file handling.
	private YamlManager config;
	private YamlManager lang;
	private FileManager fileManager;

	// Contains pairs with name of previous owner and new owner.
	private Map<String, Player> changeOwnershipMap;

	// Plugin listeners.
	private PlayerInteractListener playerInteractListener;
	private PlayerQuitListener playerQuitListener;
	private PlayerConnectListener playerConnectListener;

	// Used to check for plugin updates.
	private UpdateChecker updateChecker;

	// Additional classes related to plugin commands.
	private HelpCommand helpCommand;
	private InfoCommand infoCommand;

	/**
	 * Constructor.
	 */
	public PetMaster() {

		disabled = false;
		fileManager = new FileManager(this);
	}

	/**
	 * Called when server is launched or reloaded.
	 */
	@Override
	public void onEnable() {

		// Start enabling plugin.
		long startTime = System.currentTimeMillis();

		playerInteractListener = new PlayerInteractListener(this);
		playerQuitListener = new PlayerQuitListener(this);
		playerConnectListener = new PlayerConnectListener(this);

		PluginManager pm = getServer().getPluginManager();
		// Register listeners.
		pm.registerEvents(playerInteractListener, this);
		pm.registerEvents(playerQuitListener, this);
		pm.registerEvents(playerConnectListener, this);

		// Check for available plugin update.
		if (config.getBoolean("checkForUpdate", true))
			updateChecker = new UpdateChecker(this);

		extractParametersFromConfig(true);

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			this.getLogger().severe("Error while sending Metrics statistics.");
			successfulLoad = false;
		}

		chatHeader = ChatColor.GRAY + "[" + ChatColor.GOLD + "\u265E" + ChatColor.GRAY + "] ";

		changeOwnershipMap = new HashMap<String, Player>();

		helpCommand = new HelpCommand(this);
		infoCommand = new InfoCommand(this);

		boolean holographicDisplaysAvailable = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

		// Checking whether user configured plugin to display hologram but HolographicsDisplays not available.
		if (hologramMessage && !holographicDisplaysAvailable) {
			successfulLoad = false;
			hologramMessage = false;
			this.getLogger().warning("HolographicDisplays was not found; disabling usage of holograms.");
		}

		if (successfulLoad)
			this.getLogger().info("Plugin successfully enabled and ready to run! Took "
					+ (System.currentTimeMillis() - startTime) + "ms.");
		else
			this.getLogger().severe("Error(s) while loading plugin. Please view previous logs for more information.");
	}

	/**
	 * Extract plugin parameters from the configuration file.
	 * 
	 * @param attemptUpdate
	 */
	private void extractParametersFromConfig(boolean attemptUpdate) {

		successfulLoad = true;
		Logger logger = this.getLogger();

		logger.info("Backing up and loading configuration files...");

		try {
			config = fileManager.getNewConfig("config.yml");
		} catch (IOException e) {
			logger.severe("Error while loading configuration file.");
			e.printStackTrace();
			successfulLoad = false;
		} catch (InvalidConfigurationException e) {
			logger.severe("Error while loading configuration file, disabling plugin.");
			logger.severe("Verify your syntax using the following logs and by visiting yaml-online-parser.appspot.com");
			e.printStackTrace();
			successfulLoad = false;
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		try {
			lang = fileManager.getNewConfig(config.getString("languageFileName", "lang.yml"));
		} catch (IOException e) {
			logger.severe("Error while loading language file.");
			e.printStackTrace();
			successfulLoad = false;
		} catch (InvalidConfigurationException e) {
			logger.severe("Error while loading language file, disabling plugin.");
			logger.severe("Verify your syntax using the following logs and by visiting yaml-online-parser.appspot.com");
			e.printStackTrace();
			successfulLoad = false;
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		try {
			fileManager.backupFile("config.yml");
		} catch (IOException e) {
			logger.severe("Error while backing up configuration file.");
			e.printStackTrace();
			successfulLoad = false;
		}

		try {
			fileManager.backupFile(config.getString("languageFileName", "lang.yml"));
		} catch (IOException e) {
			logger.severe("Error while backing up language file.");
			e.printStackTrace();
			successfulLoad = false;
		}

		// Update configurations from previous versions of the plugin if server reload or restart.
		if (attemptUpdate) {
			updateOldConfiguration();
			updateOldLanguage();
		}

		// Extract options from the config.
		chatMessage = this.getConfig().getBoolean("chatMessage", true);
		hologramMessage = this.getConfig().getBoolean("hologramMessage", true);
		hologramDuration = this.getConfig().getInt("hologramDuration", 50);
		changeOwnerPrice = this.getConfig().getInt("changeOwnerPrice", 0);

		// Set to null in case user changed the option and did a /petm reload. We do not recheck for update on /petm
		// reload.
		if (!config.getBoolean("checkForUpdate", true)) {
			updateChecker = null;
		}
	}

	/**
	 * Update configuration file from older plugin versions by adding missing parameters. Upgrades from versions prior
	 * to 1.2 are not supported.
	 */
	private void updateOldConfiguration() {

		boolean updateDone = false;

		// Added in version 1.2:
		if (!config.getKeys(false).contains("languageFileName")) {
			config.set("languageFileName", "lang.yml", "Name of the language file.");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("checkForUpdate")) {
			config.set("checkForUpdate", true,
					"Check for update on plugin launch and notify when an OP joins the game.");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("changeOwnerPrice")) {
			config.set("changeOwnerPrice", true, "# Price of the /petm setowner command (requires Vault).");
			updateDone = true;
		}

		if (updateDone) {
			// Changes in the configuration: save and do a fresh load.
			try {
				config.saveConfig();
				config.reloadConfig();
			} catch (IOException e) {
				this.getLogger().severe("Error while saving changes to the configuration file.");
				e.printStackTrace();
				successfulLoad = false;
			}
		}
	}

	/**
	 * Update language file from older plugin versions by adding missing parameters. Upgrades from versions prior to 1.2
	 * are not supported.
	 */
	private void updateOldLanguage() {

		boolean updateDone = false;

		// Added in version 1.2:
		if (!lang.getKeys(false).contains("petmaster-command-setowner-hover")) {
			lang.set("petmaster-command-setowner-hover",
					"You can only change the ownership of your own pets, unless you're admin!");
			updateDone = true;
		}

		if (!lang.getKeys(false).contains("petmaster-command-disable-hover")) {
			lang.set("petmaster-command-disable-hover", "The plugin will not work until next reload or /petm enable.");
			updateDone = true;
		}

		if (!lang.getKeys(false).contains("petmaster-command-enable-hover")) {
			lang.set("petmaster-command-enable-hover",
					"Plugin enabled by default. Use this if you entered /petm disable before!");
			updateDone = true;
		}

		if (!lang.getKeys(false).contains("petmaster-command-reload-hover")) {
			lang.set("petmaster-command-reload-hover", "Reload most settings in config.yml and lang.yml files.");
			updateDone = true;
		}

		if (!lang.getKeys(false).contains("petmaster-command-info-hover")) {
			lang.set("petmaster-command-info-hover", "Some extra info about the plugin and its awesome author!");
			updateDone = true;
		}
		
		if (!lang.getKeys(false).contains("petmaster-tip")) {
			lang.set("petmaster-tip", "&lHINT&r &8You can &7&n&ohover&r &8or &7&n&oclick&r &8on the commands!");
			updateDone = true;
		}
		
		if (!lang.getKeys(false).contains("change-owner-price")) {
			lang.set("change-owner-price", "You payed: AMOUNT !");
			updateDone = true;
		}

		if (updateDone) {
			// Changes in the language file: save and do a fresh load.
			try {
				lang.saveConfig();
				lang.reloadConfig();
			} catch (IOException e) {
				this.getLogger().severe("Error while saving changes to the language file.");
				e.printStackTrace();
				successfulLoad = false;
			}
		}
	}

	/**
	 * Called when server is stopped or reloaded.
	 */
	@Override
	public void onDisable() {

		changeOwnershipMap.clear();
		this.getLogger().info("PetMaster has been disabled.");
	}

	/**
	 * Called when a player or the console enters a command.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {

		if (!cmd.getName().equalsIgnoreCase("petm"))
			return false;

		if (!sender.hasPermission("petmaster.use")) {
			sender.sendMessage(
					chatHeader + lang.getString("no-permissions", "You do not have the permission to do this."));

		} else if (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("help")) {

			helpCommand.getHelp(sender);

		} else if (args[0].equalsIgnoreCase("info")) {

			infoCommand.getInfo(sender);

		} else if (args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("petmaster.admin")) {

				this.reloadConfig();
				extractParametersFromConfig(false);
				if (successfulLoad) {
					if (sender instanceof Player)
						sender.sendMessage(chatHeader + lang.getString("configuration-successfully-reloaded",
								"Configuration successfully reloaded."));
					this.getLogger().info("Configuration successfully reloaded.");
				} else {
					sender.sendMessage(chatHeader + lang.getString("configuration-reload-failed",
							"Errors while reloading configuration. Please view logs for more details."));
					this.getLogger().severe("Errors while reloading configuration. Please view logs for more details.");
				}
			} else
				sender.sendMessage(
						chatHeader + lang.getString("no-permissions", "You do not have the permission to do this."));

		} else if (args[0].equalsIgnoreCase("disable")) {
			if (sender.hasPermission("petmaster.admin")) {
				disabled = true;
				sender.sendMessage(chatHeader
						+ lang.getString("petmaster-disabled", "PetMaster disabled till next reload or /petm enable."));
			} else
				sender.sendMessage(
						chatHeader + lang.getString("no-permissions", "You do not have the permission to do this."));

		} else if (args[0].equalsIgnoreCase("enable")) {
			if (sender.hasPermission("petmaster.admin")) {
				disabled = false;
				sender.sendMessage(chatHeader + lang.getString("petmaster-enabled", "PetMaster enabled."));
			} else
				sender.sendMessage(
						chatHeader + lang.getString("no-permissions", "You do not have the permission to do this."));

		} else if (args[0].equalsIgnoreCase("setowner") && sender instanceof Player) {

			if (args.length == 2) {
				Player newOwner = null;
				for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
					if (currentPlayer.getName().equalsIgnoreCase(args[1])) {
						newOwner = currentPlayer;
						break;
					}
				}
				if (newOwner == null)
					sender.sendMessage(
							chatHeader + lang.getString("player-offline", "The specified player is offline!"));
				else if (!sender.hasPermission("petmaster.setowner") || disabled)
					sender.sendMessage(chatHeader
							+ lang.getString("no-permissions", "You do not have the permission to do this."));
				else if (!sender.hasPermission("petmaster.admin")
						&& newOwner.getName().equals(((Player) sender).getName()))
					sender.sendMessage(chatHeader
							+ lang.getString("cannot-change-to-yourself", "You cannot change the owner to yourself!"));
				else {
					changeOwnershipMap.put(((Player) sender).getName(), newOwner);
					sender.sendMessage(chatHeader
							+ lang.getString("right-click", "Right click on one of your pets to change its owner!"));
				}
			} else
				sender.sendMessage(
						chatHeader + lang.getString("misused-command", "Misused command. Please type /petm."));

		} else
			sender.sendMessage(chatHeader + lang.getString("misused-command", "Misused command. Please type /petm."));

		return true;
	}

	/**
	 * Try to hook up with Vault, and log if this is called on plugin initialisation.
	 * 
	 * @param log
	 * @return true if Vault available, false otherwise
	 */
	public boolean setUpEconomy() {

		if (economy != null)
			return true;

		try {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
					.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}

			return (economy != null);
		} catch (NoClassDefFoundError e) {
			this.getLogger().warning("Attempt to hook up with Vault failed. Money reward ignored.");
			return false;
		}
	}

	/**
	 * Various getters and setters.
	 */

	public boolean isDisabled() {

		return disabled;
	}

	public void setSuccessfulLoad(boolean successfulLoad) {

		this.successfulLoad = successfulLoad;
	}

	public boolean isChatMessage() {

		return chatMessage;
	}

	public boolean isHologramMessage() {

		return hologramMessage;
	}

	public String getChatHeader() {

		return chatHeader;
	}

	public int getHologramDuration() {

		return hologramDuration;
	}

	public Map<String, Player> getChangeOwnershipMap() {

		return changeOwnershipMap;
	}

	public YamlManager getPluginLang() {

		return lang;
	}

	public UpdateChecker getUpdateChecker() {

		return updateChecker;
	}

	public Economy getEconomy() {

		return economy;
	}

	public int getChangeOwnerPrice() {

		return changeOwnerPrice;
	}

}
