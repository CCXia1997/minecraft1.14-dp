package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.Heightmap;

public class TopSolidHeightmapNoiseBiasedDecoratorConfig implements DecoratorConfig
{
    public final int noiseToCountRatio;
    public final double noiseFactor;
    public final double noiseOffset;
    public final Heightmap.Type heightmap;
    
    public TopSolidHeightmapNoiseBiasedDecoratorConfig(final int noiseToCountRatio, final double noiseFactor, final double noiseOffset, final Heightmap.Type heightmap) {
        this.noiseToCountRatio = noiseToCountRatio;
        this.noiseFactor = noiseFactor;
        this.noiseOffset = noiseOffset;
        this.heightmap = heightmap;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("noise_to_count_ratio"), ops.createInt(this.noiseToCountRatio), ops.createString("noise_factor"), ops.createDouble(this.noiseFactor), ops.createString("noise_offset"), ops.createDouble(this.noiseOffset), ops.createString("heightmap"), ops.createString(this.heightmap.getName()))));
    }
    
    public static TopSolidHeightmapNoiseBiasedDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("noise_to_count_ratio").asInt(10);
        final double double3 = dynamic.get("noise_factor").asDouble(80.0);
        final double double4 = dynamic.get("noise_offset").asDouble(0.0);
        final Heightmap.Type type7 = Heightmap.Type.byName(dynamic.get("heightmap").asString("OCEAN_FLOOR_WG"));
        return new TopSolidHeightmapNoiseBiasedDecoratorConfig(integer2, double3, double4, type7);
    }
}
