package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Property;
import net.minecraft.world.BlockView;
import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.shape.VoxelShape;

public class CoralBlock extends CoralParentBlock
{
    private final Block deadCoralBlock;
    protected static final VoxelShape SHAPE;
    
    protected CoralBlock(final Block block, final Settings settings) {
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
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)this.deadCoralBlock.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)CoralBlock.WATERLOGGED, false), 2);
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        this.checkLivingConditions(state, world, pos);
        if (state.<Boolean>get((Property<Boolean>)CoralBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return CoralBlock.SHAPE;
    }
    
    static {
        SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);
    }
}
