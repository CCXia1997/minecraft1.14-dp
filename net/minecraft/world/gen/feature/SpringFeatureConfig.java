package net.minecraft.world.gen.feature;

import net.minecraft.fluid.Fluids;
import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.fluid.FluidState;

public class SpringFeatureConfig implements FeatureConfig
{
    public final FluidState state;
    
    public SpringFeatureConfig(final FluidState state) {
        this.state = state;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("state"), FluidState.<T>serialize(ops, this.state).getValue())));
    }
    
    public static <T> SpringFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final FluidState fluidState2 = dynamic.get("state").map((Function)FluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState());
        return new SpringFeatureConfig(fluidState2);
    }
}
