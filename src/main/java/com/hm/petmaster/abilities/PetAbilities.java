package com.hm.petmaster.abilities;

import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import com.hm.petmaster.PetMaster;

public class PetAbilities {
	PetMaster plugin = PetMaster.getPlugin(PetMaster.class);
	
	//General Variables
	Tameable pet;
	boolean isPetOwned;
	Player player;
	
	//Health Bonus Variables
	int healthBonusAmount = plugin.getConfig().getInt("Health-Bonus-Amount");
	boolean isHealthBonusEnabled = plugin.getConfig().getBoolean("Health-Bonus-Enabled");
	
	
	public PetAbilities(PetMaster petmaster) {
		this.plugin = petmaster;
	}
	
	public void petHealthBonus() {
		if(isHealthBonusEnabled == true) {
			
		}
	}
}
