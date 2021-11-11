package com.hm.petmaster.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hm.petmaster.PetMaster;

import net.md_5.bungee.api.ChatColor;

public class ShareCommand {
	PetMaster plugin = PetMaster.getPlugin(PetMaster.class);
	
	public ShareCommand(PetMaster petMaster) {
		this.plugin = petMaster;
	}
	
	public void sharePetCommand(CommandSender sender) {
		Player player = (Player) sender;
		player.sendMessage(" ");
		player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
		player.sendMessage(ChatColor.LIGHT_PURPLE +"#  PetMaster Dev Note           #");
		player.sendMessage(ChatColor.LIGHT_PURPLE +"#-------------------------#");
		player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE +" This is a test feature!");
		player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " It will be available soon!");
		player.sendMessage(plugin.getChatHeader() + ChatColor.LIGHT_PURPLE + " Enjoy!");
		
		
		
		
		return;
	}
}
