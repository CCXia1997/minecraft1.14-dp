package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class SeagrassFeatureConfig implements FeatureConfig
{
    public final int count;
    public final double tallSeagrassProbability;
    
    public SeagrassFeatureConfig(final int count, final double tallSeagrassProbability) {
        this.count = count;
        this.tallSeagrassProbability = tallSeagrassProbability;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("count"), ops.createInt(this.count), ops.createString("tall_seagrass_probability"), ops.createDouble(this.tallSeagrassProbability))));
    }
    
    public static <T> SeagrassFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final double double3 = dynamic.get("tall_seagrass_probability").asDouble(0.0);
        return new SeagrassFeatureConfig(integer2, double3);
    }
}
