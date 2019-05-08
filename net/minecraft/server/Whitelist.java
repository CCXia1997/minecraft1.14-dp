package net.minecraft.server;

import java.util.Iterator;
import com.google.gson.JsonObject;
import java.io.File;
import com.mojang.authlib.GameProfile;

public class Whitelist extends ServerConfigList<GameProfile, WhitelistEntry>
{
    public Whitelist(final File file) {
        super(file);
    }
    
    @Override
    protected ServerConfigEntry<GameProfile> fromJson(final JsonObject jsonObject) {
        return new WhitelistEntry(jsonObject);
    }
    
    public boolean isAllowed(final GameProfile profile) {
        return ((ServerConfigList<GameProfile, V>)this).contains(profile);
    }
    
    @Override
    public String[] getNames() {
        final String[] arr1 = new String[((ServerConfigList<K, WhitelistEntry>)this).values().size()];
        int integer2 = 0;
        for (final ServerConfigEntry<GameProfile> serverConfigEntry4 : ((ServerConfigList<K, WhitelistEntry>)this).values()) {
            arr1[integer2++] = serverConfigEntry4.getKey().getName();
        }
        return arr1;
    }
    
    @Override
    protected String toString(final GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }
}
