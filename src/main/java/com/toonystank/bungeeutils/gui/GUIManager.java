package com.toonystank.bungeeutils.gui;

import com.toonystank.bungeeutils.BungeeUtils;
import com.toonystank.bungeeutils.utils.MainConfig;

import java.util.HashMap;
import java.util.Map;

public class GUIManager {

    private BungeeUtils plugin;
    private MainConfig mainConfig;
    private Map<String, BaseGUI> guiMap = new HashMap<>();

    public GUIManager(BungeeUtils plugin, MainConfig mainConfig) {
        this.plugin = plugin;
        this.mainConfig = mainConfig;
    }



}
