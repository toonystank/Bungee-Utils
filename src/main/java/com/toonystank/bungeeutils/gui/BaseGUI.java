package com.toonystank.bungeeutils.gui;

import com.toonystank.bungeeutils.BungeeUtils;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class BaseGUI {

    private final BungeeUtils plugin;
    private final BungeeAudiences adventure;
    private final GUICache guiCache;
    private final Map<ProxiedPlayer, GUIData> guiDataMap = new HashMap<>();
    public BaseGUI(BungeeUtils plugin, BungeeAudiences adventure, GUICache guiCache) throws IOException {
        this.guiCache = guiCache;
        this.plugin = plugin;
        this.adventure = adventure;
        plugin.getProxy().getLogger().info("BaseGUI constructor");
    }
    public void openGui(ProxiedPlayer player) {
        plugin.getProxy().getLogger().info("Opening GUI for " + player.getName());
        Inventory inventory = new Inventory(guiCache.getInventoryType());
        GUIData guiData = new GUIData(plugin, this, player, inventory);
        guiDataMap.put(player, guiData);
        this.populateInventory( player);
        plugin.getProxy().getLogger().info("Opening inventory for " + guiData.getProtocolizePlayer().uniqueId());
        guiData.getInventory().title(guiData.getParsedTitle());
        guiData.getProtocolizePlayer().openInventory(guiData.getInventory());
    }

    public void populateInventory(ProxiedPlayer player) {
        guiCache.getSlotMap().forEach((slot, item) -> {
            plugin.getProxy().getLogger().info("Slot: " + slot + " Item: " + item);
            ItemStack itemStack = new ItemBuilder(ItemType.valueOf(guiDataMap.get(player).getParsedSections().get(item).getMaterial()))
                    .setDisplayName(guiDataMap.get(player).getParsedSections().get(item).getDisplayName())
                    .setLore(guiDataMap.get(player).getParsedSections().get(item).getLore())
                    .formatColor();
            plugin.getProxy().getLogger().info("ItemStack: " + itemStack);
            guiDataMap.get(player).getInventory().item(slot, itemStack);
            setClickEvent( player, slot, guiDataMap.get(player), item);
            plugin.getProxy().getLogger().info("Populated inventory");
        });
    }
    public void setClickEvent( ProxiedPlayer player, Integer slot, GUIData guiData, String item) {
        ProtocolizePlayer protocolizePlayer = guiDataMap.get(player).getProtocolizePlayer();
        guiDataMap.get(player).getInventory().onClick(event -> {
            if (event.slot() != slot) return;
            if (event.cancelled()) return;
            event.cancelled(true);
            plugin.getProxy().getLogger().info("Clicked on slot: " + slot);
            List<String> clickCommands = guiData.getParsedSections().get(item).getClickCommands();
            if (clickCommands == null) return;
            clickCommands.forEach(command -> {
                if (command.contains("[sound]")) {
                    Sound musicDisc = Sound.sound().type(Key.key("bamboo_wood_door.close")).build();
                    adventure.player(player).playSound(musicDisc);
                } else if (command.contains("[message]")) {
                    String message = command.replace("[message] ", "");
                    message = formatText(message);
                    adventure.player(protocolizePlayer.uniqueId()).sendMessage(Component.text(message));
                } else if (command.contains("[player]")) {
                    String runTimeCommand = command.replace("[player] ", "");
                    runTimeCommand = formatText(runTimeCommand);
                    plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getPlayer(protocolizePlayer.uniqueId()), runTimeCommand);
                } else if (command.contains("[connect]")) {
                    String server = command.replace("[connect] ", "");
                    ServerInfo serverInfo = plugin.getProxy().getServerInfo(server);
                    if (player.getServer().getInfo().equals(serverInfo)) {
                        protocolizePlayer.closeInventory();
                        adventure.player(protocolizePlayer.uniqueId()).sendMessage(Component.text("You are already connected to this server!"));
                        return;
                    }
                    player.connect(serverInfo);
                } else if (command.contains("[close]")) {
                    protocolizePlayer.closeInventory();
                } else if (command.contains("[close-open]")) {
                    protocolizePlayer.closeInventory();
                    plugin.getProxy().getScheduler().schedule(plugin, () -> openGui(player), 1, TimeUnit.SECONDS);
                }
            });

        });
    }

    public String formatText(String text) {
        return text;
    }


}
