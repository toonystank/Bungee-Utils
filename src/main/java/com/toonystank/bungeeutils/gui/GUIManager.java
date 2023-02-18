package com.toonystank.bungeeutils.gui;

import com.toonystank.bungeeutils.BungeeUtils;
import com.toonystank.bungeeutils.utils.MainConfig;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GUIManager implements Listener {

    private final BungeeUtils plugin;
    private final MainConfig mainConfig;
    private final Map<String, BaseGUI> guiMap = new HashMap<>();

    public GUIManager(BungeeUtils plugin, MainConfig mainConfig) {
        this.plugin = plugin;
        this.mainConfig = mainConfig;
    }

    public void loadGUIs() {
        this.mainConfig.getGuiList().forEach(gui -> {
            try {
                GUICache guiCache = new GUICache(this.plugin, this.mainConfig.getGuiFileName(gui), true, false);
                BaseGUI baseGUI = new BaseGUI(this.plugin, this.plugin.getAudiences(), guiCache);
                this.guiMap.put(gui, baseGUI);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        this.guiMap.forEach((name, gui) -> {
            plugin.getLogger().info("Checking if " + event.getMessage() + " contains " + gui.getGuiCache().getOpenCommand());
            if (gui.getGuiCache().getOpenCommand().contains(event.getMessage())) {
                gui.openGui((ProxiedPlayer) event.getSender());
            }
        });
    }



}
