package com.hm.petmaster.command;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import com.hm.petmaster.PetMaster;

public class PetShareCommand {
	PetMaster plugin = PetMaster.getPlugin(PetMaster.class);
	
	ArrayList<String> sharingWith = new ArrayList<String>();
	
	Tameable pet;
	
	boolean isTamed;
	
	
	
	public PetShareCommand(PetMaster petmaster) {
		this.plugin = petmaster;
	}
	
	public void onPetShare(Player owner, Player target, String[] args) {
		target = Bukkit.getPlayerExact(args[1]);
		if(pet.isTamed()) {
			
		}
	}
	
	public void onPetStopShare(Player owner, Player target, String[] args) {
		target = Bukkit.getPlayerExact(args[1]);
	}
}
