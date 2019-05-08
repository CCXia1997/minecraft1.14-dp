package net.minecraft.world.gen;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.carver.CarverConfig;

public class ProbabilityConfig implements CarverConfig, FeatureConfig
{
    public final float probability;
    
    public ProbabilityConfig(final float probability) {
        this.probability = probability;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("probability"), ops.createFloat(this.probability))));
    }
    
    public static <T> ProbabilityConfig deserialize(final Dynamic<T> dynamic) {
        final float float2 = dynamic.get("probability").asFloat(0.0f);
        return new ProbabilityConfig(float2);
    }
}
