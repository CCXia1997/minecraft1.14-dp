package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;

public class FillLayerFeatureConfig implements FeatureConfig
{
    public final int height;
    public final BlockState state;
    
    public FillLayerFeatureConfig(final int height, final BlockState state) {
        this.height = height;
        this.state = state;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("height"), ops.createInt(this.height), ops.createString("state"), BlockState.<T>serialize(ops, this.state).getValue())));
    }
    
    public static <T> FillLayerFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final int integer2 = dynamic.get("height").asInt(0);
        final BlockState blockState3 = dynamic.get("state").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        return new FillLayerFeatureConfig(integer2, blockState3);
    }
}
