package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class MineshaftFeatureConfig implements FeatureConfig
{
    public final double probability;
    public final MineshaftFeature.Type type;
    
    public MineshaftFeatureConfig(final double probability, final MineshaftFeature.Type type) {
        this.probability = probability;
        this.type = type;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("probability"), ops.createDouble(this.probability), ops.createString("type"), ops.createString(this.type.getName()))));
    }
    
    public static <T> MineshaftFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final float float2 = dynamic.get("probability").asFloat(0.0f);
        final MineshaftFeature.Type type3 = MineshaftFeature.Type.byName(dynamic.get("type").asString(""));
        return new MineshaftFeatureConfig(float2, type3);
    }
}
