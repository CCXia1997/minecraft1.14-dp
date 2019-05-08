package net.minecraft.server;

import com.google.gson.JsonObject;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import java.util.Date;

public class BannedIpEntry extends BanEntry<String>
{
    public BannedIpEntry(final String string) {
        this(string, null, null, null, null);
    }
    
    public BannedIpEntry(final String ip, @Nullable final Date created, @Nullable final String source, @Nullable final Date expiry, @Nullable final String string5) {
        super(ip, created, source, expiry, string5);
    }
    
    @Override
    public TextComponent asTextComponent() {
        return new StringTextComponent(this.getKey());
    }
    
    public BannedIpEntry(final JsonObject jsonObject) {
        super(getIpFromJson(jsonObject), jsonObject);
    }
    
    private static String getIpFromJson(final JsonObject json) {
        return json.has("ip") ? json.get("ip").getAsString() : null;
    }
    
    @Override
    protected void serialize(final JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("ip", this.getKey());
        super.serialize(jsonObject);
    }
}
