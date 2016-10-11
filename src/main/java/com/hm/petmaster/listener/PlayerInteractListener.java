package com.hm.petmaster.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.hm.petmaster.PetMaster;

/**
 * Class used to display holograms or change the owner of a pet.
 * 
 * @author Pyves
 *
 */
public class PlayerInteractListener implements Listener {

	private PetMaster plugin;
	// Vertical offsets of the holograms for each mob type.
	final static double DOG_OFFSET = 1.5;
	final static double CAT_OFFSET = 1.42;
	final static double HORSE_OFFSET = 2.32;

	public PlayerInteractListener(PetMaster petMaster) {

		this.plugin = petMaster;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

		// On Minecraft versions from 1.9 onwards, this event is fired twice,
		// one for each hand. Need additional check.
		if ((Integer.parseInt(Bukkit.getBukkitVersion().charAt(2) + "") >= 9
				|| Bukkit.getBukkitVersion().charAt(3) != '.') && event.getHand() != EquipmentSlot.HAND)
			return;

		if (!(event.getRightClicked() instanceof Tameable) || ((Tameable) event.getRightClicked()).getOwner() == null
				|| !event.getPlayer().hasPermission("petmaster.use") || plugin.isDisabled())
			return;

		AnimalTamer owner = ((Tameable) event.getRightClicked()).getOwner();

		// Do not show information to the owner of the pet.
		if (event.getPlayer().getName().equals(owner.getName())
				&& !plugin.getChangeOwnershipMap().containsKey(event.getPlayer().getName())) {
			return;
		}

		// Change owner of the pet.
		if (plugin.getChangeOwnershipMap().containsKey(event.getPlayer().getName())) {
			changeOwner(event, owner);
			return;
		}

		// Display owner of the pet with a hologram.
		if (plugin.isHologramMessage()) {
			displayHologram(event, owner);
		}

		if (plugin.isChatMessage())
			event.getPlayer().sendMessage(
					plugin.getChatHeader() + plugin.getPluginLang().getString("petmaster-chat", "Pet owned by ")
							+ ChatColor.GOLD + owner.getName());

	}

	/**
	 * Display a hologram, and automatically delete it after a given delay.
	 * 
	 * @param event
	 * @param owner
	 */
	private void displayHologram(PlayerInteractEntityEvent event, AnimalTamer owner) {

		double offset = HORSE_OFFSET;
		if (event.getRightClicked() instanceof Ocelot)
			offset = CAT_OFFSET;
		else if (event.getRightClicked() instanceof Wolf)
			offset = DOG_OFFSET;

		Location eventLocation = event.getRightClicked().getLocation();
		// Create location with offset.
		Location hologramLocation = new Location(eventLocation.getWorld(), eventLocation.getX(),
				eventLocation.getY() + offset, eventLocation.getZ());

		final Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);
		hologram.appendTextLine(ChatColor.GRAY + plugin.getPluginLang().getString("petmaster-hologram", "Pet owned by ")
				+ ChatColor.GOLD + owner.getName());

		// Runnable to delete hologram.
		new BukkitRunnable() {

			@Override
			public void run() {

				hologram.delete();
			}
		}.runTaskLater(plugin, plugin.getHologramDuration());
	}

	/**
	 * Change the owner of a pet. User must have entered the /pet setowner command beforehand and must be the owner of
	 * the pet unless he is admin.
	 * 
	 * @param event
	 * @param owner
	 */
	@SuppressWarnings("deprecation")
	private void changeOwner(PlayerInteractEntityEvent event, AnimalTamer owner) {

		// Retrieve new owner from the map and delete corresponding entry.
		Player newOwner = plugin.getChangeOwnershipMap().remove(event.getPlayer().getName());

		// Can only change ownership if current owner or bypass permission.
		if (owner.getName().equals(event.getPlayer().getName()) || event.getPlayer().hasPermission("petmaster.admin")) {
			// Change owner.
			((Tameable) event.getRightClicked()).setOwner(newOwner);

			// Charge price.
			if (plugin.getChangeOwnerPrice() > 0 && plugin.setUpEconomy()) {
				try {
					plugin.getEconomy().depositPlayer(event.getPlayer(), plugin.getChangeOwnerPrice());
				} catch (NoSuchMethodError e) {
					// Deprecated method, but was the only one existing prior to Vault 1.4.
					plugin.getEconomy().depositPlayer(event.getPlayer().getName(), plugin.getChangeOwnerPrice());
				}
				// If player has set different currency names depending on amount,
				// adapt message accordingly.
				if (plugin.getChangeOwnerPrice() > 1)
					event.getPlayer().sendMessage(plugin.getChatHeader() + ChatColor.translateAlternateColorCodes('&',
							plugin.getPluginLang().getString("change-owner-price", "You payed: AMOUNT !").replace(
									"AMOUNT", plugin.getChangeOwnerPrice() + " "
											+ plugin.getEconomy().currencyNamePlural())));
				else
					event.getPlayer().sendMessage(plugin.getChatHeader() + ChatColor.translateAlternateColorCodes('&',
							plugin.getPluginLang().getString("change-owner-price", "You payed: AMOUNT !").replace(
									"AMOUNT", plugin.getChangeOwnerPrice() + " "
											+ plugin.getEconomy().currencyNameSingular())));
			}

			event.getPlayer().sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("owner-changed", "Say goodbye: this pet is no longer yours!"));
			newOwner.sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("new-owner", "Player PLAYER gave you ownership of his pet!")
							.replace("PLAYER", event.getPlayer().getName()));
		} else {
			event.getPlayer().sendMessage(plugin.getChatHeader() + plugin.getPluginLang()
					.getString("not-owner", "You do not own this pet!").replace("PLAYER", event.getPlayer().getName()));
		}
	}

}
