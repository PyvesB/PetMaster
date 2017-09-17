package com.hm.petmaster.command;

import org.bukkit.command.CommandSender;

import com.hm.petmaster.PetMaster;

/**
 * Class in charge of enabling or disabling the plugin on the fly (/petm enable or /petm disable).
 * 
 * @author Pyves
 * 
 */
public class EnableDisableCommand {

	private PetMaster plugin;
	private boolean enabled;

	public EnableDisableCommand(PetMaster plugin) {
		this.plugin = plugin;
		enabled = true;
	}

	public void setState(CommandSender sender, boolean enabled) {
		if (sender.hasPermission("petmaster.admin")) {
			this.enabled = enabled;
			if (enabled) {
				sender.sendMessage(plugin.getChatHeader()
						+ plugin.getPluginLang().getString("petmaster-enabled", "PetMaster enabled."));
			} else {
				sender.sendMessage(plugin.getChatHeader() + plugin.getPluginLang().getString("petmaster-disabled",
						"PetMaster disabled till next reload or /petm enable."));
			}
		} else {
			sender.sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("no-permissions", "You do not have the permission to do this."));
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}
