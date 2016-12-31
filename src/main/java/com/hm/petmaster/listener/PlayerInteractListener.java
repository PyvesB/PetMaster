package com.hm.petmaster.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Llama;
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
import com.hm.mcshared.particle.ReflectionUtils.PackageType;
import com.hm.petmaster.PetMaster;
import com.hm.petmaster.event.PlayerChangeAnimalOwnershipEvent;

/**
 * Class used to display holograms or change the owner of a pet.
 * 
 * @author Pyves
 *
 */
public class PlayerInteractListener implements Listener {

	// Vertical offsets of the holograms for each mob type.
	private static final double DOG_OFFSET = 1.5;
	private static final double CAT_OFFSET = 1.42;
	private static final double HORSE_OFFSET = 2.32;
	private static final double LLAMA_OFFSET = 2.42;

	private final PetMaster plugin;
	private final int version;

	private int hologramDuration;
	private int changeOwnerPrice;
	private boolean displayDog;
	private boolean displayCat;
	private boolean displayHorse;
	private boolean displayLlama;

	public PlayerInteractListener(PetMaster petMaster) {

		this.plugin = petMaster;
		version = Integer.parseInt(PackageType.getServerVersion().split("_")[1]);
	}

	public void extractParameters() {

		displayDog = plugin.getPluginConfig().getBoolean("displayDog", true);
		displayCat = plugin.getPluginConfig().getBoolean("displayCat", true);
		displayHorse = plugin.getPluginConfig().getBoolean("displayHorse", true);
		displayLlama = plugin.getPluginConfig().getBoolean("displayLlama", true);
		hologramDuration = plugin.getPluginConfig().getInt("hologramDuration", 50);
		changeOwnerPrice = plugin.getPluginConfig().getInt("changeOwnerPrice", 0);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

		// On Minecraft versions from 1.9 onwards, this event is fired twice, one for each hand. Need additional check.
		if (version >= 9 && event.getHand() != EquipmentSlot.HAND) {
			return;
		}

		if (!(event.getRightClicked() instanceof Tameable) || ((Tameable) event.getRightClicked()).getOwner() == null
				|| plugin.isDisabled()) {
			return;
		}

		AnimalTamer owner = ((Tameable) event.getRightClicked()).getOwner();

		boolean wantsToChangeOwnership = plugin.getChangeOwnershipMap().containsKey(event.getPlayer().getName());

		// Do not show information to the owner of the pet.
		if (event.getPlayer().getName().equals(owner.getName()) && !wantsToChangeOwnership
				&& !"DarkPyves".equals(event.getPlayer().getName())) {
			return;
		}

		// Change owner of the pet.
		if (wantsToChangeOwnership) {
			changeOwner(event, owner);
			return;
		}

		// Display owner of the pet with a hologram and/or a message.
		if (event.getPlayer().hasPermission("petmaster.showowner")) {
			displayHologramAndMessage(event, owner);
		}
	}

	/**
	 * Display a hologram, and automatically delete it after a given delay.
	 * 
	 * @param event
	 * @param owner
	 */
	private void displayHologramAndMessage(PlayerInteractEntityEvent event, AnimalTamer owner) {

		if (plugin.isHologramMessage()) {
			Entity clickedAnimal = event.getRightClicked();

			double offset = HORSE_OFFSET;
			if (clickedAnimal instanceof Ocelot) {
				if (!displayCat || !event.getPlayer().hasPermission("petmaster.showowner.cat")) {
					return;
				}
				offset = CAT_OFFSET;
			} else if (clickedAnimal instanceof Wolf) {
				if (!displayDog || !event.getPlayer().hasPermission("petmaster.showowner.dog")) {
					return;
				}
				offset = DOG_OFFSET;
			} else if (version >= 11 && clickedAnimal instanceof Llama) {
				if (!displayLlama || !event.getPlayer().hasPermission("petmaster.showowner.llama")) {
					return;
				}
				offset = LLAMA_OFFSET;
			} else if (!displayHorse || !event.getPlayer().hasPermission("petmaster.showowner.horse")) {
				return;
			}

			Location eventLocation = clickedAnimal.getLocation();
			// Create location with offset.
			Location hologramLocation = new Location(eventLocation.getWorld(), eventLocation.getX(),
					eventLocation.getY() + offset, eventLocation.getZ());

			final Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);
			hologram.appendTextLine(
					ChatColor.GRAY + plugin.getPluginLang().getString("petmaster-hologram", "Pet owned by ")
							+ ChatColor.GOLD + owner.getName());

			// Runnable to delete hologram.
			new BukkitRunnable() {

				@Override
				public void run() {

					hologram.delete();
				}
			}.runTaskLater(plugin, hologramDuration);
		}

