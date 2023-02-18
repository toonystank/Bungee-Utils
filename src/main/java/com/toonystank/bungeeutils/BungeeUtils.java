package com.toonystank.bungeeutils;

import com.toonystank.bungeeutils.utils.MainConfig;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeUtils extends Plugin  {

    private static BungeeUtils instance;
    private MainConfig mainConfig;
    private BungeeAudiences audiences;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        audiences = BungeeAudiences.create(this);
        try {
            mainConfig = new MainConfig(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public BungeeAudiences getAudiences() {
        return audiences;
    }
}
