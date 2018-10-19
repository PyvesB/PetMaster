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

	private final PetMaster plugin;
	private boolean disabled;

	public EnableDisableCommand(PetMaster plugin) {
		this.plugin = plugin;
		disabled = false;
	}

	public void setState(CommandSender sender, boolean disabled) {
		if (sender.hasPermission("petmaster.admin")) {
			this.disabled = disabled;
			if (disabled) {
				sender.sendMessage(plugin.getChatHeader() + plugin.getPluginLang().getString("petmaster-disabled",
						"PetMaster disabled till next reload or /petm enable."));
			} else {
				sender.sendMessage(plugin.getChatHeader()
						+ plugin.getPluginLang().getString("petmaster-enabled", "PetMaster enabled."));
			}
		} else {
			sender.sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("no-permissions", "You do not have the permission to do this."));
		}
	}

	public boolean isDisabled() {
		return disabled;
	}
}
