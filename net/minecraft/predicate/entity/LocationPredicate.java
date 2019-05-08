package net.minecraft.predicate.entity;

import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import com.google.gson.JsonElement;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.StructureFeature;
import javax.annotation.Nullable;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.NumberRange;

public class LocationPredicate
{
    public static final LocationPredicate ANY;
    private final NumberRange.FloatRange x;
    private final NumberRange.FloatRange y;
    private final NumberRange.FloatRange z;
    @Nullable
    private final Biome biome;
    @Nullable
    private final StructureFeature<?> feature;
    @Nullable
    private final DimensionType dimension;
    
    public LocationPredicate(final NumberRange.FloatRange x, final NumberRange.FloatRange y, final NumberRange.FloatRange z, @Nullable final Biome biome, @Nullable final StructureFeature<?> feature, @Nullable final DimensionType dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.biome = biome;
        this.feature = feature;
        this.dimension = dimension;
    }
    
    public static LocationPredicate biome(final Biome biome) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, biome, null, null);
    }
    
    public static LocationPredicate dimension(final DimensionType dimension) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, dimension);
    }
    
    public static LocationPredicate feature(final StructureFeature<?> feature) {
        return new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, feature, null);
    }
    
    public boolean test(final ServerWorld world, final double x, final double double4, final double double6) {
        return this.test(world, (float)x, (float)double4, (float)double6);
    }
    
    public boolean test(final ServerWorld world, final float x, final float y, final float float4) {
        if (!this.x.matches(x)) {
            return false;
        }
        if (!this.y.matches(y)) {
            return false;
        }
        if (!this.z.matches(float4)) {
            return false;
        }
        if (this.dimension != null && this.dimension != world.dimension.getType()) {
            return false;
        }
        final BlockPos blockPos5 = new BlockPos(x, y, float4);
        return (this.biome == null || this.biome == world.getBiome(blockPos5)) && (this.feature == null || this.feature.isInsideStructure(world, blockPos5));
    }
    
    public JsonElement serialize() {
        if (this == LocationPredicate.ANY) {
            return JsonNull.INSTANCE;
        }
        final JsonObject jsonObject1 = new JsonObject();
        if (!this.x.isDummy() || !this.y.isDummy() || !this.z.isDummy()) {
            final JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("x", this.x.serialize());
            jsonObject2.add("y", this.y.serialize());
            jsonObject2.add("z", this.z.serialize());
            jsonObject1.add("position", jsonObject2);
        }
        if (this.dimension != null) {
            jsonObject1.addProperty("dimension", DimensionType.getId(this.dimension).toString());
        }
        if (this.feature != null) {
            jsonObject1.addProperty("feature", Feature.STRUCTURES.inverse().get(this.feature));
        }
        if (this.biome != null) {
            jsonObject1.addProperty("biome", Registry.BIOME.getId(this.biome).toString());
        }
        return jsonObject1;
    }
    
    public static LocationPredicate deserialize(@Nullable final JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return LocationPredicate.ANY;
        }
        final JsonObject jsonObject2 = JsonHelper.asObject(element, "location");
        final JsonObject jsonObject3 = JsonHelper.getObject(jsonObject2, "position", new JsonObject());
        final NumberRange.FloatRange floatRange4 = NumberRange.FloatRange.fromJson(jsonObject3.get("x"));
        final NumberRange.FloatRange floatRange5 = NumberRange.FloatRange.fromJson(jsonObject3.get("y"));
        final NumberRange.FloatRange floatRange6 = NumberRange.FloatRange.fromJson(jsonObject3.get("z"));
        final DimensionType dimensionType7 = jsonObject2.has("dimension") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject2, "dimension"))) : null;
        final StructureFeature<?> structureFeature8 = jsonObject2.has("feature") ? Feature.STRUCTURES.get(JsonHelper.getString(jsonObject2, "feature")) : null;
        Biome biome9 = null;
        if (jsonObject2.has("biome")) {
            final Identifier identifier10 = new Identifier(JsonHelper.getString(jsonObject2, "biome"));
            final Object o;
            final Object o2;
            biome9 = Registry.BIOME.getOrEmpty(identifier10).<Throwable>orElseThrow(() -> {
                new JsonSyntaxException("Unknown biome '" + o2 + "'");
                return o;
            });
        }
        return new LocationPredicate(floatRange4, floatRange5, floatRange6, biome9, structureFeature8, dimensionType7);
    }
    
    static {
        ANY = new LocationPredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, null, null, null);
    }
    
    public static class Builder
    {
        private NumberRange.FloatRange x;
        private NumberRange.FloatRange y;
        private NumberRange.FloatRange z;
        @Nullable
        private Biome biome;
        @Nullable
        private StructureFeature<?> feature;
        @Nullable
        private DimensionType dimension;
        
        public Builder() {
            this.x = NumberRange.FloatRange.ANY;
            this.y = NumberRange.FloatRange.ANY;
            this.z = NumberRange.FloatRange.ANY;
        }
        
        public Builder biome(@Nullable final Biome biome) {
            this.biome = biome;
            return this;
        }
        
        public LocationPredicate build() {
            return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension);
        }
    }
}
