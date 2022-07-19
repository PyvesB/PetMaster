package com.hm.petmaster.listener;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.hm.mcshared.event.PlayerChangeAnimalOwnershipEvent;
import com.hm.petmaster.PetMaster;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to display holograms, change the owner of a pet or free a pet.
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
	private static final double PARROT_OFFSET = 1.15;

	private final PetMaster plugin;
	private Economy economy;

	// Configuration parameters.
	private boolean chatMessage;
	private boolean hologramMessage;
	private boolean actionBarMessage;
	private boolean displayDog;
	private boolean displayCat;
	private boolean displayHorse;
	private boolean displayLlama;
	private boolean displayParrot;
	private boolean displayToOwner;
	private int hologramDuration;
	private int changeOwnerPrice;
	private int freePetPrice;
	private boolean showHealth;
	private boolean disableRiding;

	public PlayerInteractListener(PetMaster petMaster) {
		this.plugin = petMaster;
		// Try to retrieve an Economy instance from Vault.
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager()
					.getRegistration(Economy.class);
			if (rsp != null) {
				economy = rsp.getProvider();
			}
		}
	}

	public void extractParameters() {
		displayDog = plugin.getPluginConfig().getBoolean("displayDog", true);
		displayCat = plugin.getPluginConfig().getBoolean("displayCat", true);
		displayHorse = plugin.getPluginConfig().getBoolean("displayHorse", true);
		displayLlama = plugin.getPluginConfig().getBoolean("displayLlama", true);
		displayParrot = plugin.getPluginConfig().getBoolean("displayParrot", true);
		displayToOwner = plugin.getPluginConfig().getBoolean("displayToOwner", false);
		hologramDuration = plugin.getPluginConfig().getInt("hologramDuration", 50);
		changeOwnerPrice = plugin.getPluginConfig().getInt("changeOwnerPrice", 0);
		freePetPrice = plugin.getPluginConfig().getInt("freePetPrice", 0);
		chatMessage = plugin.getPluginConfig().getBoolean("chatMessage", true);
		hologramMessage = plugin.getPluginConfig().getBoolean("hologramMessage", false);
		actionBarMessage = plugin.getPluginConfig().getBoolean("actionBarMessage", true);
		showHealth = plugin.getPluginConfig().getBoolean("showHealth", true);
		disableRiding = plugin.getPluginConfig().getBoolean("disableRiding", false);

		boolean holographicDisplaysAvailable = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
		// Checking whether user configured plugin to display hologram but HolographicsDisplays not available.
		if (hologramMessage && !holographicDisplaysAvailable) {
			hologramMessage = false;
			actionBarMessage = true;
			plugin.getLogger().warning(
					"HolographicDisplays was not found; disabling usage of holograms and enabling action bar messages.");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		if (shouldHandleEvent(event)) {
			Tameable tameable = (Tameable) event.getRightClicked();
			AnimalTamer currentOwner = tameable.getOwner();
			if (currentOwner == null || currentOwner.getName() == null) {
				return;
			}
			// Has the player clicked on one of his own pets?
			Player player = event.getPlayer();
			boolean isOwner = player.getUniqueId().equals(currentOwner.getUniqueId());
			// Retrieve new owner from the map and delete corresponding entry.
			Player newOwner = plugin.getSetOwnerCommand().collectPendingSetOwnershipRequest(player);
			// Has the player requested to free one of his pets?
			boolean freePet = plugin.getFreeCommand().collectPendingFreeRequest(player);

			if (disableRiding && !isOwner && !player.hasPermission("petmaster.admin") && tameable instanceof Vehicle) {
				plugin.getMessageSender().sendMessage(player, "not-owner");
				event.setCancelled(true);
				return;
			}

			// Cannot change ownership or free pet if not owner and no bypass permission.
			if ((newOwner != null || freePet) && !isOwner && !player.hasPermission("petmaster.admin")) {
				plugin.getMessageSender().sendMessage(player, "not-owner");
				return;
			}

			if (newOwner != null) {
				changeOwner(player, currentOwner, newOwner, tameable);
			} else if (freePet) {
				freePet(player, currentOwner, tameable);
			} else if ((displayToOwner || !isOwner) && player.hasPermission("petmaster.showowner.*")) {
				displayHologramAndMessage(player, currentOwner, tameable);
			}
		}
	}

	/**
	 * Determines whether the current PlayerInteractEntityEvent should be handled.
	 * 
	 * @param event
	 * @return true if the event should be handled, false otherwise
	 */
	private boolean shouldHandleEvent(PlayerInteractEntityEvent event) {
		return !plugin.getEnableDisableCommand().isDisabled() && event.getRightClicked() instanceof Tameable
				// On Minecraft 1.9+, the event is fired once per hand (HAND and OFF_HAND).
				&& (plugin.getServerVersion() < 9 || event.getHand() == EquipmentSlot.HAND);
	}

	/**
	 * Change the owner of a pet.
	 * 
	 * @param player
	 * @param oldOwner
	 * @param newOwner
	 * @param tameable
	 */
	private void changeOwner(Player player, AnimalTamer oldOwner, Player newOwner, Tameable tameable) {
		if (chargePrice(player, changeOwnerPrice)) {
			// Change owner.
			tameable.setOwner(newOwner);
			player.sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("owner-changed", "This pet was given to a new owner!"));
			newOwner.sendMessage(plugin.getChatHeader()
					+ plugin.getPluginLang().getString("new-owner", "Player PLAYER gave you ownership of a pet!")
							.replace("PLAYER", player.getName()));

			// Create new event to allow other plugins to be aware of the ownership change.
			PlayerChangeAnimalOwnershipEvent playerChangeAnimalOwnershipEvent = new PlayerChangeAnimalOwnershipEvent(
					oldOwner, newOwner, tameable);
			Bukkit.getServer().getPluginManager().callEvent(playerChangeAnimalOwnershipEvent);
		}
	}

	/**
	 * Frees a pet; it will no longer be tamed.
	 * 
	 * @param player
	 * @param oldOwner
	 * @param tameable
	 */
	private void freePet(Player player, AnimalTamer oldOwner, Tameable tameable) {
		if (chargePrice(player, freePetPrice)) {
			// Free pet.
			tameable.setTamed(false);
			// Make freed pet stand up.
			if (plugin.getServerVersion() >= 12 && tameable instanceof Sittable) {
				((Sittable) tameable).setSitting(false);
			} else if (tameable instanceof Wolf) {
				((Wolf) tameable).setSitting(false);
			} else if (tameable instanceof Ocelot) {
				// Since Minecraft 1.14, ocelots are no longer Sittable, use reflection for old game versions.
				try {
					Method setSitting = Ocelot.class.getMethod("setSitting", boolean.class);
					setSitting.invoke(tameable, false);
				} catch (ReflectiveOperationException e) {
					plugin.getLogger().warning("Failed to make freed ocelot stand up.");
				}
			}

			plugin.getMessageSender().sendMessage(player, "pet-freed");

			// Create new event to allow other plugins to be aware of the freeing.
			PlayerChangeAnimalOwnershipEvent playerChangeAnimalOwnershipEvent = new PlayerChangeAnimalOwnershipEvent(
					oldOwner, null, tameable);
			Bukkit.getServer().getPluginManager().callEvent(playerChangeAnimalOwnershipEvent);
		}
	}

	/**
	 * Displays a hologram, and automatically delete it after a given delay.
	 * 
	 * @param player
	 * @param owner
	 * @param tameable
	 */
	@SuppressWarnings("deprecation")
	private void displayHologramAndMessage(Player player, AnimalTamer owner, Tameable tameable) {
		if (hologramMessage) {
			int version = plugin.getServerVersion();
			double offset = HORSE_OFFSET;
			if (tameable instanceof Ocelot || version >= 14 && tameable instanceof Cat) {
				if (!displayCat || !player.hasPermission("petmaster.showowner.cat")) {
					return;
				}
				offset = CAT_OFFSET;
			} else if (tameable instanceof Wolf) {
				if (!displayDog || !player.hasPermission("petmaster.showowner.dog")) {
					return;
				}
				offset = DOG_OFFSET;
			} else if (version >= 11 && tameable instanceof Llama) {
				if (!displayLlama || !player.hasPermission("petmaster.showowner.llama")) {
					return;
				}
				offset = LLAMA_OFFSET;
			} else if (version >= 12 && tameable instanceof Parrot) {
				if (!displayParrot || !player.hasPermission("petmaster.showowner.parrot")) {
					return;
				}
				offset = PARROT_OFFSET;
			} else if (tameable instanceof Vehicle) {
				if (!displayHorse || !player.hasPermission("petmaster.showowner.horse")) {
					return;
				}
			}

			Location eventLocation = ((Animals) tameable).getLocation();
			// Create location with offset.
			Location hologramLocation = new Location(eventLocation.getWorld(), eventLocation.getX(),
					eventLocation.getY() + offset, eventLocation.getZ());

			final Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);
			hologram.appendTextLine(plugin.getMessageSender().parseMessageToString(
					"petmaster-hologram",
					Placeholder.component("owner", Component.text(owner.getName() != null ? owner.getName() : "null"))
			));

			// Runnable to delete hologram.
			new BukkitRunnable() {

				@Override
				public void run() {

					hologram.delete();
				}
			}.runTaskLater(plugin, hologramDuration);
		}

		Component healthInfo = null;
		if (showHealth) {
			@SuppressWarnings("cast") // Tameable did not extend Animals in older versions of Bukkit.
			Animals animal = (Animals) tameable;
			healthInfo = plugin.getMessageSender().parseMessage(
					plugin.getPluginLang().getString("petmaster-health"),
					Placeholder.component("current-health", Component.text(String.format("%.1f", animal.getHealth()))),
					Placeholder.component("max-health", Component.text(
							plugin.getServerVersion() < 9 ? String.format("%.1f", animal.getMaxHealth())
							: String.format("%.1f", animal.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())))
			);
		}

		if (chatMessage) {
			Component parsedComponent = plugin.getMessageSender().parseMessage(
					plugin.getPluginLang().getString("petmaster-chat"),
					Placeholder.component("owner", Component.text(owner.getName() != null ? owner.getName() : "null"))
			);
			if (healthInfo != null){
				parsedComponent = parsedComponent.append(healthInfo);
			}
			plugin.getMessageSender().sendComponent(player, parsedComponent);
		}

		if (actionBarMessage) {
			Component parsedComponent = plugin.getMessageSender().parseMessage(
					plugin.getPluginLang().getString("petmaster-action-bar"),
					Placeholder.component("owner", Component.text(owner.getName() != null ? owner.getName() : "null"))
			);
			if (healthInfo != null){
				parsedComponent = parsedComponent.append(healthInfo);
			}
			plugin.getMessageSender().sendComponentToActionBar(player, parsedComponent);
		}
	}

	/**
	 * Charges a player if he has enough money and displays relevant messages.
	 * 
	 * @param player
	 * @param price
	 * @return true if money should be withdrawn from the player, false otherwise
	 */
	private boolean chargePrice(Player player, int price) {
		// Charge player for changing ownership.
		if (price > 0 && !player.hasPermission("petmaster.admin") && economy != null) {
			// If server has set different currency names depending on amount, adapt message accordingly.
			String priceWithCurrency = price + " "
					+ (price > 1 ? economy.currencyNamePlural() : economy.currencyNameSingular());
			if (economy.getBalance(player) < price) {
				plugin.getMessageSender().sendMessage(
						player,
						"not-enough-money",
						Placeholder.component("amount", Component.text(priceWithCurrency))
				);
				return false;
			}
			economy.withdrawPlayer(player, price);
			plugin.getMessageSender().sendMessage(
					player,
					"change-owner-price",
					Placeholder.component("amount", Component.text(priceWithCurrency))
			);
		}
		return true;
	}
}
