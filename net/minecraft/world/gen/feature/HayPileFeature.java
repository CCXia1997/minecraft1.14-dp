package net.minecraft.world.gen.feature;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Property;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class HayPileFeature extends AbstractPileFeature
{
    public HayPileFeature(final Function<Dynamic<?>, ? extends DefaultFeatureConfig> configDeserializer) {
        super(configDeserializer);
    }
    
    @Override
    protected BlockState getPileBlockState(final IWorld world) {
        final Direction.Axis axis2 = Direction.Axis.a(world.getRandom());
        return ((AbstractPropertyContainer<O, BlockState>)Blocks.gs.getDefaultState()).<Direction.Axis, Direction.Axis>with(PillarBlock.AXIS, axis2);
    }
}
