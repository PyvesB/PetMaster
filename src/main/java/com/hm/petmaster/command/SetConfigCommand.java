package com.hm.petmaster.command;

import java.util.HashSet;

import org.bukkit.entity.Player;

import com.hm.petmaster.PetMaster;
import com.hm.petmaster.files.PetAbilityFile;

import net.md_5.bungee.api.ChatColor;

public class SetConfigCommand {
	PetMaster plugin = PetMaster.getPlugin(PetMaster.class);
	
	public SetConfigCommand(PetMaster plugin) {
		this.plugin = plugin;
	}
	
	public void setConfigCommand(Player player, String[] args) {
		if(player.hasPermission("petmaster.admin")) {
			if(args[0].equalsIgnoreCase("set")) {
				if(args[1].equalsIgnoreCase("config")) {
					if(args[2].equalsIgnoreCase("set")) {
						player.sendMessage(" ");
						player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
						player.sendMessage(ChatColor.LIGHT_PURPLE +"#  PetMaster Dev Note           #");
						player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
						player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" This is a test feature!");
						player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " It will be available soon!");
						player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " Enjoy!");
						
						
					}
				}
				if(args[1].equalsIgnoreCase("pet-abilities")) {
					if(args[2].equalsIgnoreCase("enabled")) {
						if(args[3].equalsIgnoreCase("true")) {
							player.sendMessage(" ");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#  PetMaster Dev Note           #");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" This is a test feature!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " It will be available soon!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " Enjoy!");
							
							player.sendMessage("You have enabled Pet Abilities!");
							PetAbilityFile.getPetAbilities().set("Is-Pet-Abilities-Enabled", true);
						}
						if(args[3].equalsIgnoreCase("false")) {
							player.sendMessage(" ");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#  PetMaster Dev Note           #");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" This is a test feature!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " It will be available soon!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " Enjoy!");
							
							player.sendMessage("You have disabled Pet Abilities!");
							PetAbilityFile.getPetAbilities().set("Is-Pet-Abilities-Enabled", false);
						}
					}
					if(args[2].equalsIgnoreCase("set")) {
						if(args[3].equalsIgnoreCase("health")) {
							player.sendMessage(" ");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#  PetMaster Dev Note           #");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" This is a test feature!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " It will be available soon!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " Enjoy!");
							
							player.sendMessage("You have enabled Pet Abilities!");
							
							
						}
						if(args[3].equalsIgnoreCase("damage")) {
							player.sendMessage(" ");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#  PetMaster Dev Note           #");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" This is a test feature!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " It will be available soon!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " Enjoy!");
							
							player.sendMessage("You have enabled Pet Abilities!");
							
						}
						if(args[3].equalsIgnoreCase("speed")) {
							player.sendMessage(" ");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#  PetMaster Dev Note           #");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" This is a test feature!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " It will be available soon!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " Enjoy!");
							
							player.sendMessage("You have enabled Pet Abilities!");
							
						}
						if(args[3].equalsIgnoreCase("protection")) {
							player.sendMessage(" ");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#  PetMaster Dev Note           #");
							player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" This is a test feature!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " It will be available soon!");
							player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " Enjoy!");
							
							player.sendMessage("You have enabled Pet Abilities!");
							
						}
					}
				}
			}
		}
	}

}
