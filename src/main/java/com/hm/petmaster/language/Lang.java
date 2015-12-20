package com.hm.petmaster.language;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
 
/**
* An enum for requesting strings from the language file.
* @author gomeow
*/
public enum Lang {
    
    CONFIGURATION_SUCCESSFULLY_RELOADED("configuration-successfully-reloaded", "Configuration successfully reloaded."),
    CONFIGURATION_RELOAD_FAILED("configuration-reload-failed", "Errors while reloading configuration. Please view logs for more details."),
    NO_PERMS("no-permissions", "You do not have the permission to do this."),
    PETMASTER_DISABLED("petmaster-disabled", "PetMaster disabled till next reload or /petm enable."),
    PETMASTER_ENABLED("petmaster-enabled", "PetMaster enabled."),
    PETMASTER_HOLOGRAM("petmaster-hologram", "Pet owned by "),
    PETMASTER_CHAT("petmaster-chat", "Pet owned by "),
    MISUSED_COMMAND("misused-command","Misused command. Please type /petm."),
    PETMASTER_COMMAND_DISABLE("petmaster-command-disable", "Disable plugin till next reload."),
    PETMASTER_COMMAND_ENABLE("petmaster-command-enable", "Enable plugin (if previously disabled)."),
    PETMASTER_COMMAND_RELOAD("petmaster-command-reload", "Reload the plugin's configuration."),
    PETMASTER_COMMAND_INFO("petmaster-command-info", "Display various information about the plugin."),
    VERSION_COMMAND_NAME("version-command-name", "Name:"),
    VERSION_COMMAND_VERSION("version-command-version", "Version:"),
    VERSION_COMMAND_WEBSITE("version-command-website", "Website:"),
    VERSION_COMMAND_AUTHOR("version-command-author", "Author:"),
    VERSION_COMMAND_DESCRIPTION("version-command-description", "Description:"),
    VERSION_COMMAND_DESCRIPTION_DETAILS("version-command-description-details", "Who's pet is this? Pet Master, a simple plugin to display the owner of a pet via an hologram or a chat message."),
    VERSION_COMMAND_ENABLED("version-command-enabled", "Plugin enabled:");
 
    private String path;
    private String def;
    private static YamlConfiguration LANG;
 
    /**
    * Lang enum constructor.
    * @param path The string path.
    * @param start The default string.
    */
    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }
 
    /**
    * Set the {@code YamlConfiguration} to use.
    * @param config The config to set.
    */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }
 
    @Override
    public String toString() {        
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }
 
    /**
    * Get the default value of the path.
    * @return The default value of the path.
    */
    public String getDefault() {
        return this.def;
    }
 
    /**
    * Get the path to the string.
    * @return The path to the string.
    */
    public String getPath() {
        return this.path;
    }
}