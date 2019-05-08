package net.minecraft.server;

import java.net.SocketAddress;
import com.google.gson.JsonObject;
import java.io.File;

public class BannedIpList extends ServerConfigList<String, BannedIpEntry>
{
    public BannedIpList(final File file) {
        super(file);
    }
    
    @Override
    protected ServerConfigEntry<String> fromJson(final JsonObject jsonObject) {
        return new BannedIpEntry(jsonObject);
    }
    
    public boolean isBanned(final SocketAddress ip) {
        final String string2 = this.stringifyAddress(ip);
        return ((ServerConfigList<String, V>)this).contains(string2);
    }
    
    public boolean isBanned(final String ip) {
        return ((ServerConfigList<String, V>)this).contains(ip);
    }
    
    public BannedIpEntry get(final SocketAddress socketAddress) {
        final String string2 = this.stringifyAddress(socketAddress);
        return this.get(string2);
    }
    
    private String stringifyAddress(final SocketAddress socketAddress) {
        String string2 = socketAddress.toString();
        if (string2.contains("/")) {
            string2 = string2.substring(string2.indexOf(47) + 1);
        }
        if (string2.contains(":")) {
            string2 = string2.substring(0, string2.indexOf(58));
        }
        return string2;
    }
}