		if (plugin.isChatMessage()) {
			event.getPlayer().sendMessage(
					plugin.getChatHeader() + plugin.getPluginLang().getString("petmaster-chat", "Pet owned by ")
							+ ChatColor.GOLD + owner.getName());
		}
	}

	/**
	 * Change the owner of a pet. User must have entered the /pet setowner command beforehand and must be the owner of
	 * the pet unless he is admin.
	 * 
	 * @param event
	 * @param oldOwner
	 */
	@SuppressWarnings("deprecation")
	private void changeOwner(PlayerInteractEntityEvent event, AnimalTamer oldOwner) {

		// Retrieve new owner from the map and delete corresponding entry.
		Player newOwner = plugin.getChangeOwnershipMap().remove(event.getPlayer().getName());

		// Can only change ownership if current owner or bypass permission.
		if (oldOwner.getName().equals(event.getPlayer().getName())
				|| event.getPlayer().hasPermission("petmaster.admin")) {
			// Change owner.
			Tameable tameableAnimal = (Tameable) event.getRightClicked();
			tameableAnimal.setOwner(newOwner);

			// Charge price.
			if (changeOwnerPrice > 0 && plugin.setUpEconomy()) {
				try {
					plugin.getEconomy().depositPlayer(event.getPlayer(), changeOwnerPrice);
				} catch (NoSuchMethodError e) {
					// Deprecated method, but was the only one existing prior to Vault 1.4.
					plugin.getEconomy().depositPlayer(event.getPlayer().getName(), changeOwnerPrice);
				}
				// If player has set different currency names depending on amount, adapt message accordingly.
				if (changeOwnerPrice > 1) {
					event.getPlayer().sendMessage(plugin.getChatHeader() + ChatColor.translateAlternateColorCodes('&',
							plugin.getPluginLang().getString("change-owner-price", "You payed: AMOUNT !").replace(
									"AMOUNT", changeOwnerPrice + " " + plugin.getEconomy().currencyNamePlural())));
				} else {
					event.getPlayer().sendMessage(plugin.getChatHeader() + ChatColor.translateAlternateColorCodes('&',
							plugin.getPluginLang().getString("change-owner-price", "You payed: AMOUNT !").replace(
									"AMOUNT", changeOwnerPrice + " " + plugin.getEconomy().currencyNameSingular())));
				}
			}

			event.getPlayer().sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("owner-changed", "Say goodbye: this pet is no longer yours!"));
			newOwner.sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("new-owner", "Player PLAYER gave you ownership of his pet!")
							.replace("PLAYER", event.getPlayer().getName()));

			// Create new event to allow other plugins to be aware of the ownership change.
			PlayerChangeAnimalOwnershipEvent playerChangeAnimalOwnershipEvent = new PlayerChangeAnimalOwnershipEvent(
					oldOwner, newOwner, tameableAnimal);
			Bukkit.getServer().getPluginManager().callEvent(playerChangeAnimalOwnershipEvent);
		} else {
			event.getPlayer().sendMessage(plugin.getChatHeader() + plugin.getPluginLang()
					.getString("not-owner", "You do not own this pet!").replace("PLAYER", event.getPlayer().getName()));
		}
	}
}
