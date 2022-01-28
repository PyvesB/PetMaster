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
				plugin.getMessageSender().sendMessage(sender, "petmaster-disabled");
			} else {
				plugin.getMessageSender().sendMessage(sender, "petmaster-enabled");
			}
		} else {
			plugin.getMessageSender().sendMessage(sender, "no-permissions");
		}
	}

	public boolean isDisabled() {
		return disabled;
	}
}
