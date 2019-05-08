package net.minecraft.world.gen.decorator;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DungeonDecoratorConfig implements DecoratorConfig
{
    public final int chance;
    
    public DungeonDecoratorConfig(final int chance) {
        this.chance = chance;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("chance"), ops.createInt(this.chance))));
    }
    
    public static DungeonDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        final int integer2 = dynamic.get("chance").asInt(0);
        return new DungeonDecoratorConfig(integer2);
    }
}
