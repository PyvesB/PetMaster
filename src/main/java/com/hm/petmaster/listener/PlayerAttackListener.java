package com.hm.petmaster.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerAttackListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamagePet(EntityDamageByEntityEvent event){
        if(!((event.getDamager() instanceof Projectile || event.getDamager() instanceof Player) && event.getEntity() instanceof Tameable && ((Tameable) event.getEntity()).getOwner() != null))
            return;
        Entity damager = event.getDamager();
        if(event.getDamager() instanceof Projectile)
            damager = (Entity) ((Projectile) event.getDamager()).getShooter();
        if(damager instanceof Player && !((Tameable) event.getEntity()).getOwner().getName().equals(damager.getName()))
            event.setCancelled(true);
    }
}
