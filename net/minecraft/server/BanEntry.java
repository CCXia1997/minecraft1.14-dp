package net.minecraft.server;

import net.minecraft.text.TextComponent;
import java.text.ParseException;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import java.util.Date;
import java.text.SimpleDateFormat;

public abstract class BanEntry<T> extends ServerConfigEntry<T>
{
    public static final SimpleDateFormat DATE_FORMAT;
    protected final Date creationDate;
    protected final String source;
    protected final Date expiryDate;
    protected final String reason;
    
    public BanEntry(final T object, @Nullable final Date date2, @Nullable final String string3, @Nullable final Date date4, @Nullable final String string5) {
        super(object);
        this.creationDate = ((date2 == null) ? new Date() : date2);
        this.source = ((string3 == null) ? "(Unknown)" : string3);
        this.expiryDate = date4;
        this.reason = ((string5 == null) ? "Banned by an operator." : string5);
    }
    
    protected BanEntry(final T object, final JsonObject jsonObject) {
        super(object, jsonObject);
        Date date3;
        try {
            date3 = (jsonObject.has("created") ? BanEntry.DATE_FORMAT.parse(jsonObject.get("created").getAsString()) : new Date());
        }
        catch (ParseException parseException4) {
            date3 = new Date();
        }
        this.creationDate = date3;
        this.source = (jsonObject.has("source") ? jsonObject.get("source").getAsString() : "(Unknown)");
        Date date4;
        try {
            date4 = (jsonObject.has("expires") ? BanEntry.DATE_FORMAT.parse(jsonObject.get("expires").getAsString()) : null);
        }
        catch (ParseException parseException5) {
            date4 = null;
        }
        this.expiryDate = date4;
        this.reason = (jsonObject.has("reason") ? jsonObject.get("reason").getAsString() : "Banned by an operator.");
    }
    
    public String getSource() {
        return this.source;
    }
    
    public Date getExpiryDate() {
        return this.expiryDate;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public abstract TextComponent asTextComponent();
    
    @Override
    boolean isInvalid() {
        return this.expiryDate != null && this.expiryDate.before(new Date());
    }
    
    @Override
    protected void serialize(final JsonObject jsonObject) {
        jsonObject.addProperty("created", BanEntry.DATE_FORMAT.format(this.creationDate));
        jsonObject.addProperty("source", this.source);
        jsonObject.addProperty("expires", (this.expiryDate == null) ? "forever" : BanEntry.DATE_FORMAT.format(this.expiryDate));
        jsonObject.addProperty("reason", this.reason);
    }
    
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    }
}
