package com.hm.petmaster.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.hm.petmaster.PetMaster;

public class PlayerQuitListener implements Listener {

	private PetMaster plugin;

	public PlayerQuitListener(PetMaster plugin) {

		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {

		plugin.getChangeOwnershipMap().remove(event.getPlayer().getName());
	}

}
