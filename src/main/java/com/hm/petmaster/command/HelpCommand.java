package com.hm.petmaster.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hm.petmaster.PetMaster;
import com.hm.petmaster.language.Lang;
import com.hm.petmaster.particle.PacketSender;

public class HelpCommand {

	private PetMaster plugin;

	public HelpCommand(PetMaster plugin) {

		this.plugin = plugin;
	}

	public void getHelp(CommandSender sender) {

		sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD).append("-=-=-=-=-=-=-=-=-=-")
				.append(ChatColor.GRAY).append("[").append(ChatColor.GOLD).append("\u265E").append("§lPet Master")
				.append(ChatColor.GOLD).append("\u265E").append(ChatColor.GRAY).append("]").append(ChatColor.GOLD)
				.append("-=-=-=-=-=-=-=-=-=-").toString());

		sendJsonClickableMessage(
				sender,
				(new StringBuilder()).append(plugin.getChatHeader()).append(ChatColor.GOLD + "/petm disable")
						.append(ChatColor.GRAY).append(" - " + Lang.PETMASTER_COMMAND_DISABLE).toString(),
				"/petm disable");

		sendJsonClickableMessage(
				sender,
				(new StringBuilder()).append(plugin.getChatHeader()).append(ChatColor.GOLD + "/petm enable")
						.append(ChatColor.GRAY).append(" - " + Lang.PETMASTER_COMMAND_ENABLE).toString(),
				"/petm enable");

		sendJsonClickableMessage(
				sender,
				(new StringBuilder()).append(plugin.getChatHeader()).append(ChatColor.GOLD + "/petm reload")
						.append(ChatColor.GRAY).append(" - " + Lang.PETMASTER_COMMAND_RELOAD).toString(),
				"/petm reload");

		sendJsonClickableMessage(
				sender,
				(new StringBuilder()).append(plugin.getChatHeader()).append(ChatColor.GOLD + "/petm info")
						.append(ChatColor.GRAY).append(" - " + Lang.PETMASTER_COMMAND_INFO).toString(), "/petm info");

		sender.sendMessage((new StringBuilder()).append(ChatColor.GOLD)
				.append("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-").toString());
	}

	/**
	 * Send a packet message to the server in order to display a clickable
	 * message. A suggestion command is then displayed in the chat. Parts of
	 * this method were extracted from ELCHILEN0's AutoMessage plugin, under MIT
	 * license (http://dev.bukkit.org/bukkit-plugins/automessage/). Thanks for
	 * his help on this matter.
	 */
	public void sendJsonClickableMessage(CommandSender sender, String message, String command) {

		// Build the json format string.
		String json = "{\"text\":\"" + message + "\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + command + "\"}}";

		if (sender instanceof Player)
			try {
				PacketSender.sendChatPacket((Player) sender, json);
			} catch (Exception ex) {

				plugin.getLogger()
						.severe("Errors while trying to display clickable in /petm help command. Displaying standard message instead.");
				sender.sendMessage(message);
			}
		else
			sender.sendMessage(message);
	}
}
