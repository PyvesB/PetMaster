package com.hm.petmaster.particle;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.hm.petmaster.particle.ReflectionUtils.PackageType;


/**
 * Class used to send packets to the player; can be titles or json messages.
 * 
 * @author Pyves
 *
 */
public final class PacketSender {

	private static final String CLASS_CHAT_BASE_COMPONENT = "IChatBaseComponent";
	private static final String CLASS_CRAFT_PLAYER = "CraftPlayer";
	private static final String CLASS_ENTITY_PLAYER = "EntityPlayer";
	private static final String CLASS_PACKET = "Packet";
	private static final String CLASS_PACKET_PLAY_OUT_CHAT = "PacketPlayOutChat";
	private static final String CLASS_PLAYER_CONNECTION = "PlayerConnection";
	private static final String FIELD_PLAYER_CONNECTION = "playerConnection";
	private static final String METHOD_GET_HANDLE = "getHandle";
	private static final String METHOD_SEND_PACKET = "sendPacket";
	private static final String NESTED_CHAT_SERIALIZER = "ChatSerializer";
	private static final String PACKAGE_ENTITY = "entity";

	private PacketSender() {
		// Not called.
	}

	/**
	 * Sends the chat packet (hover and clickable messages).
	 * 
	 * @param player
	 * @param json
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws NoSuchFieldException
	 */
	public static void sendChatPacket(Player player, String json)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException,
			InstantiationException, NoSuchFieldException {

		// Retrieve a CraftPlayer instance and its PlayerConnection instance.
		Object craftPlayer = PackageType.CRAFTBUKKIT.getClass(PACKAGE_ENTITY + "." + CLASS_CRAFT_PLAYER).cast(player);
		Object craftHandle = PackageType.CRAFTBUKKIT.getClass(PACKAGE_ENTITY + "." + CLASS_CRAFT_PLAYER)
				.getMethod(METHOD_GET_HANDLE).invoke(craftPlayer);
		Object playerConnection = PackageType.MINECRAFT_SERVER.getClass(CLASS_ENTITY_PLAYER)
				.getField(FIELD_PLAYER_CONNECTION).get(craftHandle);

		// Parse the json message.
		Object parsedMessage;
		try {
			// Since 1.8.3
			parsedMessage = Class.forName(
					PackageType.MINECRAFT_SERVER + "." + CLASS_CHAT_BASE_COMPONENT + "$" + NESTED_CHAT_SERIALIZER)
					.getMethod("a", String.class)
					.invoke(null, ChatColor.translateAlternateColorCodes("&".charAt(0), json));
		} catch (ClassNotFoundException e) {
			// Older versions of the game.
			parsedMessage = PackageType.MINECRAFT_SERVER.getClass(NESTED_CHAT_SERIALIZER).getMethod("a", String.class)
					.invoke(null, ChatColor.translateAlternateColorCodes("&".charAt(0), json));
		}
		Object packetPlayOutChat = PackageType.MINECRAFT_SERVER.getClass(CLASS_PACKET_PLAY_OUT_CHAT)
				.getConstructor(PackageType.MINECRAFT_SERVER.getClass(CLASS_CHAT_BASE_COMPONENT))
				.newInstance(parsedMessage);

		// Send the message packet through the PlayerConnection.
		PackageType.MINECRAFT_SERVER.getClass(CLASS_PLAYER_CONNECTION)
				.getMethod(METHOD_SEND_PACKET, PackageType.MINECRAFT_SERVER.getClass(CLASS_PACKET))
				.invoke(playerConnection, packetPlayOutChat);
	}
}
