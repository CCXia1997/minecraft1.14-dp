package net.minecraft.server;

import java.util.UUID;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

public class OperatorEntry extends ServerConfigEntry<GameProfile>
{
    private final int permissionLevel;
    private final boolean bypassPlayerLimit;
    
    public OperatorEntry(final GameProfile profile, final int permissionLevel, final boolean boolean3) {
        super(profile);
        this.permissionLevel = permissionLevel;
        this.bypassPlayerLimit = boolean3;
    }
    
    public OperatorEntry(final JsonObject jsonObject) {
        super(getProfileFromJson(jsonObject), jsonObject);
        this.permissionLevel = (jsonObject.has("level") ? jsonObject.get("level").getAsInt() : 0);
        this.bypassPlayerLimit = (jsonObject.has("bypassesPlayerLimit") && jsonObject.get("bypassesPlayerLimit").getAsBoolean());
    }
    
    public int getPermissionLevel() {
        return this.permissionLevel;
    }
    
    public boolean canBypassPlayerLimit() {
        return this.bypassPlayerLimit;
    }
    
    @Override
    protected void serialize(final JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("uuid", (this.getKey().getId() == null) ? "" : this.getKey().getId().toString());
        jsonObject.addProperty("name", this.getKey().getName());
        super.serialize(jsonObject);
        jsonObject.addProperty("level", this.permissionLevel);
        jsonObject.addProperty("bypassesPlayerLimit", this.bypassPlayerLimit);
    }
    
    private static GameProfile getProfileFromJson(final JsonObject json) {
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
