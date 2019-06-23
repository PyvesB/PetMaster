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
				player.sendMessage(plugin.getChatHeader()
						+ plugin.getPluginLang().getString("player-offline", "The specified player is offline!"));
			} else if (!player.hasPermission("petmaster.setowner")) {
				player.sendMessage(plugin.getChatHeader() + plugin.getPluginLang().getString("no-permissions",
						"You do not have the permission to do this."));
			} else if (plugin.getEnableDisableCommand().isDisabled()) {
				player.sendMessage(plugin.getChatHeader() + plugin.getPluginLang().getString("currently-disabled",
						"PetMaster is currently disabled, you cannot use this command."));
			} else if (!player.hasPermission("petmaster.admin") && newOwner.getUniqueId().equals(player.getUniqueId())) {
				player.sendMessage(plugin.getChatHeader() + plugin.getPluginLang()
						.getString("cannot-change-to-yourself", "You cannot change the owner to yourself!"));
			} else {
				changeOwnershipMap.put(player.getUniqueId(), newOwner);
				player.sendMessage(plugin.getChatHeader()
						+ plugin.getPluginLang().getString("right-click", "Right click on a pet to change its owner!"));
				// Cancel previous pending operation.
				plugin.getFreeCommand().collectPendingFreeRequest(player);
			}
		} else {
			player.sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("misused-command", "Misused command. Please type /petm."));
		}
	}

	public Player collectPendingSetOwnershipRequest(Player player) {
		return changeOwnershipMap.remove(player.getUniqueId());
	}
}
