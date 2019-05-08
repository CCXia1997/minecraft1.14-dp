package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountExtraChanceDecoratorConfig implements DecoratorConfig
{
    public final int count;
    public final float extraChance;
    public final int extraCount;
    
    public CountExtraChanceDecoratorConfig(final int count, final float extraChance, final int extraCount) {
        this.count = count;
        this.extraChance = extraChance;
        this.extraCount = extraCount;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("count"), ops.createInt(this.count), ops.createString("extra_chance"), ops.createFloat(this.extraChance), ops.createString("extra_count"), ops.createInt(this.extraCount))));
    }
    
    public static CountExtraChanceDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        final float float3 = dynamic.get("extra_chance").asFloat(0.0f);
        final int integer3 = dynamic.get("extra_count").asInt(0);
        return new CountExtraChanceDecoratorConfig(integer2, float3, integer3);
    }
}
