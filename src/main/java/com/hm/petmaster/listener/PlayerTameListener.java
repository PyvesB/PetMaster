package com.hm.petmaster.listener;

import com.hm.petmaster.PetMaster;
import java.io.File;
import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

public class PlayerTameListener implements Listener {

	private final PetMaster plugin;

	public PlayerTameListener(PetMaster plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerTamePet(EntityTameEvent event) {
		switch (event.getEntity().getType()) {
			case CAT: {
				DyeColor color = plugin.getSetColorCommand().getColor(event.getOwner().getUniqueId());
				((Cat) event.getEntity()).setCollarColor(color != null ? color : DyeColor.RED);
				break;
			}
			case WOLF: {
				DyeColor color = plugin.getSetColorCommand().getColor(event.getOwner().getUniqueId());
				((Wolf) event.getEntity()).setCollarColor(color != null ? color : DyeColor.RED);
				break;
			}
			default:
			// Nothing
		}
	}

}
