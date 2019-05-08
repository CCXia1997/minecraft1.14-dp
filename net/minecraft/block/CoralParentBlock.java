package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.entity.VerticalEntityPosition;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;

public class CoralParentBlock extends Block implements Waterloggable
{
    public static final BooleanProperty WATERLOGGED;
    private static final VoxelShape SHAPE;
    
    protected CoralParentBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)CoralParentBlock.WATERLOGGED, true));
    }
    
    protected void checkLivingConditions(final BlockState blockState, final IWorld iWorld, final BlockPos blockPos) {
        if (!isInWater(blockState, iWorld, blockPos)) {
            iWorld.getBlockTickScheduler().schedule(blockPos, this, 60 + iWorld.getRandom().nextInt(40));
        }
    }
    
    protected static boolean isInWater(final BlockState state, final BlockView world, final BlockPos pos) {
        if (state.<Boolean>get((Property<Boolean>)CoralParentBlock.WATERLOGGED)) {
            return true;
        }
        for (final Direction direction7 : Direction.values()) {
            if (world.getFluidState(pos.offset(direction7)).matches(FluidTags.a)) {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final FluidState fluidState2 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)CoralParentBlock.WATERLOGGED, fluidState2.matches(FluidTags.a) && fluidState2.getLevel() == 8);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return CoralParentBlock.SHAPE;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)CoralParentBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (facing == Direction.DOWN && !this.canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        return Block.isSolidFullSquare(world.getBlockState(blockPos4), world, blockPos4, Direction.UP);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(CoralParentBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)CoralParentBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    static {
        WATERLOGGED = Properties.WATERLOGGED;
        SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    }
}
