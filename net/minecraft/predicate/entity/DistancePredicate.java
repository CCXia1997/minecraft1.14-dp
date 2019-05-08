package net.minecraft.predicate.entity;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.NumberRange;

public class DistancePredicate
{
    public static final DistancePredicate ANY;
    private final NumberRange.FloatRange x;
    private final NumberRange.FloatRange y;
    private final NumberRange.FloatRange z;
    private final NumberRange.FloatRange horizontal;
    private final NumberRange.FloatRange absolute;
    
    public DistancePredicate(final NumberRange.FloatRange x, final NumberRange.FloatRange y, final NumberRange.FloatRange z, final NumberRange.FloatRange horizontal, final NumberRange.FloatRange floatRange5) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.horizontal = horizontal;
        this.absolute = floatRange5;
    }
    
    public static DistancePredicate horizontal(final NumberRange.FloatRange horizontal) {
        return new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, horizontal, NumberRange.FloatRange.ANY);
    }
    
    public static DistancePredicate y(final NumberRange.FloatRange y) {
        return new DistancePredicate(NumberRange.FloatRange.ANY, y, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY);
    }
    
    public boolean test(final double double1, final double double3, final double double5, final double double7, final double double9, final double double11) {
        final float float13 = (float)(double1 - double7);
        final float float14 = (float)(double3 - double9);
        final float float15 = (float)(double5 - double11);
        return this.x.matches(MathHelper.abs(float13)) && this.y.matches(MathHelper.abs(float14)) && this.z.matches(MathHelper.abs(float15)) && this.horizontal.matchesSquared(float13 * float13 + float15 * float15) && this.absolute.matchesSquared(float13 * float13 + float14 * float14 + float15 * float15);
    }
    
    public static DistancePredicate deserialize(@Nullable final JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return DistancePredicate.ANY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(el, "distance");
        final NumberRange.FloatRange floatRange3 = NumberRange.FloatRange.fromJson(jsonObject2.get("x"));
        final NumberRange.FloatRange floatRange4 = NumberRange.FloatRange.fromJson(jsonObject2.get("y"));
        final NumberRange.FloatRange floatRange5 = NumberRange.FloatRange.fromJson(jsonObject2.get("z"));
        final NumberRange.FloatRange floatRange6 = NumberRange.FloatRange.fromJson(jsonObject2.get("horizontal"));
        final NumberRange.FloatRange floatRange7 = NumberRange.FloatRange.fromJson(jsonObject2.get("absolute"));
        return new DistancePredicate(floatRange3, floatRange4, floatRange5, floatRange6, floatRange7);
    }
    
    public JsonElement serialize() {
        if (this == DistancePredicate.ANY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        jsonObject1.add("x", this.x.serialize());
        jsonObject1.add("y", this.y.serialize());
        jsonObject1.add("z", this.z.serialize());
        jsonObject1.add("horizontal", this.horizontal.serialize());
        jsonObject1.add("absolute", this.absolute.serialize());
        return jsonObject1;
    }
    
    static {
        ANY = new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY);
    }
}
