package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class PillagerOutpostFeatureConfig implements FeatureConfig
{
    public final double probability;
    
    public PillagerOutpostFeatureConfig(final double probability) {
        this.probability = probability;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("probability"), ops.createDouble(this.probability))));
    }
    
    public static <T> PillagerOutpostFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final float float2 = dynamic.get("probability").asFloat(0.0f);
        return new PillagerOutpostFeatureConfig(float2);
    }
}
