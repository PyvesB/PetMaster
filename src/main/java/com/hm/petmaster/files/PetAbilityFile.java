package com.hm.petmaster.files;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PetAbilityFile {
	
	private static File petAbilityFile;
	private static FileConfiguration petAbility;
	
	private static int wolfHealth = 0;
	private static int wolfDamage = 0;
	private static int wolfSpeed = 0;
	private static int wolfProtection = 0;
	
	private static int catHealth = 0;
	private static int catDamage = 0;
	private static int catSpeed = 0;
	private static int catProtection = 0;
	
	private static int donkeyHealth = 0;
	private static int donkeyDamage = 0;
	private static int donkeySpeed = 0;
	private static int donkeyProtection = 0;
	
	private static int llamaHealth = 0;
	private static int llamaDamage = 0;
	private static int llamaSpeed = 0;
	private static int llamaProtection = 0;
	
	private static int parrotHealth = 0;
	private static int parrotDamage = 0;
	private static int parrotSpeed = 0;
	private static int parrotProtection = 0;
	
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
		petAbility.set("Is-Pet-Abilities-Enabled", true);
		
		petAbility.set("Wolf-Health", wolfHealth);
		petAbility.set("Wolf-Damage", wolfDamage);
		petAbility.set("Wolf-Speed", wolfSpeed);
		petAbility.set("Wolf-Protection", wolfProtection);
		
		petAbility.set("Cat-Health", catHealth);
		petAbility.set("Cat-Damage", catDamage);
		petAbility.set("Cat-Speed", catSpeed);
		petAbility.set("Cat-Protection", catProtection);
		
		petAbility.set("Donkey-Health", donkeyHealth);
		petAbility.set("Donkey-Damage", donkeyDamage);
		petAbility.set("Donkey-Speed", donkeySpeed);
		petAbility.set("Donkey-Protection", donkeyProtection);
		
		petAbility.set("Llama-Health", llamaHealth);
		petAbility.set("Llama-Damage", llamaDamage);
		petAbility.set("Llama-Speed", llamaSpeed);
		petAbility.set("Llama-Protection", llamaProtection);
		
		petAbility.set("Parrot-Health", parrotHealth);
		petAbility.set("Parrot-Damage", parrotDamage);
		petAbility.set("Parrot-Speed", parrotSpeed);
		petAbility.set("Parrot-Protection", parrotProtection);
	}

}
