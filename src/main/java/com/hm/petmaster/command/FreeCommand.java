package com.hm.petmaster.command;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
	private final Set<UUID> freePetSet;

	public FreeCommand(PetMaster plugin) {
		this.plugin = plugin;
		freePetSet = new HashSet<>();
	}

	public void freePet(Player player, String[] args) {
		if (args.length == 1) {
			if (!player.hasPermission("petmaster.free")) {
				plugin.getMessageSender().sendMessage(player, "no-permissions");
			} else if (plugin.getEnableDisableCommand().isDisabled()) {
				plugin.getMessageSender().sendMessage(player, "currently-disabled");
			} else {
				freePetSet.add(player.getUniqueId());
				plugin.getMessageSender().sendMessage(player, "right-click");
				// Cancel previous pending operation.
				plugin.getSetOwnerCommand().collectPendingSetOwnershipRequest(player);
			}
		} else {
			plugin.getMessageSender().sendMessage(player, "misused-command");
		}
	}

	public boolean collectPendingFreeRequest(Player player) {
		return freePetSet.remove(player.getUniqueId());
	}
}
