package net.minecraft.server;

import java.util.UUID;
import net.minecraft.text.StringTextComponent;
import java.util.Objects;
import net.minecraft.text.TextComponent;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import java.util.Date;
import com.mojang.authlib.GameProfile;

public class BannedPlayerEntry extends BanEntry<GameProfile>
{
    public BannedPlayerEntry(final GameProfile gameProfile) {
        this(gameProfile, null, null, null, null);
    }
    
    public BannedPlayerEntry(final GameProfile profile, @Nullable final Date created, @Nullable final String source, @Nullable final Date expiry, @Nullable final String string5) {
        super(profile, created, source, expiry, string5);
    }
    
    public BannedPlayerEntry(final JsonObject jsonObject) {
        super(getProfileFromJson(jsonObject), jsonObject);
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
    
    @Override
    public TextComponent asTextComponent() {
        final GameProfile gameProfile1 = this.getKey();
        return new StringTextComponent((gameProfile1.getName() != null) ? gameProfile1.getName() : Objects.toString(gameProfile1.getId(), "(Unknown)"));
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
