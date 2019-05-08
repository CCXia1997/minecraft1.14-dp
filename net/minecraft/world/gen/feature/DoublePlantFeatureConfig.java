package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;

public class DoublePlantFeatureConfig implements FeatureConfig
{
    public final BlockState state;
    
    public DoublePlantFeatureConfig(final BlockState state) {
        this.state = state;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("state"), BlockState.<T>serialize(ops, this.state).getValue())));
    }
    
    public static <T> DoublePlantFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final BlockState blockState2 = dynamic.get("state").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new DoublePlantFeatureConfig(blockState2);
    }
}
