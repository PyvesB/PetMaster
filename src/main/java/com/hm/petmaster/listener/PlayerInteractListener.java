package com.hm.petmaster.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.hm.petmaster.PetMaster;
import com.hm.petmaster.language.Lang;

public class PlayerInteractListener implements Listener {

	private PetMaster plugin;

	public PlayerInteractListener(PetMaster petMaster) {

		this.plugin = petMaster;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

		if (!(event.getRightClicked() instanceof Tameable))
			return;

		Player owner;

		if (!(((Tameable) event.getRightClicked()).getOwner() instanceof Player))
			return;

		owner = (Player) ((Tameable) event.getRightClicked()).getOwner();

		// Do not show information to the owner of the pet.
		 if (event.getPlayer().equals(owner))
		 return;

		if (plugin.isUseHolographicDisplays() && plugin.isHoologramMessage()) {
			Location eventLocation = event.getRightClicked().getLocation();
			Location hologramLocation = new Location(eventLocation.getWorld(), eventLocation.getX(),
					eventLocation.getY() + 1.6, eventLocation.getZ());
			final Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);
			hologram.appendTextLine(ChatColor.GRAY + Lang.PETMASTER_HOLOGRAM.toString() + ChatColor.GOLD
					+ owner.getName());

			// Runnable to delete hologram.
			new BukkitRunnable() {

				@Override
				public void run() {

					hologram.delete();
				}
			}.runTaskLater(plugin, plugin.getHologramDuration());
		}

		if (plugin.isChatMessage())
			event.getPlayer().sendMessage(
					plugin.getChatHeader() + Lang.PETMASTER_CHAT + ChatColor.GOLD + owner.getName());

	}

}
