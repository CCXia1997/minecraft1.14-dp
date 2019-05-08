package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class IcePatchFeatureConfig implements FeatureConfig
{
    public final int radius;
    
    public IcePatchFeatureConfig(final int radius) {
        this.radius = radius;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("radius"), ops.createInt(this.radius))));
    }
    
    public static <T> IcePatchFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final int integer2 = dynamic.get("radius").asInt(0);
        return new IcePatchFeatureConfig(integer2);
    }
}
