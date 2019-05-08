package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;

public class BoulderFeatureConfig implements FeatureConfig
{
    public final BlockState state;
    public final int startRadius;
    
    public BoulderFeatureConfig(final BlockState state, final int startRadius) {
        this.state = state;
        this.startRadius = startRadius;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("state"), BlockState.<T>serialize(ops, this.state).getValue(), ops.createString("start_radius"), ops.createInt(this.startRadius))));
    }
    
    public static <T> BoulderFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final BlockState blockState2 = dynamic.get("state").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        final int integer3 = dynamic.get("start_radius").asInt(0);
        return new BoulderFeatureConfig(blockState2, integer3);
    }
}
