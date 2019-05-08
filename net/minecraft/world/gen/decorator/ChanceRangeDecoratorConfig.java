package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceRangeDecoratorConfig implements DecoratorConfig
{
    public final float chance;
    public final int bottomOffset;
    public final int topOffset;
    public final int top;
    
    public ChanceRangeDecoratorConfig(final float chance, final int bottomOffset, final int topOffset, final int top) {
        this.chance = chance;
        this.bottomOffset = bottomOffset;
        this.topOffset = topOffset;
        this.top = top;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("chance"), ops.createFloat(this.chance), ops.createString("bottom_offset"), ops.createInt(this.bottomOffset), ops.createString("top_offset"), ops.createInt(this.topOffset), ops.createString("top"), ops.createInt(this.top))));
    }
    
    public static ChanceRangeDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final float float2 = dynamic.get("chance").asFloat(0.0f);
        final int integer3 = dynamic.get("bottom_offset").asInt(0);
        final int integer4 = dynamic.get("top_offset").asInt(0);
        final int integer5 = dynamic.get("top").asInt(0);
        return new ChanceRangeDecoratorConfig(float2, integer3, integer4, integer5);
    }
}
