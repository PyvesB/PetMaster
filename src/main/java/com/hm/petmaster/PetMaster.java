package com.hm.petmaster;

import java.io.IOException;
import java.util.logging.Level;

import com.hm.petmaster.listener.PlayerAttackListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.hm.mcshared.file.CommentedYamlConfiguration;
import com.hm.mcshared.update.UpdateChecker;
import com.hm.petmaster.command.EnableDisableCommand;
import com.hm.petmaster.command.FreeCommand;
import com.hm.petmaster.command.HelpCommand;
import com.hm.petmaster.command.InfoCommand;
import com.hm.petmaster.command.ReloadCommand;
import com.hm.petmaster.command.SetOwnerCommand;
import com.hm.petmaster.listener.PlayerInteractListener;
import com.hm.petmaster.listener.PlayerQuitListener;

/**
 * Whose pet is this? Manage pets and display information via holograms, action bar or chat messages!
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
 * @version 1.10.0
 * @author DarkPyves
 */
public class PetMaster extends JavaPlugin {

	// Plugin options and various parameters.
	private String chatHeader;
	private boolean successfulLoad;
	private boolean updatePerformed;

	// Fields related to file handling.
	private CommentedYamlConfiguration config;
	private CommentedYamlConfiguration lang;

	// Plugin listeners.
	private PlayerInteractListener playerInteractListener;
	private PlayerQuitListener playerQuitListener;
	private PlayerAttackListener playerAttackListener;

	// Used to check for plugin updates.
	private UpdateChecker updateChecker;

	// Additional classes related to plugin commands.
	private HelpCommand helpCommand;
	private InfoCommand infoCommand;
	private SetOwnerCommand setOwnerCommand;
	private FreeCommand freeCommand;
	private EnableDisableCommand enableDisableCommand;
	private ReloadCommand reloadCommand;

	/**
	 * Called when server is launched or reloaded.
	 */
	@Override
	public void onEnable() {
		// Start enabling plugin.
		long startTime = System.currentTimeMillis();

		getLogger().info("Registering listeners...");

		playerInteractListener = new PlayerInteractListener(this);
		playerQuitListener = new PlayerQuitListener(this);

		PluginManager pm = getServer().getPluginManager();
		// Register listeners.
		pm.registerEvents(playerInteractListener, this);
		pm.registerEvents(playerQuitListener, this);

		extractParametersFromConfig(true);

		chatHeader = ChatColor.GRAY + "[" + ChatColor.GOLD + "\u265E" + ChatColor.GRAY + "] ";

		helpCommand = new HelpCommand(this);
		infoCommand = new InfoCommand(this);
		setOwnerCommand = new SetOwnerCommand(this);
		freeCommand = new FreeCommand(this);
		enableDisableCommand = new EnableDisableCommand(this);
		reloadCommand = new ReloadCommand(this);

		if (successfulLoad) {
			getLogger().info("Plugin successfully enabled and ready to run! Took "
					+ (System.currentTimeMillis() - startTime) + "ms.");
		} else {
			getLogger().severe("Error(s) while loading plugin. Please view previous logs for more information.");
		}
	}

