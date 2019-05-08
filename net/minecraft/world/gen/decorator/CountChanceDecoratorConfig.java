package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountChanceDecoratorConfig implements DecoratorConfig
{
    public final int count;
    public final float chance;
    
    public CountChanceDecoratorConfig(final int count, final float chance) {
        this.count = count;
        this.chance = chance;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("count"), ops.createInt(this.count), ops.createString("chance"), ops.createFloat(this.chance))));
    }
    
    public static CountChanceDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final float float3 = dynamic.get("chance").asFloat(0.0f);
        return new CountChanceDecoratorConfig(integer2, float3);
    }
}
