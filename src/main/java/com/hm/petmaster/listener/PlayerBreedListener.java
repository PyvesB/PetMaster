package com.hm.petmaster.listener;

import com.hm.petmaster.PetMaster;
import static jdk.nashorn.internal.runtime.Version.version;
import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class PlayerBreedListener implements Listener {
	
	private final PetMaster plugin;
	
	public PlayerBreedListener(PetMaster plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBreedNewPet(EntityBreedEvent event) {
		if (plugin.getServerVersion() >= 14 && event.getEntity().getType().equals(EntityType.CAT)) {
			Cat cat = ((Cat) event.getEntity());
			DyeColor color = plugin.getSetColorCommand().getColor(cat.getOwner().getUniqueId());
			cat.setCollarColor(color);
		} else if (event.getEntity().getType().equals(EntityType.WOLF)) {
			Wolf wolf = ((Wolf) event.getEntity());
			DyeColor color = plugin.getSetColorCommand().getColor(wolf.getOwner().getUniqueId());
			wolf.setCollarColor(color);
		}
	}
}
