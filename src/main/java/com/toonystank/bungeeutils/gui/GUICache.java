package com.toonystank.bungeeutils.gui;

import com.toonystank.bungeeutils.BungeeUtils;
import com.toonystank.bungeeutils.utils.config.ConfigManager;
import dev.simplix.protocolize.data.inventory.InventoryType;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.*;

@Getter
@Setter
public class GUICache extends ConfigManager {

    private final BungeeUtils plugin;
    private String title;
    private InventoryType inventoryType;
    private List<String> openCommand = new ArrayList<>();
    private List<String> commandArguments = new ArrayList<>();
    private Map<Integer, String> slotMap = new HashMap<>();
    private Map<String, GUISection> sectionMap = new HashMap<>();

    public GUICache(BungeeUtils plugin, String fileName, boolean copy) throws IOException {
        super(plugin, fileName, false, copy);
        this.plugin = plugin;
        loadConfiguration(fileName, copy);
    }
    public GUICache(BungeeUtils plugin, String fileName, boolean copy, boolean construct) throws IOException {
        super(plugin, fileName, false, copy);
        this.plugin = plugin;
    }

    private void loadConfiguration(String fileName, boolean copy) {
        this.title = this.getString("data.menu_title");
        this.inventoryType = InventoryType.valueOf(this.getString("data.inventory_type"));
        this.openCommand.addAll(this.getStringList("data.open_command"));
        if (this.getConfig().contains("data.args")) {
            this.commandArguments.addAll(this.getStringList("data.args"));
        }
        setSlotMap();
        setSectionMap();
    }

    private void setSlotMap() {
        this.slotMap.clear();
        Set<String> items = this.getConfig().getConfigurationSection("items").getKeys(false);
        for (String item : items) {
            if (this.getConfig().contains("items." + item + ".slot")) {
                int slot = this.getInt("items." + item + ".slot");
                this.slotMap.put(slot, item);
            }
            if (this.getConfig().contains("items." + item + ".slots")) {
                List<Integer> slots = this.getConfig().getIntegerList("items." + item + ".slots");
                for (int slot : slots) {
                    this.slotMap.put(slot, item);
                }
            }
        }
    }
    private void setSectionMap() {
        this.sectionMap.clear();
        Set<String> sections = this.getConfig().getConfigurationSection("sections").getKeys(false);
        for (String section : sections) {
            String material = this.getString("sections." + section + ".material");
            List<Integer> slots = this.getConfig().getIntegerList("sections." + section + ".slots");
            String displayName = this.getString("sections." + section + ".display_name");
            List<String> lore = this.getStringList("sections." + section + ".lore");
            List<String> commands = this.getStringList("sections." + section + ".click_commands");
            this.sectionMap.put(section, new GUISection(material, slots, displayName, lore, commands, section));
        }
    }
}
