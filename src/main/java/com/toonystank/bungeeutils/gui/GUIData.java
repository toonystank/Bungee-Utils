package com.toonystank.bungeeutils.gui;


import com.toonystank.bungeeutils.BungeeUtils;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

@Getter
@Setter
public class GUIData {

    private final BungeeUtils plugin;
    private final BaseGUI gui;
    private final ProxiedPlayer proxiedPlayer;
    private final ProtocolizePlayer protocolizePlayer;
    private Inventory inventory;

    private String parsedTitle;
    private final Map<String, GUISection> parsedSections;

    /**
     * Constructs a new GUIData object.
     *
     * @param plugin the BungeeUtils plugin
     * @param gui the GUI instance
     * @param player the player viewing the GUI
     * @throws NullPointerException if any parameter is null
     */
    public GUIData(BungeeUtils plugin, BaseGUI gui, ProxiedPlayer player, Inventory inventory) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.gui = Objects.requireNonNull(gui, "gui cannot be null");
        this.proxiedPlayer = Objects.requireNonNull(player, "player cannot be null");
        this.protocolizePlayer = Protocolize.playerProvider().player(player.getUniqueId());
        this.parsedSections = new HashMap<>();
        this.parsedTitle = parse(gui.getGuiCache().getTitle());
        this.inventory = inventory;
        parseSections();
    }

    /**
     * Parses the GUI title and replaces any occurrences of "%player%" with the player's name.
     *
     * @return the parsed title
     */
    public String getParsedTitle() {
        return parsedTitle;
    }

    /**
     * Returns the parsed sections of the GUI.
     *
     * @return the parsed sections
     */
    public Map<String, GUISection> getParsedSections() {
        return parsedSections;
    }

    /**
     * Parses the GUI sections and replaces any occurrences of "%player%" with the player's name.
     */
    private void parseSections() {
        gui.getGuiCache().getSlotMap().forEach((slot, item) -> {
            GUISection section = gui.getGuiCache().getSectionMap().get(item);
            if (section != null) {
                GUISection parsedSection = gui.getGuiCache().getSectionMap().get(item);
                parsedSection.setLore(parseLines(section.getLore()));
                parsedSection.setDisplayName(parse(section.getDisplayName()));
                parsedSection.setClickCommands(parseLines(section.getClickCommands()));
                parsedSections.put(item, parsedSection);
            }
        });
    }

    /**
     * Replaces any occurrences of "%player%" in the given text with the player's name.
     *
     * @param text the text to parse
     * @return the parsed text
     */
    private String parse(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        return text.replace("%player%", proxiedPlayer.getName());
    }

    /**
     * Parses a list of strings and replaces any occurrences of "%player%" with the player's name.
     *
     * @param lines the list of strings to parse
     * @return the parsed list of strings
     */
    private List<String> parseLines(List<String> lines) {
        if (lines == null) {
            return null;
        }
        List<String> parsedLines = new ArrayList<>();
        for (String line : lines) {
            parsedLines.add(parse(line));
        }
        return parsedLines;
    }
}
