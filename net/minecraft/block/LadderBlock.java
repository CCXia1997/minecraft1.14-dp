package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class LadderBlock extends Block implements Waterloggable
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape EAST_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape NORTH_SHAPE;
    
    protected LadderBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)LadderBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)LadderBlock.WATERLOGGED, false));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        switch (state.<Direction>get((Property<Direction>)LadderBlock.FACING)) {
            case NORTH: {
                return LadderBlock.NORTH_SHAPE;
            }
            case SOUTH: {
                return LadderBlock.SOUTH_SHAPE;
            }
            case WEST: {
                return LadderBlock.WEST_SHAPE;
            }
            default: {
                return LadderBlock.EAST_SHAPE;
            }
        }
    }
    
    private boolean canPlaceOn(final BlockView world, final BlockPos pos, final Direction side) {
        final BlockState blockState4 = world.getBlockState(pos);
        return !blockState4.emitsRedstonePower() && Block.isSolidFullSquare(blockState4, world, pos, side);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final Direction direction4 = state.<Direction>get((Property<Direction>)LadderBlock.FACING);
        return this.canPlaceOn(world, pos.offset(direction4.getOpposite()), direction4);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing.getOpposite() == state.<Direction>get((Property<Direction>)LadderBlock.FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (state.<Boolean>get((Property<Boolean>)LadderBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        if (!ctx.c()) {
            final BlockState blockState2 = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(ctx.getFacing().getOpposite()));
            if (blockState2.getBlock() == this && blockState2.<Comparable>get((Property<Comparable>)LadderBlock.FACING) == ctx.getFacing()) {
                return null;
            }
        }
        BlockState blockState2 = this.getDefaultState();
        final ViewableWorld viewableWorld3 = ctx.getWorld();
        final BlockPos blockPos4 = ctx.getBlockPos();
        final FluidState fluidState5 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        for (final Direction direction9 : ctx.getPlacementFacings()) {
            if (direction9.getAxis().isHorizontal()) {
                blockState2 = ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Direction>with((Property<Comparable>)LadderBlock.FACING, direction9.getOpposite());
                if (blockState2.canPlaceAt(viewableWorld3, blockPos4)) {
                    return ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Boolean>with((Property<Comparable>)LadderBlock.WATERLOGGED, fluidState5.getFluid() == Fluids.WATER);
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)LadderBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)LadderBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)LadderBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(LadderBlock.FACING, LadderBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)LadderBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        WATERLOGGED = Properties.WATERLOGGED;
        EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
        WEST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    }
}
