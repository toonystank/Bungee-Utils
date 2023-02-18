package com.toonystank.bungeeutils.gui;

import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder extends ItemStack {

    public ItemStack itemStack = this;
    public ItemType itemType;
    public int amount;
    public int durability;
    public String displayName;
    public List<String> lore = new ArrayList<>();

    public ItemBuilder(ItemType itemType) {
        super(itemType);
        this.itemType = itemType;
    }
    public ItemBuilder(ItemType itemType, int amount) {
        super(itemType, amount);
        this.itemType = itemType;
        this.amount = amount;
    }
    public ItemBuilder(BaseItemStack baseItemStack) {
        super(baseItemStack);
    }
    public ItemBuilder(ItemType itemType, int amount, short durability) {
        super(itemType, amount, durability);
        this.itemType = itemType;
        this.amount = amount;
        this.durability = durability;
    }
    public ItemBuilder setDisplayName(String displayName) {
        this.displayName(displayName);
        this.displayName = displayName;
        return this;
    }
    public ItemBuilder setLore(List<String> lore) {
        this.lore().add(lore);
        this.lore = lore;
        return this;
    }
    public ItemBuilder setAmount(byte amount) {
        this.amount(amount);
        return this;
    }
    public ItemBuilder setDurability(short durability) {
        this.durability(durability);
        return this;
    }
    public ItemBuilder formatColor() {
        this.displayName(ChatColor.translateAlternateColorCodes('&', this.displayName));
        this.lore.forEach(lore -> this.lore.add(ChatColor.translateAlternateColorCodes('&', lore)));
        this.setLore(this.lore);
        return this;
    }
    public ItemStack getItemStack() {
        return this;
    }
}
