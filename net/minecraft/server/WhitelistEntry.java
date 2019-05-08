package net.minecraft.server;

import java.util.UUID;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

public class WhitelistEntry extends ServerConfigEntry<GameProfile>
{
    public WhitelistEntry(final GameProfile gameProfile) {
        super(gameProfile);
    }
    
    public WhitelistEntry(final JsonObject jsonObject) {
        super(deserializeProfile(jsonObject), jsonObject);
    }
    
    @Override
    protected void serialize(final JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("uuid", (this.getKey().getId() == null) ? "" : this.getKey().getId().toString());
        jsonObject.addProperty("name", this.getKey().getName());
        super.serialize(jsonObject);
    }
    
    private static GameProfile deserializeProfile(final JsonObject json) {
        if (!json.has("uuid") || !json.has("name")) {
            return null;
        }
        final String string2 = json.get("uuid").getAsString();
        UUID uUID3;
        try {
            uUID3 = UUID.fromString(string2);
        }
        catch (Throwable throwable4) {
            return null;
        }
        return new GameProfile(uUID3, json.get("name").getAsString());
    }
}
