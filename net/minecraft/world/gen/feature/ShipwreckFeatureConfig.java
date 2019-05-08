package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ShipwreckFeatureConfig implements FeatureConfig
{
    public final boolean isBeached;
    
    public ShipwreckFeatureConfig(final boolean isBeached) {
        this.isBeached = isBeached;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("is_beached"), ops.createBoolean(this.isBeached))));
    }
    
    public static <T> ShipwreckFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final boolean boolean2 = dynamic.get("is_beached").asBoolean(false);
        return new ShipwreckFeatureConfig(boolean2);
    }
}
