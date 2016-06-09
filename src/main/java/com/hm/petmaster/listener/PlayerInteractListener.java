package com.hm.petmaster.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

		// On Minecraft versions from 1.9 onwards, this event is fired twice,
		// one for each hand. Need additional check.
		if ((Integer.valueOf(Bukkit.getBukkitVersion().charAt(2) + "") >= 9 || Bukkit.getBukkitVersion().charAt(3) != '.') && event.getHand() != EquipmentSlot.HAND)
			return;

		if (!(event.getRightClicked() instanceof Tameable) || ((Tameable) event.getRightClicked()).getOwner() == null
				|| !event.getPlayer().hasPermission("petmaster.use") || plugin.isDisabled())
			return;

		String owner = ((Tameable) event.getRightClicked()).getOwner().getName();

		// Do not show information to the owner of the pet.
		if (event.getPlayer().getName().equals(owner)
				&& !plugin.getChangeOwnershipMap().containsKey(event.getPlayer().getName())) {
			return;
		}

		// Change owner of the pet.
		if (plugin.getChangeOwnershipMap().containsKey(event.getPlayer().getName())) {
			Player newOwner = plugin.getChangeOwnershipMap().remove(event.getPlayer().getName());
			// Can only change ownership if current owner or bypass permission.
			if (owner.equals(event.getPlayer().getName()) || event.getPlayer().hasPermission("petmaster.admin")) {
				((Tameable) event.getRightClicked()).setOwner(newOwner);
				event.getPlayer().sendMessage(plugin.getChatHeader() + Lang.OWNER_CHANGED);
				newOwner.sendMessage(plugin.getChatHeader()
						+ Lang.NEW_OWNER.toString().replaceAll("PLAYER", event.getPlayer().getName()));
			} else {
				event.getPlayer().sendMessage(plugin.getChatHeader() + Lang.NOT_OWNER);
			}
			return;
		}

		// Display owner of the pet.
		if (plugin.isUseHolographicDisplays() && plugin.isHologramMessage()) {
			double offset = 1.5;
			if (event.getRightClicked() instanceof Ocelot)
				offset = 1.42;
			else if (event.getRightClicked() instanceof Horse)
				offset = 2.32;
			Location eventLocation = event.getRightClicked().getLocation();
			Location hologramLocation = new Location(eventLocation.getWorld(), eventLocation.getX(),
					eventLocation.getY() + offset, eventLocation.getZ());
			final Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);
			hologram.appendTextLine(ChatColor.GRAY + Lang.PETMASTER_HOLOGRAM.toString() + ChatColor.GOLD + owner);

			// Runnable to delete hologram.
			new BukkitRunnable() {

				@Override
				public void run() {

					hologram.delete();
				}
			}.runTaskLater(plugin, plugin.getHologramDuration());
		}

		if (plugin.isChatMessage())
			event.getPlayer().sendMessage(plugin.getChatHeader() + Lang.PETMASTER_CHAT + ChatColor.GOLD + owner);

	}

}
