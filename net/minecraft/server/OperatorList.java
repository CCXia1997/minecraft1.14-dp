package net.minecraft.server;

import java.util.Iterator;
import com.google.gson.JsonObject;
import java.io.File;
import com.mojang.authlib.GameProfile;

public class OperatorList extends ServerConfigList<GameProfile, OperatorEntry>
{
    public OperatorList(final File file) {
        super(file);
    }
    
    @Override
    protected ServerConfigEntry<GameProfile> fromJson(final JsonObject jsonObject) {
        return new OperatorEntry(jsonObject);
    }
    
    @Override
    public String[] getNames() {
        final String[] arr1 = new String[((ServerConfigList<K, OperatorEntry>)this).values().size()];
        int integer2 = 0;
        for (final ServerConfigEntry<GameProfile> serverConfigEntry4 : ((ServerConfigList<K, OperatorEntry>)this).values()) {
            arr1[integer2++] = serverConfigEntry4.getKey().getName();
        }
        return arr1;
    }
    
    public boolean isOp(final GameProfile gameProfile) {
        final OperatorEntry operatorEntry2 = this.get(gameProfile);
        return operatorEntry2 != null && operatorEntry2.canBypassPlayerLimit();
    }
    
    @Override
    protected String toString(final GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }
}
