package com.hm.petmaster.listener;

import com.hm.petmaster.PetMaster;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

public class PlayerLeashListener implements Listener {
    private final PetMaster plugin;

    // Configuration parameters.
    private boolean disableLeash;

    public PlayerLeashListener(PetMaster petMaster) {
        this.plugin = petMaster;
	}

    public void extractParameters() {
        disableLeash = plugin.getPluginConfig().getBoolean("disableLeash", false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent event) {
        if (plugin.getEnableDisableCommand().isDisabled() || !disableLeash)
            return;
        Entity entity = event.getEntity();
        if (!(entity instanceof Tameable))
            return;

        Player player = event.getPlayer();
        Tameable tameable = (Tameable) entity;
        AnimalTamer currentOwner = tameable.getOwner();
        if (currentOwner == null || currentOwner.getUniqueId().equals(player.getUniqueId()) || player.hasPermission("petmaster.admin"))
            return;

        player.sendMessage(plugin.getChatHeader() + plugin.getPluginLang()
                .getString("not-owner", "You do not own this pet!"));
        event.setCancelled(true);
    }
}
