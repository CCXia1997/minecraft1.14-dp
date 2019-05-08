package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountDepthDecoratorConfig implements DecoratorConfig
{
    public final int count;
    public final int baseline;
    public final int spread;
    
    public CountDepthDecoratorConfig(final int count, final int baseline, final int spread) {
        this.count = count;
        this.baseline = baseline;
        this.spread = spread;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("count"), ops.createInt(this.count), ops.createString("baseline"), ops.createInt(this.baseline), ops.createString("spread"), ops.createInt(this.spread))));
    }
    
    public static CountDepthDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final int integer3 = dynamic.get("baseline").asInt(0);
        final int integer4 = dynamic.get("spread").asInt(0);
        return new CountDepthDecoratorConfig(integer2, integer3, integer4);
    }
}
