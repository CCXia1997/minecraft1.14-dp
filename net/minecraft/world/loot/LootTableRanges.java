package net.minecraft.world.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;
import java.util.Map;

public class LootTableRanges
{
    private static final Map<Identifier, Class<? extends LootTableRange>> types;
    
    public static LootTableRange deserialize(final JsonElement json, final JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return context.<LootTableRange>deserialize(json, ConstantLootTableRange.class);
        }
        final JsonObject jsonObject3 = json.getAsJsonObject();
        final String string4 = JsonHelper.getString(jsonObject3, "type", LootTableRange.UNIFORM.toString());
        final Class<? extends LootTableRange> class5 = LootTableRanges.types.get(new Identifier(string4));
        if (class5 == null) {
            throw new JsonParseException("Unknown generator: " + string4);
        }
        return context.<LootTableRange>deserialize(jsonObject3, class5);
    }
    
    public static JsonElement serialize(final LootTableRange range, final JsonSerializationContext context) {
        final JsonElement jsonElement3 = context.serialize(range);
        if (jsonElement3.isJsonObject()) {
            jsonElement3.getAsJsonObject().addProperty("type", range.getType().toString());
        }
        return jsonElement3;
    }
    
    static {
        (types = Maps.newHashMap()).put(LootTableRange.UNIFORM, UniformLootTableRange.class);
        LootTableRanges.types.put(LootTableRange.BINOMIAL, BinomialLootTableRange.class);
        LootTableRanges.types.put(LootTableRange.CONSTANT, ConstantLootTableRange.class);
    }
}
