package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class RangeDecoratorConfig implements DecoratorConfig
{
    public final int count;
    public final int bottomOffset;
    public final int topOffset;
    public final int maximum;
    
    public RangeDecoratorConfig(final int count, final int bottomOffset, final int topOffset, final int maximum) {
        this.count = count;
        this.bottomOffset = bottomOffset;
        this.topOffset = topOffset;
        this.maximum = maximum;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("count"), ops.createInt(this.count), ops.createString("bottom_offset"), ops.createInt(this.bottomOffset), ops.createString("top_offset"), ops.createInt(this.topOffset), ops.createString("maximum"), ops.createInt(this.maximum))));
    }
    
    public static RangeDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final int integer3 = dynamic.get("bottom_offset").asInt(0);
        final int integer4 = dynamic.get("top_offset").asInt(0);
        final int integer5 = dynamic.get("maximum").asInt(0);
        return new RangeDecoratorConfig(integer2, integer3, integer4, integer5);
    }
}
