package com.hm.petmaster.command;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import com.hm.petmaster.PetMaster;

/**
 * Class in charge of handling player requests to free a pet (/petm free).
 * 
 * @author Pyves
 * 
 */
public class FreeCommand {

	private final PetMaster plugin;
	// Contains names of owners wanting to free their pets.
	private final Set<String> freePetSet;

	public FreeCommand(PetMaster plugin) {
		this.plugin = plugin;
		freePetSet = new HashSet<>();
	}

	public void freePet(Player player, String[] args) {
		if (args.length == 1) {
			if (!player.hasPermission("petmaster.free")) {
				player.sendMessage(plugin.getChatHeader() + plugin.getPluginLang().getString("no-permissions",
						"You do not have the permission to do this."));
			} else if (plugin.getEnableDisableCommand().isDisabled()) {
				player.sendMessage(plugin.getChatHeader() + plugin.getPluginLang().getString("currently-disabled",
						"PetMaster is currently disabled, you cannot use this command."));
			} else {
				freePetSet.add(player.getName());
				player.sendMessage(plugin.getChatHeader()
						+ plugin.getPluginLang().getString("right-click", "Right click on a pet to change its owner!"));
				// Cancel previous pending operation.
				plugin.getSetOwnerCommand().collectPendingSetOwnershipRequest(player);
			}
		} else {
			player.sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("misused-command", "Misused command. Please type /petm."));
		}
	}

	public boolean collectPendingFreeRequest(Player player) {
		return freePetSet.remove(player.getName());
	}
}
