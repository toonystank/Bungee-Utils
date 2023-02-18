package com.toonystank.bungeeutils.gui;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@Setter
public class GUISection {
    private String material;
    private List<Integer> slots;
    private String displayName;
    private List<String> lore;
    @Nullable
    private List<String> clickCommands;
    private String sectionName;

    public GUISection(String material, List<Integer> slots, String displayName, List<String> lore, @Nullable List<String> clickCommands, String name) {
        this.material = material;
        this.slots = slots;
        this.displayName = displayName;
        this.lore = lore;
        this.clickCommands = clickCommands;
        this.sectionName = name;
    }

}
