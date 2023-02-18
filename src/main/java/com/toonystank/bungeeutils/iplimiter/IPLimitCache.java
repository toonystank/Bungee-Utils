package com.toonystank.bungeeutils.iplimiter;

import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class IPLimitCache {

    private final IPLimitData data;
    private String ownerName;
    private UUID uuid;
    private String ip;
    private List<String> alts = new ArrayList<>();
    private boolean isMain = false;

    public IPLimitCache(IPLimitData data, ProxiedPlayer player) {
        this.data = data;
        this.ownerName = player.getName();
        this.uuid = player.getUniqueId();
        this.ip = player.getPendingConnection().getSocketAddress().toString();
        process();
    }
    public IPLimitCache(IPLimitData data, String uuid, String ip, String name) {
        this.data = data;
        this.ownerName = name;
        this.uuid = UUID.fromString(uuid);
        this.ip = ip;
        process();
    }

    private void process() {
        if (data.isAlt(String.valueOf(uuid)).getBoolean()) return;
        if (data.isPlayerInConfig(String.valueOf(uuid))) {
            isMain = true;
            this.alts = data.getAlts(String.valueOf(uuid));
            return;
        }
        data.addPlayer(String.valueOf(uuid), ip, ownerName);
    }

    public IPLimitReturnType addAlt(ProxiedPlayer player) {
        return addAlt(player.getUniqueId().toString());
    }
    public IPLimitReturnType addAlt(String altUUID) {
        if (data.isAlt(altUUID).getBoolean()) return IPLimitReturnType.ERROR;
        alts.add(altUUID);
        data.addAlt(String.valueOf(this.uuid), altUUID);
        return IPLimitReturnType.SUCCESS;
    }

}
