package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NopeDecoratorConfig implements DecoratorConfig
{
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.emptyMap());
    }
    
    public static NopeDecoratorConfig deserialize(final Dynamic<?> dynamic) {
        return new NopeDecoratorConfig();
    }
}
