package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import java.util.function.Function;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import java.util.stream.Stream;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.BlockState;

public class SimpleBlockFeatureConfig implements FeatureConfig
{
    protected final BlockState toPlace;
    protected final List<BlockState> placeOn;
    protected final List<BlockState> placeIn;
    protected final List<BlockState> placeUnder;
    
    public SimpleBlockFeatureConfig(final BlockState toPlace, final List<BlockState> placeOn, final List<BlockState> placeIn, final List<BlockState> placeUnder) {
        this.toPlace = toPlace;
        this.placeOn = placeOn;
        this.placeIn = placeIn;
        this.placeUnder = placeUnder;
    }
    
    public SimpleBlockFeatureConfig(final BlockState toPlace, final BlockState[] placeOn, final BlockState[] placeIn, final BlockState[] placeUnder) {
        this(toPlace, Lists.<BlockState>newArrayList(placeOn), Lists.<BlockState>newArrayList(placeIn), Lists.<BlockState>newArrayList(placeUnder));
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        final T object2 = (T)BlockState.<T>serialize(ops, this.toPlace).getValue();
        final T object3 = (T)ops.createList((Stream)this.placeOn.stream().map(blockState -> BlockState.<T>serialize(ops, blockState).getValue()));
        final T object4 = (T)ops.createList((Stream)this.placeIn.stream().map(blockState -> BlockState.<T>serialize(ops, blockState).getValue()));
        final T object5 = (T)ops.createList((Stream)this.placeUnder.stream().map(blockState -> BlockState.<T>serialize(ops, blockState).getValue()));
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, ops.createMap((Map)ImmutableMap.<Object, T>of(ops.createString("to_place"), object2, ops.createString("place_on"), object3, ops.createString("place_in"), object4, ops.createString("place_under"), object5)));
    }
    
    public static <T> SimpleBlockFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final BlockState blockState2 = dynamic.get("to_place").map((Function)BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
        final List<BlockState> list3 = (List<BlockState>)dynamic.get("place_on").asList((Function)BlockState::deserialize);
        final List<BlockState> list4 = (List<BlockState>)dynamic.get("place_in").asList((Function)BlockState::deserialize);
        final List<BlockState> list5 = (List<BlockState>)dynamic.get("place_under").asList((Function)BlockState::deserialize);
        return new SimpleBlockFeatureConfig(blockState2, list3, list4, list5);
    }
}
