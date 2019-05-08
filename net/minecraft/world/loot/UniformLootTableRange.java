package net.minecraft.world.loot;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import java.util.Random;

public class UniformLootTableRange implements LootTableRange
{
    private final float min;
    private final float max;
    
    public UniformLootTableRange(final float min, final float max) {
        this.min = min;
        this.max = max;
    }
    
    public UniformLootTableRange(final float value) {
        this.min = value;
        this.max = value;
    }
    
    public static UniformLootTableRange between(final float min, final float max) {
        return new UniformLootTableRange(min, max);
    }
    
    public float getMinValue() {
        return this.min;
    }
    
    public float getMaxValue() {
        return this.max;
    }
    
    @Override
    public int next(final Random random) {
        return MathHelper.nextInt(random, MathHelper.floor(this.min), MathHelper.floor(this.max));
    }
    
    public float nextFloat(final Random random) {
        return MathHelper.nextFloat(random, this.min, this.max);
    }
    
    public boolean contains(final int value) {
        return value <= this.max && value >= this.min;
    }
    
    @Override
    public Identifier getType() {
        return UniformLootTableRange.UNIFORM;
    }
    
    public static class Serializer implements JsonDeserializer<UniformLootTableRange>, JsonSerializer<UniformLootTableRange>
    {
        public UniformLootTableRange a(final JsonElement json, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            if (JsonHelper.isNumber(json)) {
                return new UniformLootTableRange(JsonHelper.asFloat(json, "value"));
            }
            final JsonObject jsonObject4 = JsonHelper.asObject(json, "value");
            final float float5 = JsonHelper.getFloat(jsonObject4, "min");
            final float float6 = JsonHelper.getFloat(jsonObject4, "max");
            return new UniformLootTableRange(float5, float6);
        }
        
        public JsonElement a(final UniformLootTableRange entry, final Type unused, final JsonSerializationContext context) {
            if (entry.min == entry.max) {
                return new JsonPrimitive(entry.min);
            }
            final JsonObject jsonObject4 = new JsonObject();
            jsonObject4.addProperty("min", entry.min);
            jsonObject4.addProperty("max", entry.max);
            return jsonObject4;
        }
    }
}
