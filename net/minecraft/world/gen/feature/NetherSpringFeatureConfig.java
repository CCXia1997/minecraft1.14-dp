package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NetherSpringFeatureConfig implements FeatureConfig
{
    public final boolean insideRock;
    
    public NetherSpringFeatureConfig(final boolean insideRock) {
        this.insideRock = insideRock;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("inside_rock"), ops.createBoolean(this.insideRock))));
    }
    
    public static <T> NetherSpringFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final boolean boolean2 = dynamic.get("inside_rock").asBoolean(false);
        return new NetherSpringFeatureConfig(boolean2);
    }
}
