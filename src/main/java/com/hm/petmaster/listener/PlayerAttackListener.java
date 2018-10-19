package com.hm.petmaster.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.hm.petmaster.PetMaster;

/**
 * Class used to prevent player damage to pets who have an owner.
 * 
 * @author lss233
 *
 */
public class PlayerAttackListener implements Listener {

	private final PetMaster plugin;

	public PlayerAttackListener(PetMaster plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamagePet(EntityDamageByEntityEvent event) {
		if (plugin.getEnableDisableCommand().isDisabled()) {
			return;
		}

		if (!((event.getDamager() instanceof Projectile || event.getDamager() instanceof Player)
				&& event.getEntity() instanceof Tameable && ((Tameable) event.getEntity()).getOwner() != null)) {
			return;
		}
		Entity damager = event.getDamager();
		if (damager instanceof Projectile) {
			damager = (Entity) ((Projectile) damager).getShooter();
		}
		if (damager instanceof Player && !((Tameable) event.getEntity()).getOwner().getName().equals(damager.getName())) {
			event.setCancelled(true);
		}
	}
}
