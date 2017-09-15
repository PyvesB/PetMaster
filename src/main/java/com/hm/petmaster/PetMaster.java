package com.hm.petmaster;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.hm.mcshared.file.CommentedYamlConfiguration;
import com.hm.mcshared.update.UpdateChecker;
import com.hm.petmaster.command.HelpCommand;
import com.hm.petmaster.command.InfoCommand;
import com.hm.petmaster.listener.PlayerInteractListener;
import com.hm.petmaster.listener.PlayerQuitListener;

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
 * @version 1.6.2
 * @author DarkPyves
 */
public class PetMaster extends JavaPlugin implements Listener {

	// Contains pairs with name of previous owner and new owner.
	private final Map<String, Player> changeOwnershipMap;
	// Contains names of owners wanting to free their pets.
	private final Set<String> freePetSet;

	// Used for Vault plugin integration.
	private Economy economy;

	// Plugin options and various parameters.
	private boolean disabled;
	private String chatHeader;
	private boolean chatMessage;
	private boolean hologramMessage;
	private boolean actionBarMessage;
	private boolean successfulLoad;

	// Fields related to file handling.
	private CommentedYamlConfiguration config;
	private CommentedYamlConfiguration lang;

	// Plugin listeners.
	private PlayerInteractListener playerInteractListener;
	private PlayerQuitListener playerQuitListener;

	// Used to check for plugin updates.
	private UpdateChecker updateChecker;

	// Additional classes related to plugin commands.
	private HelpCommand helpCommand;
	private InfoCommand infoCommand;

	/**
	 * Constructor.
	 */
	public PetMaster() {
		changeOwnershipMap = new HashMap<>();
		freePetSet = new HashSet<>();
		disabled = false;
	}

