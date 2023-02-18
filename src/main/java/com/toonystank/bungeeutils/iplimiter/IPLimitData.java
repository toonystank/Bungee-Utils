package com.toonystank.bungeeutils.iplimiter;

import com.toonystank.bungeeutils.utils.config.ConfigManager;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class IPLimitData extends ConfigManager {

@Getter
    private final Plugin plugin;
@Getter
    private final String fileName;

    public IPLimitData(Plugin plugin, String fileName) throws IOException {
        super(plugin, fileName, "iplimit" ,false, false);
        this.plugin = plugin;
        this.fileName = fileName;
        setupDefaults();
    }

    private void setupDefaults() {
        this.getConfig().addDefault("users", null);
    }

    public void addPlayer(ProxiedPlayer player) {
        addPlayer(player.getUniqueId().toString(), player.getPendingConnection().getSocketAddress().toString(), player.getName());
    }

    public void addPlayer(String uuid, String ip, String name) {
        this.getConfig().set("users." + uuid + ".ip", ip);
        this.getConfig().set("users." + uuid + ".name", name);
        this.save();
    }

    public void removePlayer(ProxiedPlayer player) {
        removePlayer(player.getUniqueId().toString());
    }

    public void removePlayer(String uuid) {
        this.getConfig().set("users." + uuid, null);
        this.save();
    }

    public List<String> getPlayers() {
        return this.getConfig().getStringList("users");
    }

    public boolean isPlayerInConfig(ProxiedPlayer player) {
        return isPlayerInConfig(player.getUniqueId().toString());
    }

    public boolean isPlayerInConfig(String uuid) {
        return this.getConfig().contains("users." + uuid);
    }

    public IPLimitReturnType doseIpExist(ProxiedPlayer player) {
        return doseIpExist(player.getUniqueId().toString(), player.getPendingConnection().getSocketAddress().toString());
    }

    public IPLimitReturnType doseIpExist(String player, String ip) {
        for (String stringUuid: getPlayers()) {
            if (Objects.equals(this.getConfig().getString("users." + stringUuid + ".ip"), ip)) {
                return IPLimitReturnType.ALREADY_EXISTS;
            }
        }
        return IPLimitReturnType.NOT_FOUND;
    }

    public String getIp(ProxiedPlayer player) {
        return getIp(player.getUniqueId().toString());
    }

    public String getIp(String uuid) {
        return this.getConfig().getString("users." + uuid + ".ip");
    }
    public String getName(ProxiedPlayer player) {
        return getName(player.getUniqueId().toString());
    }
    public String getName(String uuid) {
        return this.getConfig().getString("users." + uuid + ".name");
    }

    public List<String> getAlts(ProxiedPlayer player) {
        return getAlts(player.getUniqueId().toString());
    }

    public List<String> getAlts(String uuid) {
        return getStringList("users." + uuid + ".alts");
    }

    public void addAlt(ProxiedPlayer player, String altUUID) {
        addAlt(player.getUniqueId().toString(), altUUID);
    }

    public void addAlt(String uuid, String altUUID) {
        List<String> alts = getAlts(uuid);
        alts.add(altUUID);
        this.getConfig().set("users." + uuid + ".alts", alts);
        this.save();
    }

    public int getAltCount(ProxiedPlayer player) {
        return getAltCount(player.getUniqueId().toString());
    }
    public int getAltCount(String uuid) {
        return getAlts(uuid).size();
    }

    public IPLimitReturnType isAlt(ProxiedPlayer player) {
        return isAlt(player.getUniqueId().toString());
    }

    public IPLimitReturnType isAlt(String uuid) {
        for (String stringUUID: getPlayers()) {
            if (getAlts(stringUUID).contains(uuid)) {
                return IPLimitReturnType.ALT;
            }
        }
        return IPLimitReturnType.ERROR;
    }


}
