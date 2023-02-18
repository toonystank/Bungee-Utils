package com.toonystank.bungeeutils.gui.premade;

import com.toonystank.bungeeutils.BungeeUtils;
import com.toonystank.bungeeutils.gui.GUICache;

import java.io.IOException;

public class Selector {

    BungeeUtils plugin;

    public Selector(BungeeUtils plugin) throws IOException {
        GUICache guiCache = new GUICache(plugin, "selector.yml", true, true);
        guiCache.loadConfiguration();
    }

    public void processGUI() {
        plugin.getProxy().getServers().forEach((name, server) -> {
            plugin.getProxy().getLogger().info("Server: " + name + " ServerInfo: " + server);
        });
    }

}