	/**
	 * Called when server is launched or reloaded.
	 */
	@Override
	public void onEnable() {
		// Start enabling plugin.
		long startTime = System.currentTimeMillis();

		this.getLogger().info("Registering listeners...");

		playerInteractListener = new PlayerInteractListener(this);
		playerQuitListener = new PlayerQuitListener(this);

		PluginManager pm = getServer().getPluginManager();
		// Register listeners.
		pm.registerEvents(playerInteractListener, this);
		pm.registerEvents(playerQuitListener, this);

		extractParametersFromConfig(true);

		chatHeader = ChatColor.GRAY + "[" + ChatColor.GOLD + "\u265E" + ChatColor.GRAY + "] ";

		// Check for available plugin update.
		if (config.getBoolean("checkForUpdate", true)) {
			updateChecker = new UpdateChecker(this, "https://raw.githubusercontent.com/PyvesB/PetMaster/master/pom.xml",
					new String[] { "dev.bukkit.org/bukkit-plugins/pet-master/files",
							"spigotmc.org/resources/pet-master.15904" },
					"petmaster.admin", chatHeader);
			pm.registerEvents(updateChecker, this);
			updateChecker.launchUpdateCheckerTask();
		}

		helpCommand = new HelpCommand(this);
		infoCommand = new InfoCommand(this);

		boolean holographicDisplaysAvailable = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");

		// Checking whether user configured plugin to display hologram but HolographicsDisplays not available.
		if (hologramMessage && !holographicDisplaysAvailable) {
			successfulLoad = false;
			hologramMessage = false;
			actionBarMessage = true;
			this.getLogger().warning(
					"HolographicDisplays was not found; disabling usage of holograms and enabling action bar messages.");
		}

		if (successfulLoad) {
			this.getLogger().info("Plugin successfully enabled and ready to run! Took "
					+ (System.currentTimeMillis() - startTime) + "ms.");
		} else {
			this.getLogger().severe("Error(s) while loading plugin. Please view previous logs for more information.");
		}
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
			config = new CommentedYamlConfiguration("config.yml", this);
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error while loading configuration file: ", e);
			successfulLoad = false;
		} catch (InvalidConfigurationException e) {
			logger.severe("Error while loading configuration file, disabling plugin.");
			logger.log(Level.SEVERE,
					"Verify your syntax by visiting yaml-online-parser.appspot.com and using the following logs: ", e);
			successfulLoad = false;
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		try {
			lang = new CommentedYamlConfiguration(config.getString("languageFileName", "lang.yml"), this);
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error while loading language file: ", e);
			successfulLoad = false;
		} catch (InvalidConfigurationException e) {
			logger.severe("Error while loading language file, disabling plugin.");
			this.getLogger().log(Level.SEVERE,
					"Verify your syntax by visiting yaml-online-parser.appspot.com and using the following logs: ", e);
			successfulLoad = false;
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		try {
			config.backupConfiguration();
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error while backing up configuration file: ", e);
			successfulLoad = false;
		}

		try {
			lang.backupConfiguration();
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error while backing up language file: ", e);
			successfulLoad = false;
		}

		// Update configurations from previous versions of the plugin if server reloads or restarts.
		if (attemptUpdate) {
			updateOldConfiguration();
			updateOldLanguage();
		}

		// Extract options from the config.
		chatMessage = config.getBoolean("chatMessage", false);
		hologramMessage = config.getBoolean("hologramMessage", true);
		actionBarMessage = config.getBoolean("actionBarMessage", true);
		playerInteractListener.extractParameters();

		// Unregister events if user changed the option and did a /petm reload. Do not recheck for update on /petm
		// reload.
		if (!config.getBoolean("checkForUpdate", true)) {
			PlayerJoinEvent.getHandlerList().unregister(updateChecker);
		}
	}

	/**
	 * Update configuration file from older plugin versions by adding missing parameters. Upgrades from versions prior
	 * to 1.2 are not supported.
	 */
	private void updateOldConfiguration() {
		boolean updateDone = false;

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
			config.set("changeOwnerPrice", 0, "Price of the /petm setowner command (requires Vault).");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("displayDog")) {
			config.set("displayDog", true, "Take dogs into account.");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("displayCat")) {
			config.set("displayCat", true, "Take cats into account.");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("displayHorse")) {
			config.set("displayHorse", true, "Take horses into account.");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("displayLlama")) {
			config.set("displayLlama", true, "Take llamas into account.");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("displayParrot")) {
			config.set("displayParrot", true, "Take parrots into account.");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("actionBarMessage")) {
			config.set("actionBarMessage", false,
					"Enable or disable action bar messages when right-clicking on a pet.");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("displayToOwner")) {
			config.set("displayToOwner", false,
					"Enable or disable showing ownership information for a player's own pets.");
			updateDone = true;
		}

		if (!config.getKeys(false).contains("freePetPrice")) {
			config.set("freePetPrice", 0, "Price of the /petm free command (requires Vault).");
			updateDone = true;
		}

		if (updateDone) {
			// Changes in the configuration: save and do a fresh load.
			try {
				config.saveConfiguration();
				config.loadConfiguration();
			} catch (IOException | InvalidConfigurationException e) {
				this.getLogger().log(Level.SEVERE, "Error while saving changes to the configuration file: ", e);
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

		if (!lang.getKeys(false).contains("petmaster-action-bar")) {
			lang.set("petmaster-action-bar", "Pet owned by ");
			updateDone = true;
		}

		if (!lang.getKeys(false).contains("petmaster-command-free")) {
			lang.set("petmaster-command-free", "Free a pet.");
			updateDone = true;
		}

		if (!lang.getKeys(false).contains("petmaster-command-free-hover")) {
			lang.set("petmaster-command-free-hover", "You can only free your own pets, unless you're admin!");
			updateDone = true;
		}

		if (!lang.getKeys(false).contains("pet-freed")) {
			lang.set("pet-freed", "Say goodbye: this pet returned to the wild!");
			updateDone = true;
		}

		if (!lang.getKeys(false).contains("not-enough-money")) {
			lang.set("not-enough-money", "You do not have the required amount: AMOUNT !");
			updateDone = true;
		}

		if (updateDone) {
			// Changes in the language file: save and do a fresh load.
			try {
				lang.saveConfiguration();
				lang.loadConfiguration();
			} catch (IOException | InvalidConfigurationException e) {
				this.getLogger().log(Level.SEVERE, "Error while saving changes to the language file: ", e);
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
		freePetSet.clear();
		this.getLogger().info("PetMaster has been disabled.");
	}

	/**
	 * Called when a player or the console enters a command.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!"petm".equalsIgnoreCase(cmd.getName())) {
			return false;
		}

		if (args.length == 0 || args.length == 1 && "help".equalsIgnoreCase(args[0])) {
			helpCommand.getHelp(sender);
		} else if ("info".equalsIgnoreCase(args[0])) {
			infoCommand.getInfo(sender);
		} else if ("reload".equalsIgnoreCase(args[0])) {
			if (sender.hasPermission("petmaster.admin")) {
				this.reloadConfig();
				extractParametersFromConfig(false);
				if (successfulLoad) {
					if (sender instanceof Player) {
						sender.sendMessage(chatHeader + lang.getString("configuration-successfully-reloaded",
								"Configuration successfully reloaded."));
					}
					this.getLogger().info("Configuration successfully reloaded.");
				} else {
					sender.sendMessage(chatHeader + lang.getString("configuration-reload-failed",
							"Errors while reloading configuration. Please view logs for more details."));
					this.getLogger().severe("Errors while reloading configuration. Please view logs for more details.");
				}
			} else {
				sender.sendMessage(
						chatHeader + lang.getString("no-permissions", "You do not have the permission to do this."));
			}
		} else if ("disable".equalsIgnoreCase(args[0])) {
			if (sender.hasPermission("petmaster.admin")) {
				disabled = true;
				sender.sendMessage(chatHeader
						+ lang.getString("petmaster-disabled", "PetMaster disabled till next reload or /petm enable."));
			} else
				sender.sendMessage(
						chatHeader + lang.getString("no-permissions", "You do not have the permission to do this."));
		} else if ("enable".equalsIgnoreCase(args[0])) {
			if (sender.hasPermission("petmaster.admin")) {
				disabled = false;
				sender.sendMessage(chatHeader + lang.getString("petmaster-enabled", "PetMaster enabled."));
			} else
				sender.sendMessage(
						chatHeader + lang.getString("no-permissions", "You do not have the permission to do this."));
		} else {
			String playerName = ((Player) sender).getName();
			if ("setowner".equalsIgnoreCase(args[0]) && sender instanceof Player) {
				if (args.length == 2) {
					Player newOwner = null;
					for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
						if (currentPlayer.getName().equalsIgnoreCase(args[1])) {
							newOwner = currentPlayer;
							break;
						}
					}
					if (newOwner == null) {
						sender.sendMessage(
								chatHeader + lang.getString("player-offline", "The specified player is offline!"));
					} else if (!sender.hasPermission("petmaster.setowner") || disabled) {
						sender.sendMessage(chatHeader
								+ lang.getString("no-permissions", "You do not have the permission to do this."));
					} else if (!sender.hasPermission("petmaster.admin") && newOwner.getName().equals(playerName)) {
						sender.sendMessage(chatHeader + lang.getString("cannot-change-to-yourself",
								"You cannot change the owner to yourself!"));
					} else {
						changeOwnershipMap.put(playerName, newOwner);
						sender.sendMessage(chatHeader + lang.getString("right-click",
								"Right click on a pet to change its owner!"));
						// Cancel previous pending operation.
						freePetSet.remove(playerName);
					}
				} else {
					sender.sendMessage(
							chatHeader + lang.getString("misused-command", "Misused command. Please type /petm."));
				}
			} else if ("free".equalsIgnoreCase(args[0]) && sender instanceof Player) {
				if (args.length == 1) {
					if (!sender.hasPermission("petmaster.free") || disabled) {
						sender.sendMessage(chatHeader
								+ lang.getString("no-permissions", "You do not have the permission to do this."));
					} else {
						freePetSet.add(playerName);
						sender.sendMessage(chatHeader + lang.getString("right-click",
								"Right click on a pet to change its owner!"));
						// Cancel previous pending operation.
						changeOwnershipMap.remove(playerName);
					}
				} else {
					sender.sendMessage(
							chatHeader + lang.getString("misused-command", "Misused command. Please type /petm."));
				}
			} else {
				sender.sendMessage(
						chatHeader + lang.getString("misused-command", "Misused command. Please type /petm."));
			}
		}
		return true;
	}

	/**
	 * Try to hook up with Vault, and log if this is called on plugin initialisation.
	 * 
	 * @param log
	 * @return true if Vault available, false otherwise
	 */
	public boolean setUpEconomy() {
		if (economy != null) {
			return true;
		}

		try {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
					.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			return economy != null;
		} catch (NoClassDefFoundError e) {
			this.getLogger().warning("Attempt to hook up with Vault failed. Payment ignored.");
			return false;
		}
	}

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

	public boolean isActionBarMessage() {
		return actionBarMessage;
	}

	public String getChatHeader() {
		return chatHeader;
	}

	public Map<String, Player> getChangeOwnershipMap() {
		return changeOwnershipMap;
	}

	public Set<String> getFreePetSet() {
		return freePetSet;
	}

	public CommentedYamlConfiguration getPluginConfig() {
		return config;
	}

	public CommentedYamlConfiguration getPluginLang() {
		return lang;
	}

	public Economy getEconomy() {
		return economy;
	}
}
