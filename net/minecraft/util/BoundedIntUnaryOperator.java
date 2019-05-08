package net.minecraft.util;

import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import java.util.function.IntUnaryOperator;

public class BoundedIntUnaryOperator implements IntUnaryOperator
{
    private final Integer min;
    private final Integer max;
    private final IntUnaryOperator operator;
    
    private BoundedIntUnaryOperator(@Nullable final Integer integer1, @Nullable final Integer integer2) {
        this.min = integer1;
        this.max = integer2;
        if (integer1 == null) {
            if (integer2 == null) {
                this.operator = (integer -> integer);
            }
            else {
                final int integer4 = integer2;
                this.operator = (integer2 -> Math.min(integer4, integer2));
            }
        }
        else {
            final int integer4 = integer1;
            if (integer2 == null) {
                final int integer5;
                this.operator = (integer2 -> Math.max(integer5, integer2));
            }
            else {
                final int integer6 = integer2;
                final int integer5;
                this.operator = (integer3 -> MathHelper.clamp(integer3, integer5, integer6));
            }
        }
    }
    
    public static BoundedIntUnaryOperator create(final int integer1, final int integer2) {
        return new BoundedIntUnaryOperator(integer1, integer2);
    }
    
    public static BoundedIntUnaryOperator createMin(final int integer) {
        return new BoundedIntUnaryOperator(integer, null);
    }
    
    public static BoundedIntUnaryOperator createMax(final int integer) {
        return new BoundedIntUnaryOperator(null, integer);
    }
    
    @Override
    public int applyAsInt(final int integer) {
        return this.operator.applyAsInt(integer);
    }
    
    public static class Serializer implements JsonDeserializer<BoundedIntUnaryOperator>, JsonSerializer<BoundedIntUnaryOperator>
    {
        public BoundedIntUnaryOperator a(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject4 = JsonHelper.asObject(functionJson, "value");
            final Integer integer5 = jsonObject4.has("min") ? Integer.valueOf(JsonHelper.getInt(jsonObject4, "min")) : null;
            final Integer integer6 = jsonObject4.has("max") ? Integer.valueOf(JsonHelper.getInt(jsonObject4, "max")) : null;
            return new BoundedIntUnaryOperator(integer5, integer6, null);
        }
        
        public JsonElement a(final BoundedIntUnaryOperator boundedIntUnaryOperator, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject4 = new JsonObject();
            if (boundedIntUnaryOperator.max != null) {
                jsonObject4.addProperty("max", boundedIntUnaryOperator.max);
            }
            if (boundedIntUnaryOperator.min != null) {
                jsonObject4.addProperty("min", boundedIntUnaryOperator.min);
            }
            return jsonObject4;
        }
    }
}
