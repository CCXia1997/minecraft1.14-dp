package net.minecraft.realms;

import net.minecraft.network.ServerAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsServerAddress
{
    private final String host;
    private final int port;
    
    protected RealmsServerAddress(final String string, final int integer) {
        this.host = string;
        this.port = integer;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public static RealmsServerAddress parseString(final String string) {
        final ServerAddress serverAddress2 = ServerAddress.parse(string);
        return new RealmsServerAddress(serverAddress2.getAddress(), serverAddress2.getPort());
    }
}
