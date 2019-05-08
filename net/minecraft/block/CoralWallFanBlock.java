package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CoralWallFanBlock extends DeadCoralWallFanBlock
{
    private final Block deadCoralBlock;
    
    protected CoralWallFanBlock(final Block block, final Settings settings) {
        super(settings);
        this.deadCoralBlock = block;
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        this.checkLivingConditions(state, world, pos);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!CoralParentBlock.isInWater(state, world, pos)) {
            world.setBlockState(pos, (((AbstractPropertyContainer<O, BlockState>)this.deadCoralBlock.getDefaultState()).with((Property<Comparable>)CoralWallFanBlock.WATERLOGGED, false)).<Comparable, Comparable>with((Property<Comparable>)CoralWallFanBlock.FACING, (Comparable)state.<V>get((Property<V>)CoralWallFanBlock.FACING)), 2);
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing.getOpposite() == state.<Direction>get((Property<Direction>)CoralWallFanBlock.FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (state.<Boolean>get((Property<Boolean>)CoralWallFanBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        this.checkLivingConditions(state, world, pos);
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
}
