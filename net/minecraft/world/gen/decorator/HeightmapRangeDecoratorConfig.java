package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HeightmapRangeDecoratorConfig implements DecoratorConfig
{
    public final int min;
    public final int max;
    
    public HeightmapRangeDecoratorConfig(final int min, final int max) {
        this.min = min;
        this.max = max;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("min"), ops.createInt(this.min), ops.createString("max"), ops.createInt(this.max))));
    }
    
    public static HeightmapRangeDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("min").asInt(0);
        final int integer3 = dynamic.get("max").asInt(0);
        return new HeightmapRangeDecoratorConfig(integer2, integer3);
    }
}
