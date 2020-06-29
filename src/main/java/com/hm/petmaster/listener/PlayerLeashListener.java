package com.hm.petmaster.listener;

import com.hm.mcshared.particle.ReflectionUtils;
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
    private final int version;

    // Configuration parameters.
    private boolean disableLeash;

    public PlayerLeashListener(PetMaster petMaster) {
        this.plugin = petMaster;
        version = Integer.parseInt(ReflectionUtils.PackageType.getServerVersion().split("_")[1]);
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
        if (currentOwner == null || currentOwner.getUniqueId().equals(player.getUniqueId()))
            return;

        player.sendMessage(plugin.getChatHeader() + plugin.getPluginLang()
                .getString("not-owner", "You do not own this pet!").replace("PLAYER", player.getName()));
        event.setCancelled(true);
    }
}
