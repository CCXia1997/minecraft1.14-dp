package net.minecraft.client.network;

import net.minecraft.util.SystemUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanServerEntry
{
    private final String motd;
    private final String addressPort;
    private long lastTimeMillis;
    
    public LanServerEntry(final String motd, final String string2) {
        this.motd = motd;
        this.addressPort = string2;
        this.lastTimeMillis = SystemUtil.getMeasuringTimeMs();
    }
    
    public String getMotd() {
        return this.motd;
    }
    
    public String getAddressPort() {
        return this.addressPort;
    }
    
    public void updateLastTime() {
        this.lastTimeMillis = SystemUtil.getMeasuringTimeMs();
    }
}
