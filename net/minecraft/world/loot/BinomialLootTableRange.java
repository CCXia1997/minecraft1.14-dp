package net.minecraft.world.loot;

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
import java.util.Random;

public final class BinomialLootTableRange implements LootTableRange
{
    private final int n;
    private final float p;
    
    public BinomialLootTableRange(final int n, final float p) {
        this.n = n;
        this.p = p;
    }
    
    @Override
    public int next(final Random random) {
        int integer2 = 0;
        for (int integer3 = 0; integer3 < this.n; ++integer3) {
            if (random.nextFloat() < this.p) {
                ++integer2;
            }
        }
        return integer2;
    }
    
    public static BinomialLootTableRange create(final int n, final float p) {
        return new BinomialLootTableRange(n, p);
    }
    
    @Override
    public Identifier getType() {
        return BinomialLootTableRange.BINOMIAL;
    }
    
    public static class Serializer implements JsonDeserializer<BinomialLootTableRange>, JsonSerializer<BinomialLootTableRange>
    {
        public BinomialLootTableRange a(final JsonElement json, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = JsonHelper.asObject(json, "value");
            final int integer5 = JsonHelper.getInt(jsonObject4, "n");
            final float float6 = JsonHelper.getFloat(jsonObject4, "p");
            return new BinomialLootTableRange(integer5, float6);
        }
        
        public JsonElement a(final BinomialLootTableRange range, final Type unused, final JsonSerializationContext context) {
            final JsonObject jsonObject4 = new JsonObject();
            jsonObject4.addProperty("n", range.n);
            jsonObject4.addProperty("p", range.p);
            return jsonObject4;
        }
    }
}
