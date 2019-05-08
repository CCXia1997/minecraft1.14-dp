package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Stream;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.block.BlockState;

public class DiskFeatureConfig implements FeatureConfig
{
    public final BlockState state;
    public final int radius;
    public final int ySize;
    public final List<BlockState> targets;
    
    public DiskFeatureConfig(final BlockState state, final int radius, final int ySize, final List<BlockState> targets) {
        this.state = state;
        this.radius = radius;
        this.ySize = ySize;
        this.targets = targets;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.of(ops.createString("state"), BlockState.<T>serialize(ops, this.state).getValue(), ops.createString("radius"), ops.createInt(this.radius), ops.createString("y_size"), ops.createInt(this.ySize), ops.createString("targets"), ops.createList((Stream)this.targets.stream().map(blockState -> BlockState.<T>serialize(ops, blockState).getValue())))));
    }
    
    public static <T> DiskFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final BlockState blockState2 = dynamic.get("state").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        final int integer3 = dynamic.get("radius").asInt(0);
        final int integer4 = dynamic.get("y_size").asInt(0);
        final List<BlockState> list5 = (List<BlockState>)dynamic.get("targets").asList((Function)BlockState::deserialize);
        return new DiskFeatureConfig(blockState2, integer3, integer4, list5);
    }
}
