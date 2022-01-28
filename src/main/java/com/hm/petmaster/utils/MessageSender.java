package com.hm.petmaster.utils;

import com.hm.petmaster.PetMaster;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MessageSender {
    private final PetMaster plugin;

    public MessageSender(PetMaster plugin) {
        this.plugin = plugin;
    }

    public Component parseMessage(String message) {
        return parseMessage(message, new ArrayList<>());
    }

    public String parseMessageToString(String key, List<Template> templates) {
        return LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().build().serialize(
                parseMessage(plugin.getPluginLang().getString(key), templates)
        );
    }

    public String parseMessageToString(String key) {
        return parseMessageToString(key, new ArrayList<>());
    }

    public Component parseMessage(String message, List<Template> templates) {
        templates.add(Template.of("prefix", plugin.getPluginLang().getString("petmaster-prefix")));
        return MiniMessage.get().parse(message, templates);
    }

    public void sendComponent(Player player, Component component){
        Audience audience = plugin.adventure().player(player);
        audience.sendMessage(component);
    }

    public void sendComponentToActionBar(Player player, Component component){
        Audience audience = plugin.adventure().player(player);
        audience.sendActionBar(component);
    }

    public void sendMessage(Audience audience, String key, List<Template> templates) {
        audience.sendMessage(parseMessage(plugin.getPluginLang().getString(key), templates));
    }

    public void sendMessage(Player player, String key) {
        Audience audience = plugin.adventure().player(player);
        sendMessage(audience, key, new ArrayList<>());
    }

    public void sendMessage(Player player, String key, List<Template> templates) {
        Audience audience = plugin.adventure().player(player);
        sendMessage(audience, key, templates);
    }

    public void sendMessage(CommandSender sender, String key) {
        Audience audience = plugin.adventure().sender(sender);
        sendMessage(audience, key, new ArrayList<>());
    }

    public void sendMessage(CommandSender sender, String key, List<Template> templates) {
        Audience audience = plugin.adventure().sender(sender);
        sendMessage(audience, key, templates);
    }

    public void sendActionBar(Audience audience, String key, List<Template> templates) {
        audience.sendActionBar(parseMessage(plugin.getPluginLang().getString(key)));
    }

    public void sendActionBar(Player player, String key) {
        Audience audience = plugin.adventure().player(player);
        sendActionBar(audience, key, new ArrayList<>());
    }

    public void sendActionBar(Player player, String key, List<Template> templates) {
        Audience audience = plugin.adventure().player(player);
        sendActionBar(audience, key, templates);
    }
}
