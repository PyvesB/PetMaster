package com.hm.petmaster.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hm.petmaster.PetMaster;

import net.md_5.bungee.api.ChatColor;

public class PetSkillCommand {
	PetMaster plugin = PetMaster.getPlugin(PetMaster.class);
	
	public PetSkillCommand(PetMaster petMaster) {
		this.plugin = petMaster;
	}
	
	public void petSkillCommand(CommandSender sender) {
		Player player = (Player) sender;
		player.sendMessage(" ");
		player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
		player.sendMessage(ChatColor.LIGHT_PURPLE +"#  PetMaster Dev Note           #");
		player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
		player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" This is a test feature!");
		player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " It will be available soon!");
		player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " Enjoy!");
		player.sendMessage(" ");
		
		if(player.hasPermission("petmaster.admin") || player.isOp()) {
			player.sendMessage("Pet Boosts Info");
			player.sendMessage("#-------------------------#");
			player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" Pet Boost(Enabled): " + ChatColor.GREEN +plugin.getConfig().getBoolean("Pet-Boost-Enabled"));
			player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" Health Boost(Enabled): " + ChatColor.GREEN + plugin.getConfig().getBoolean("Health-Boost-Enabled"));
			player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" Damage Boost(Enabled): " + ChatColor.GREEN + plugin.getConfig().getBoolean("Damage-Boost-Enabled"));
			player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" Speed Boost(Enabled): " + ChatColor.GREEN + plugin.getConfig().getBoolean("Speed-Boost-Enabled"));
			player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" Defense Boost(Enabled): " + ChatColor.GREEN + plugin.getConfig().getBoolean("Defense-Boost-Enabled"));
			player.sendMessage(" ");
		}
		
		//Stats of boosts current pet is giving them
		player.sendMessage("Pet Boosts");
		player.sendMessage("#---------------------#");
		player.sendMessage(plugin.getChatHeader()+ ChatColor.LIGHT_PURPLE +" Health Boost: "+ ChatColor.GREEN + plugin.getConfig().getDouble("Health-Boost-Amount")+ ChatColor.LIGHT_PURPLE+ " %");
		player.sendMessage(plugin.getChatHeader()+ ChatColor.LIGHT_PURPLE +" Damage Boost: "+ ChatColor.GREEN + plugin.getConfig().getDouble("Damage-Boost-Amount")+ ChatColor.LIGHT_PURPLE+ " %");
		player.sendMessage(plugin.getChatHeader()+ ChatColor.LIGHT_PURPLE +" Speed Boost: "+ ChatColor.GREEN + plugin.getConfig().getDouble("Speed-Boost-Amount")+ ChatColor.LIGHT_PURPLE+ " %");
		player.sendMessage(plugin.getChatHeader()+ ChatColor.LIGHT_PURPLE +" Defense Boost: "+ ChatColor.GREEN + plugin.getConfig().getDouble("Defense-Boost-Amount")+ ChatColor.LIGHT_PURPLE+ " %");
		
		return;
	}
}
