package net.minecraft.world.loot;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import net.minecraft.util.Identifier;
import java.util.Random;

public final class ConstantLootTableRange implements LootTableRange
{
    private final int value;
    
    public ConstantLootTableRange(final int value) {
        this.value = value;
    }
    
    @Override
    public int next(final Random random) {
        return this.value;
    }
    
    @Override
    public Identifier getType() {
        return ConstantLootTableRange.CONSTANT;
    }
    
    public static ConstantLootTableRange create(final int value) {
        return new ConstantLootTableRange(value);
    }
    
    public static class Serializer implements JsonDeserializer<ConstantLootTableRange>, JsonSerializer<ConstantLootTableRange>
    {
        public ConstantLootTableRange a(final JsonElement json, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            return new ConstantLootTableRange(JsonHelper.asInt(json, "value"));
        }
        
        public JsonElement a(final ConstantLootTableRange range, final Type unused, final JsonSerializationContext context) {
            return new JsonPrimitive(range.value);
        }
    }
}
