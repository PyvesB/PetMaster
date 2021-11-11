package com.hm.petmaster.files;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PetAbilityFile {
	
	private static File petAbilityFile;
	private static FileConfiguration petAbility;
	private static String[] horse = {"health", "damage", "speed", "protection"};
	
	public static void petAbilitySetup() {
		petAbilityFile = new File(Bukkit.getServer().getPluginManager().getPlugin("PetMaster").getDataFolder(),"petability.yml" );
		
		if(!petAbilityFile.exists()) {
			try { 
				petAbilityFile.createNewFile();
			}catch(IOException e) {
				
			}
		}
		petAbility = YamlConfiguration.loadConfiguration(petAbilityFile);
		
	}
	
	public static FileConfiguration getPetAbilities() {
		return petAbility;
	}
	
	public static void petAbilitySave() {
		try {
			petAbility.save(petAbilityFile);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}
	
	public static void petAbilityReload() {
		petAbility = YamlConfiguration.loadConfiguration(petAbilityFile);
	}
	
	public static void petAbilityAddDefaults() {
		petAbility.set("Horse" , horse);
	}

}
