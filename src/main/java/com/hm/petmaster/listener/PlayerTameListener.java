package com.hm.petmaster.listener;

import com.hm.mcshared.particle.ReflectionUtils.PackageType;
import com.hm.petmaster.PetMaster;
import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

public class PlayerTameListener implements Listener {

	private final PetMaster plugin;
	private final int version;

	public PlayerTameListener(PetMaster plugin) {
		this.plugin = plugin;
		this.version = Integer.parseInt(PackageType.getServerVersion().split("_")[1]);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerTamePet(EntityTameEvent event) {
		if (version >= 14 && event.getEntity().getType().equals(EntityType.CAT)) {
			DyeColor color = plugin.getSetColorCommand().getColor(event.getOwner().getUniqueId());
			((Cat) event.getEntity()).setCollarColor(color);
		} else if (event.getEntity().getType().equals(EntityType.WOLF)) {
			DyeColor color = plugin.getSetColorCommand().getColor(event.getOwner().getUniqueId());
			((Wolf) event.getEntity()).setCollarColor(color);
		}
	}

}
