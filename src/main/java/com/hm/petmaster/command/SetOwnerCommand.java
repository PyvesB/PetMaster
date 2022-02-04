package com.hm.petmaster.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hm.petmaster.PetMaster;

/**
 * Class in charge of handling player requests to change ownership of a pet (/petm setowner).
 * 
 * @author Pyves
 * 
 */
public class SetOwnerCommand {

	private final PetMaster plugin;
	// Contains pairs with name of previous owner and new owner.
	private final Map<UUID, Player> changeOwnershipMap;

	public SetOwnerCommand(PetMaster plugin) {
		this.plugin = plugin;
		changeOwnershipMap = new HashMap<>();
	}

	public void setOwner(Player player, String[] args) {
		if (args.length == 2) {
			Player newOwner = null;
			for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
				if (currentPlayer.getName().equalsIgnoreCase(args[1])) {
					newOwner = currentPlayer;
					break;
				}
			}
			if (newOwner == null) {
				plugin.getMessageSender().sendMessage(player, "player-offline");
			} else if (!player.hasPermission("petmaster.setowner")) {
				plugin.getMessageSender().sendMessage(player, "no-permissions");
			} else if (plugin.getEnableDisableCommand().isDisabled()) {
				plugin.getMessageSender().sendMessage(player, "currently-disabled");
			} else if (!player.hasPermission("petmaster.admin") && newOwner.getUniqueId().equals(player.getUniqueId())) {
				plugin.getMessageSender().sendMessage(player, "cannot-change-to-yourself");
			} else {
				changeOwnershipMap.put(player.getUniqueId(), newOwner);
				plugin.getMessageSender().sendMessage(player, "right-click");
				// Cancel previous pending operation.
				plugin.getFreeCommand().collectPendingFreeRequest(player);
			}
		} else {
			plugin.getMessageSender().sendMessage(player, "misused-command");
		}
	}

	public Player collectPendingSetOwnershipRequest(Player player) {
		return changeOwnershipMap.remove(player.getUniqueId());
	}
}
