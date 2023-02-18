package com.toonystank.bungeeutils.iplimiter;

import com.toonystank.bungeeutils.BungeeUtils;
import com.toonystank.bungeeutils.utils.MainConfig;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
public class IPLimit {

    private BungeeUtils plugin;
    private MainConfig mainConfig;
    private IPLimitData userIPLimitData;
    private Map<String,IPLimitCache> cache = new HashMap<>();

    public IPLimit(BungeeUtils plugin) throws IOException {
        this.plugin = plugin;

        userIPLimitData = new IPLimitData(plugin, "data.yml");
        plugin.getProxy().getScheduler().schedule(plugin,this::process,1, TimeUnit.SECONDS);
    }

    private void process() {
        for (String uuid: userIPLimitData.getPlayers()) {
            cache.put(uuid, new IPLimitCache(userIPLimitData, uuid, userIPLimitData.getIp(uuid), userIPLimitData.getName(uuid)));
        }
        registerEvents();
    }

    private void registerEvents() {
        plugin.getProxy().getPluginManager().registerListener(plugin, new IPLimitPlayerJoin(plugin, mainConfig, this));
    }

    public IPLimitReturnType canAllow(ProxiedPlayer player) {
        return canAllow(player.getUniqueId().toString(), player.getPendingConnection().getSocketAddress().toString(), player.getName());
    }
    public IPLimitReturnType canAllow(String uuid, String ip, String name) {
        if (mainConfig.isIpLimitDisabled()) return IPLimitReturnType.SUCCESS;
        IPLimitCache cache = this.cache.get(uuid);
        if (cache.getIp().equals(ip)) {
            if (cache.isMain() || cache.getAlts().contains(uuid)) {
                return IPLimitReturnType.SUCCESS;
            }
            if (cache.getAlts().size() >= mainConfig.getIPLimit()) {
                return IPLimitReturnType.MAX_ALTS;
            }
            cache.addAlt(uuid);
            return IPLimitReturnType.ALT;
        } else {
            this.cache.put(uuid,new IPLimitCache(userIPLimitData, uuid, ip, name));
            return IPLimitReturnType.SUCCESS;
        }
    }
    public @Nullable String getOwnerUUID(String altUUID) {
        for (String uuid: cache.keySet()) {
            if (cache.get(uuid).getAlts().contains(altUUID)) return uuid;
        }
        return null;
    }
}
