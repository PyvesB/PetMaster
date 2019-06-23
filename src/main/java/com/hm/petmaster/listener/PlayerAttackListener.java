package com.hm.petmaster.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
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

	private boolean enableAngryMobPlayerDamage;

	public PlayerAttackListener(PetMaster plugin) {
		this.plugin = plugin;
	}

	public void extractParameters() {
		enableAngryMobPlayerDamage = plugin.getPluginConfig().getBoolean("enableAngryMobPlayerDamage", true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamagePet(EntityDamageByEntityEvent event) {
		if (plugin.getEnableDisableCommand().isDisabled()) {
			return;
		}

		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		if (!isDamagerProjectileOrPlayer(damager) || !isDamagedOwned(damaged)) {
			return;
		}
		if (damager instanceof Projectile) {
			damager = (Entity) ((Projectile) damager).getShooter();
		}
		if (isDamagerPlayerDifferentFromDamagedOwner(damager, damaged)) {
			event.setCancelled(!enableAngryMobPlayerDamage || !isDamagedTargettingDamager(damager, damaged));
		}
	}

	private boolean isDamagerProjectileOrPlayer(Entity damager) {
		return damager instanceof Projectile || damager instanceof Player;
	}

	private boolean isDamagedOwned(Entity damaged) {
		return damaged instanceof Tameable && ((Tameable) damaged).getOwner() != null;
	}

	private boolean isDamagerPlayerDifferentFromDamagedOwner(Entity damager, Entity damaged) {
		return damager instanceof Player && !((Tameable) damaged).getOwner().getName().equals(damager.getName());
	}

	private boolean isDamagedTargettingDamager(Entity damager, Entity damaged) {
		if (damaged instanceof Mob) {
			Mob mob = (Mob) damaged;
			return mob.getTarget() != null && mob.getTarget().getName().equals(damager.getName());
		}
		return false;
	}
}
