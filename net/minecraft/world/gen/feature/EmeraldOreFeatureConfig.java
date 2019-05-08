package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;

public class EmeraldOreFeatureConfig implements FeatureConfig
{
    public final BlockState target;
    public final BlockState state;
    
    public EmeraldOreFeatureConfig(final BlockState target, final BlockState state) {
        this.target = target;
        this.state = state;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("target"), BlockState.<T>serialize(ops, this.target).getValue(), ops.createString("state"), BlockState.<T>serialize(ops, this.state).getValue())));
    }
    
    public static <T> EmeraldOreFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final BlockState blockState2 = dynamic.get("target").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        final BlockState blockState3 = dynamic.get("state").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new EmeraldOreFeatureConfig(blockState2, blockState3);
    }
}
