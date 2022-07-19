package com.hm.petmaster.utils;

import com.hm.petmaster.PetMaster;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageSender {
    private final PetMaster plugin;
    private final MiniMessage miniMessage;

    public MessageSender(PetMaster plugin) {
        this.plugin = plugin;
        miniMessage = MiniMessage.miniMessage();
    }
    

    public String parseMessageToString(String key, TagResolver... tagResolvers) {
        return LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().build().serialize(
                parseMessage(plugin.getPluginLang().getString(key), tagResolvers)
        );
    }

    public Component parseMessage(String message, TagResolver... tagResolvers) {
        List<TagResolver> allTagResolvers = new ArrayList<>(Arrays.stream(tagResolvers).toList());
        allTagResolvers.add(Placeholder.parsed(
                "prefix",
                plugin.getPluginLang().getString("petmaster-prefix", "<gray>[<gold>♞<gray>] ")
        ));
        return miniMessage.deserialize(message, allTagResolvers.toArray(new TagResolver[]{}));
    }

    public void sendComponent(Player player, Component component){
        Audience audience = plugin.adventure().player(player);
        audience.sendMessage(component);
    }

    public void sendComponentToActionBar(Player player, Component component){
        Audience audience = plugin.adventure().player(player);
        audience.sendActionBar(component);
    }

    public void sendMessage(Audience audience, String key, TagResolver... tagResolvers) {
        audience.sendMessage(parseMessage(plugin.getPluginLang().getString(key), tagResolvers));
    }

    public void sendMessage(Player player, String key, TagResolver... tagResolvers) {
        Audience audience = plugin.adventure().player(player);
        sendMessage(audience, key, tagResolvers);
    }

    public void sendMessage(CommandSender sender, String key, TagResolver... tagResolvers) {
        Audience audience = plugin.adventure().sender(sender);
        sendMessage(audience, key, tagResolvers);
    }

    public void sendActionBar(Audience audience, String key, TagResolver... tagResolvers) {
        audience.sendActionBar(parseMessage(plugin.getPluginLang().getString(key), tagResolvers));
    }

    public void sendActionBar(Player player, String key, TagResolver... tagResolvers) {
        Audience audience = plugin.adventure().player(player);
        sendActionBar(audience, key, tagResolvers);
    }

    public void sendNewLine(CommandSender sender, boolean sendPrefix){
        Audience audience = plugin.adventure().sender(sender);

        if (sendPrefix){
            sendMessage(audience, "petmaster-prefix");
        } else {
            audience.sendMessage(Component.newline());
        }
    }
}
