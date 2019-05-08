package net.minecraft.advancement.criterion;

import java.text.ParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import net.minecraft.util.PacketByteBuf;
import java.util.Date;
import java.text.SimpleDateFormat;

public class CriterionProgress
{
    private static final SimpleDateFormat FORMAT;
    private Date obtained;
    
    public boolean isObtained() {
        return this.obtained != null;
    }
    
    public void obtain() {
        this.obtained = new Date();
    }
    
    public void reset() {
        this.obtained = null;
    }
    
    public Date getObtainedDate() {
        return this.obtained;
    }
    
    @Override
    public String toString() {
        return "CriterionProgress{obtained=" + ((this.obtained == null) ? "false" : this.obtained) + '}';
    }
    
    public void toPacket(final PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBoolean(this.obtained != null);
        if (this.obtained != null) {
            packetByteBuf.writeDate(this.obtained);
        }
    }
    
    public JsonElement toJson() {
        if (this.obtained != null) {
            return new JsonPrimitive(CriterionProgress.FORMAT.format(this.obtained));
        }
        return JsonNull.INSTANCE;
    }
    
    public static CriterionProgress fromPacket(final PacketByteBuf packetByteBuf) {
        final CriterionProgress criterionProgress2 = new CriterionProgress();
        if (packetByteBuf.readBoolean()) {
            criterionProgress2.obtained = packetByteBuf.readDate();
        }
        return criterionProgress2;
    }
    
    public static CriterionProgress obtainedAt(final String string) {
        final CriterionProgress criterionProgress2 = new CriterionProgress();
        try {
            criterionProgress2.obtained = CriterionProgress.FORMAT.parse(string);
        }
        catch (ParseException parseException3) {
            throw new JsonSyntaxException("Invalid datetime: " + string, parseException3);
        }
        return criterionProgress2;
    }
    
    static {
        FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    }
}