	/**
	 * Extracts plugin parameters from the configuration file.
	 * 
	 * @param attemptUpdate
	 */
	public void extractParametersFromConfig(boolean attemptUpdate) {
		successfulLoad = true;

		getLogger().info("Backing up and loading configuration files...");

		config = loadAndBackupYamlConfiguration("config.yml");
		if (config == null) {
			return;
		}

		lang = loadAndBackupYamlConfiguration(config.getString("languageFileName", "lang.yml"));
		if (lang == null) {
			return;
		}

		// Update configurations from previous versions of the plugin if server reloads or restarts.
		if (attemptUpdate) {
			updateOldConfiguration();
			updateOldLanguage();
		}

		playerInteractListener.extractParameters();

		if (config.getBoolean("checkForUpdate", true)) {
			if (updateChecker == null) {
				updateChecker = new UpdateChecker(this, "https://raw.githubusercontent.com/PyvesB/PetMaster/master/pom.xml",
						"petmaster.admin", chatHeader, "spigotmc.org/resources/pet-master.15904");
				getServer().getPluginManager().registerEvents(updateChecker, this);
				updateChecker.launchUpdateCheckerTask();
			}
		} else {
			PlayerJoinEvent.getHandlerList().unregister(updateChecker);
			updateChecker = null;
		}

		if (config.getBoolean("disablePlayerDamage", false)) {
			if (playerAttackListener == null) {
				playerAttackListener = new PlayerAttackListener(this);
				getServer().getPluginManager().registerEvents(playerAttackListener, this);
				playerAttackListener.extractParameters();
			}

		} else {
			if (playerAttackListener != null) {
				HandlerList.unregisterAll(playerAttackListener);
				playerAttackListener = null;
			}
		}
	}

	/**
	 * Loads and backs up file fileName.
	 * 
	 * @param fileName
	 * @return the loaded CommentedYamlConfiguration
	 */
	private CommentedYamlConfiguration loadAndBackupYamlConfiguration(String fileName) {
		CommentedYamlConfiguration yamlConfiguration = new CommentedYamlConfiguration(fileName, this);
		try {
			yamlConfiguration.loadConfiguration();
		} catch (IOException | InvalidConfigurationException e) {
			getLogger().severe("Error while loading " + fileName + " file, disabling plugin.");
			getLogger().log(Level.SEVERE,
					"Verify your syntax by visiting yaml-online-parser.appspot.com and using the following logs: ", e);
			successfulLoad = false;
			getServer().getPluginManager().disablePlugin(this);
		}

		try {
			yamlConfiguration.backupConfiguration();
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Error while backing up configuration file: ", e);
			successfulLoad = false;
		}
		return yamlConfiguration;
	}

	/**
	 * Updates configuration file from older plugin versions by adding missing parameters. Upgrades from versions prior
	 * to 1.2 are not supported.
	 */
	private void updateOldConfiguration() {
		updatePerformed = false;

		updateSetting(config, "languageFileName", "lang.yml", "Name of the language file.");
		updateSetting(config, "checkForUpdate", true,
				"Check for update on plugin launch and notify when an OP joins the game.");
		updateSetting(config, "changeOwnerPrice", 0, "Price of the /petm setowner command (requires Vault).");
		updateSetting(config, "displayDog", true, "Take dogs into account.");
		updateSetting(config, "displayCat", true, "Take cats into account.");
		updateSetting(config, "displayHorse", true, "Take horses into account.");
		updateSetting(config, "displayLlama", true, "Take llamas into account.");
		updateSetting(config, "displayParrot", true, "Take parrots into account.");
		updateSetting(config, "actionBarMessage", false,
				"Enable or disable action bar messages when right-clicking on a pet.");
		updateSetting(config, "displayToOwner", false,
				"Enable or disable showing ownership information for a player's own pets.");
		updateSetting(config, "freePetPrice", 0, "Price of the /petm free command (requires Vault).");
		updateSetting(config, "showHealth", true,
				"Show health next to owner in chat and action bar messages (not holograms).");
		updateSetting(config, "disablePlayerDamage", false, "Protect pets to avoid being hurt by other player.");
		updateSetting(config, "enableAngryMobPlayerDamage", true, "Allows players to defend themselves against angry tamed mobs (e.g. dogs) even if disablePlayerDamage is true.");

		if (updatePerformed) {
			// Changes in the configuration: save and do a fresh load.
			try {
				config.saveConfiguration();
				config.loadConfiguration();
			} catch (IOException | InvalidConfigurationException e) {
				getLogger().log(Level.SEVERE, "Error while saving changes to the configuration file: ", e);
				successfulLoad = false;
			}
		}
	}

