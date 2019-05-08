package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountDecoratorConfig implements DecoratorConfig
{
    public final int count;
    
    public CountDecoratorConfig(final int count) {
        this.count = count;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("count"), ops.createInt(this.count))));
    }
    
    public static CountDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("count").asInt(0);
        return new CountDecoratorConfig(integer2);
    }
}
