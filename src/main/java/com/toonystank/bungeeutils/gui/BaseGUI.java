package com.toonystank.bungeeutils.gui;

import com.toonystank.bungeeutils.BungeeUtils;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.Sound;
import lombok.Getter;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class BaseGUI {

    private BungeeUtils plugin;
    private BungeeAudiences adventure;
    private GUICache guiCache;
    private Map<ProxiedPlayer, GUIData> guiDataMap = new HashMap<>();
    public BaseGUI(BungeeUtils plugin, String fileName, BungeeAudiences adventure, boolean copy) throws IOException {
        guiCache = new GUICache(plugin, fileName, copy);
        this.plugin = plugin;
        this.adventure = adventure;
    }
    public void openGui(ProxiedPlayer player) {
        this.setInventory(player);
        this.populateInventory(guiDataMap.get(player).getInventory(), player, guiCache.getCommandArguments());
        guiDataMap.get(player).getProtocolizePlayer().openInventory(this.guiDataMap.get(player).getInventory());
    }
    private void setInventory(ProxiedPlayer player) {
        guiDataMap.get(player).setInventory(new Inventory(guiCache.getInventoryType()));
    }

    public void populateInventory(Inventory inventory, ProxiedPlayer player, List<String> args) {
        ProtocolizePlayer protocolizePlayer = guiDataMap.get(player).getProtocolizePlayer();
        GUIData guiData = new GUIData(plugin, this, player, inventory);
        guiCache.getSlotMap().forEach((slot, item) -> {
            plugin.getProxy().getLogger().info("Slot: " + slot + " Item: " + item);
            ItemStack itemStack = new ItemBuilder(ItemType.valueOf(guiData.getParsedSections().get(item).getMaterial()))
                    .setDisplayName(guiData.getParsedSections().get(item).getDisplayName())
                    .setLore(guiData.getParsedSections().get(item).getLore())
                    .formatColor();
            plugin.getProxy().getLogger().info("ItemStack: " + itemStack);
            inventory.item(slot, itemStack);
            inventory.onClick(event -> {
                if (event.slot() != slot) return;
                if (event.cancelled()) return;
                event.cancelled(true);
                plugin.getProxy().getLogger().info("Clicked on slot: " + slot);
                List<String> clickCommands = guiData.getParsedSections().get(item).getClickCommands();
                if (clickCommands == null) return;
                clickCommands.forEach(command -> {
                    List<String> arguments = new ArrayList<>();
                    if (command.contains("[sound]")) {
                        String sound = command.replace("[sound] ", "");
                        protocolizePlayer.playSound(Sound.valueOf(sound), SoundCategory.MASTER, 1, 1);
                    } else if (command.contains("[message]")) {
                        String message = command.replace("[message] ", "");
                        message = formatText(protocolizePlayer, message, arguments);
                        adventure.player(protocolizePlayer.uniqueId()).sendMessage(Component.text(message));
                    } else if (command.contains("[player]")) {
                        String runTimeCommand = command.replace("[player] ", "");
                        runTimeCommand = formatText(protocolizePlayer, runTimeCommand, arguments);
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
                        plugin.getProxy().getScheduler().schedule(plugin, () -> {
                            populateInventory(inventory, player, args);
                        }, 1, TimeUnit.SECONDS);
                    }
                });

            });
        });
        guiData.setInventory(inventory);
    }
    public String formatText(ProtocolizePlayer player, String text, List<String> args) {
        return text;
    }


}