	/**
	 * Updates language file from older plugin versions by adding missing parameters. Upgrades from versions prior to
	 * 1.2 are not supported.
	 */
	private void updateOldLanguage() {
		updatePerformed = false;

		updateSetting(lang, "petmaster-command-setowner-hover",
				"You can only change the ownership of your own pets, unless you're admin!");
		updateSetting(lang, "petmaster-command-disable-hover",
				"The plugin will not work until next reload or /petm enable.");
		updateSetting(lang, "petmaster-command-enable-hover",
				"Plugin enabled by default. Use this if you entered /petm disable before!");
		updateSetting(lang, "petmaster-command-reload-hover", "Reload most settings in config.yml and lang.yml files.");
		updateSetting(lang, "petmaster-command-info-hover", "Some extra info about the plugin and its awesome author!");
		updateSetting(lang, "petmaster-tip", "&lHINT&r &8You can &7&n&ohover&r &8or &7&n&oclick&r &8on the commands!");
		updateSetting(lang, "change-owner-price", "You payed: AMOUNT!");
		updateSetting(lang, "petmaster-action-bar", "Pet owned by ");
		updateSetting(lang, "petmaster-command-free", "Free a pet.");
		updateSetting(lang, "petmaster-command-free-hover", "You can only free your own pets, unless you're admin!");
		updateSetting(lang, "pet-freed", "Say goodbye: this pet returned to the wild!");
		updateSetting(lang, "not-enough-money", "You do not have the required amount: AMOUNT!");
		updateSetting(lang, "currently-disabled", "PetMaster is currently disabled, you cannot use this command.");
		updateSetting(lang, "petmaster-health", "Health: ");

		if (updatePerformed) {
			// Changes in the language file: save and do a fresh load.
			try {
				lang.saveConfiguration();
				lang.loadConfiguration();
			} catch (IOException | InvalidConfigurationException e) {
				getLogger().log(Level.SEVERE, "Error while saving changes to the language file: ", e);
				successfulLoad = false;
			}
		}
	}

	/**
	 * Called when server is stopped or reloaded.
	 */
	@Override
	public void onDisable() {
		getLogger().info("PetMaster has been disabled.");
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
			reloadCommand.reload(sender);
		} else if ("disable".equalsIgnoreCase(args[0])) {
			enableDisableCommand.setState(sender, true);
		} else if ("enable".equalsIgnoreCase(args[0])) {
			enableDisableCommand.setState(sender, false);
		} else if ("setowner".equalsIgnoreCase(args[0]) && sender instanceof Player) {
			setOwnerCommand.setOwner(((Player) sender), args);
		} else if ("free".equalsIgnoreCase(args[0]) && sender instanceof Player) {
			freeCommand.freePet(((Player) sender), args);
		} else {
			sender.sendMessage(chatHeader + lang.getString("misused-command", "Misused command. Please type /petm."));
		}
		return true;
	}

	/**
	 * Updates the configuration file to include a new setting with its default value and its comments.
	 * 
	 * @param file
	 * @param name
	 * @param value
	 * @param comments
	 */
	private void updateSetting(CommentedYamlConfiguration file, String name, Object value, String... comments) {
		if (!file.getKeys(false).contains(name)) {
			file.set(name, value, comments);
			updatePerformed = true;
		}
	}

	public void setSuccessfulLoad(boolean successfulLoad) {
		this.successfulLoad = successfulLoad;
	}

	public boolean isSuccessfulLoad() {
		return successfulLoad;
	}

	public String getChatHeader() {
		return chatHeader;
	}

	public CommentedYamlConfiguration getPluginConfig() {
		return config;
	}

	public CommentedYamlConfiguration getPluginLang() {
		return lang;
	}

	public SetOwnerCommand getSetOwnerCommand() {
		return setOwnerCommand;
	}

	public FreeCommand getFreeCommand() {
		return freeCommand;
	}

	public EnableDisableCommand getEnableDisableCommand() {
		return enableDisableCommand;
	}
}
