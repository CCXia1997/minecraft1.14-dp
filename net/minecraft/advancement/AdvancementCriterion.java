package net.minecraft.advancement;

import javax.annotation.Nullable;
import java.util.Iterator;
import com.google.gson.JsonElement;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.advancement.criterion.Criterion;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.advancement.criterion.CriterionConditions;

public class AdvancementCriterion
{
    private final CriterionConditions conditions;
    
    public AdvancementCriterion(final CriterionConditions criterionConditions) {
        this.conditions = criterionConditions;
    }
    
    public AdvancementCriterion() {
        this.conditions = null;
    }
    
    public void serialize(final PacketByteBuf packetByteBuf) {
    }
    
    public static AdvancementCriterion deserialize(final JsonObject obj, final JsonDeserializationContext context) {
        final Identifier identifier3 = new Identifier(JsonHelper.getString(obj, "trigger"));
        final Criterion<?> criterion4 = Criterions.getById(identifier3);
        if (criterion4 == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + identifier3);
        }
        final CriterionConditions criterionConditions5 = (CriterionConditions)criterion4.conditionsFromJson(JsonHelper.getObject(obj, "conditions", new JsonObject()), context);
        return new AdvancementCriterion(criterionConditions5);
    }
    
    public static AdvancementCriterion createNew(final PacketByteBuf buf) {
        return new AdvancementCriterion();
    }
    
    public static Map<String, AdvancementCriterion> fromJson(final JsonObject obj, final JsonDeserializationContext context) {
        final Map<String, AdvancementCriterion> map3 = Maps.newHashMap();
        for (final Map.Entry<String, JsonElement> entry5 : obj.entrySet()) {
            map3.put(entry5.getKey(), deserialize(JsonHelper.asObject(entry5.getValue(), "criterion"), context));
        }
        return map3;
    }
    
    public static Map<String, AdvancementCriterion> fromPacket(final PacketByteBuf buf) {
        final Map<String, AdvancementCriterion> map2 = Maps.newHashMap();
        for (int integer3 = buf.readVarInt(), integer4 = 0; integer4 < integer3; ++integer4) {
            map2.put(buf.readString(32767), createNew(buf));
        }
        return map2;
    }
    
    public static void serialize(final Map<String, AdvancementCriterion> criteria, final PacketByteBuf buf) {
        buf.writeVarInt(criteria.size());
        for (final Map.Entry<String, AdvancementCriterion> entry4 : criteria.entrySet()) {
            buf.writeString(entry4.getKey());
            entry4.getValue().serialize(buf);
        }
    }
    
    @Nullable
    public CriterionConditions getConditions() {
        return this.conditions;
    }
    
    public JsonElement toJson() {
        final JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("trigger", this.conditions.getId().toString());
        jsonObject1.add("conditions", this.conditions.toJson());
        return jsonObject1;
    }
}
