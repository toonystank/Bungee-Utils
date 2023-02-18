package com.toonystank.bungeeutils.iplimiter;

import com.toonystank.bungeeutils.BungeeUtils;
import com.toonystank.bungeeutils.utils.MainConfig;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class IPLimitPlayerJoin implements Listener {

    private final BungeeUtils plugin;
    private final MainConfig mainConfig;
    private final IPLimit ipLimit;

    public IPLimitPlayerJoin(BungeeUtils plugin, MainConfig mainConfig, IPLimit ipLimit) {
        this.plugin = plugin;
        this.mainConfig = mainConfig;
        this.ipLimit = ipLimit;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        switch (ipLimit.canAllow(event.getPlayer())) {
            case SUCCESS:
                break;
            case MAX_ALTS:
                event.getPlayer().disconnect(new TextComponent(mainConfig.getIPLimitKickMessage()));
                break;
            case ALT:
                String uuid = ipLimit.getOwnerUUID(event.getPlayer().getUniqueId().toString());
                if (uuid != null) {
                    IPLimitCache cache = ipLimit.getCache().get(uuid);
                    plugin.getLogger().info("Allowing alt " + event.getPlayer().getName() + " to join the server.");
                    plugin.getLogger().info("Owner UUID: " + cache.getOwnerName());
                    plugin.getLogger().info("Alts: " + cache.getAlts());

                }
            case ERROR:
                plugin.getLogger().warning("An error occurred while trying to check if the player " + event.getPlayer().getName() + " can join the server. aborting check and letting player join.");
                break;

        }
    }

}
