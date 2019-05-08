package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;

public class HayBlock extends PillarBlock
{
    public HayBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Direction.Axis, Direction.Axis>with(HayBlock.AXIS, Direction.Axis.Y));
    }
    
    @Override
    public void onLandedUpon(final World world, final BlockPos pos, final Entity entity, final float distance) {
        entity.handleFallDamage(distance, 0.2f);
    }
}
