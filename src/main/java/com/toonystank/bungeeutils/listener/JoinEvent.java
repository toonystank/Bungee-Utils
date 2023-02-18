package com.toonystank.bungeeutils.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.logging.Logger;

public class JoinEvent implements Listener {

    private final Plugin plugin;
    private final Logger logger;

    public JoinEvent(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            logger.info("Player " + event.getPlayer().getName() + " joined the server.");
            logger.info(player.getPendingConnection().getSocketAddress().toString());
        }
    }
}
