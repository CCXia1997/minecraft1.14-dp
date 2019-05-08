package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.world.IWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;

public class WallBlock extends HorizontalConnectedBlock
{
    public static final BooleanProperty UP;
    private final VoxelShape[] UP_OUTLINE_SHAPES;
    private final VoxelShape[] UP_COLLISION_SHAPES;
    
    public WallBlock(final Settings settings) {
        super(0.0f, 3.0f, 0.0f, 14.0f, 24.0f, settings);
        this.setDefaultState((((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)WallBlock.UP, true)).with((Property<Comparable>)WallBlock.NORTH, false)).with((Property<Comparable>)WallBlock.EAST, false)).with((Property<Comparable>)WallBlock.SOUTH, false)).with((Property<Comparable>)WallBlock.WEST, false)).<Comparable, Boolean>with((Property<Comparable>)WallBlock.WATERLOGGED, false));
        this.UP_OUTLINE_SHAPES = this.createShapes(4.0f, 3.0f, 16.0f, 0.0f, 14.0f);
        this.UP_COLLISION_SHAPES = this.createShapes(4.0f, 3.0f, 24.0f, 0.0f, 24.0f);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        if (state.<Boolean>get((Property<Boolean>)WallBlock.UP)) {
            return this.UP_OUTLINE_SHAPES[this.getShapeIndex(state)];
        }
        return super.getOutlineShape(state, view, pos, verticalEntityPosition);
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        if (state.<Boolean>get((Property<Boolean>)WallBlock.UP)) {
            return this.UP_COLLISION_SHAPES[this.getShapeIndex(state)];
        }
        return super.getCollisionShape(state, view, pos, ePos);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    private boolean shouldConnectTo(final BlockState state, final boolean faceFullSquare, final Direction side) {
        final Block block4 = state.getBlock();
        final boolean boolean5 = block4.matches(BlockTags.z) || (block4 instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, side));
        return (!Block.canConnect(block4) && faceFullSquare) || boolean5;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final ViewableWorld viewableWorld2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        final FluidState fluidState4 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        final BlockPos blockPos4 = blockPos3.north();
        final BlockPos blockPos5 = blockPos3.east();
        final BlockPos blockPos6 = blockPos3.south();
        final BlockPos blockPos7 = blockPos3.west();
        final BlockState blockState9 = viewableWorld2.getBlockState(blockPos4);
        final BlockState blockState10 = viewableWorld2.getBlockState(blockPos5);
        final BlockState blockState11 = viewableWorld2.getBlockState(blockPos6);
        final BlockState blockState12 = viewableWorld2.getBlockState(blockPos7);
        final boolean boolean13 = this.shouldConnectTo(blockState9, Block.isSolidFullSquare(blockState9, viewableWorld2, blockPos4, Direction.SOUTH), Direction.SOUTH);
        final boolean boolean14 = this.shouldConnectTo(blockState10, Block.isSolidFullSquare(blockState10, viewableWorld2, blockPos5, Direction.WEST), Direction.WEST);
        final boolean boolean15 = this.shouldConnectTo(blockState11, Block.isSolidFullSquare(blockState11, viewableWorld2, blockPos6, Direction.NORTH), Direction.NORTH);
        final boolean boolean16 = this.shouldConnectTo(blockState12, Block.isSolidFullSquare(blockState12, viewableWorld2, blockPos7, Direction.EAST), Direction.EAST);
        final boolean boolean17 = (!boolean13 || boolean14 || !boolean15 || boolean16) && (boolean13 || !boolean14 || boolean15 || !boolean16);
        return (((((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)WallBlock.UP, boolean17 || !viewableWorld2.isAir(blockPos3.up()))).with((Property<Comparable>)WallBlock.NORTH, boolean13)).with((Property<Comparable>)WallBlock.EAST, boolean14)).with((Property<Comparable>)WallBlock.SOUTH, boolean15)).with((Property<Comparable>)WallBlock.WEST, boolean16)).<Comparable, Boolean>with((Property<Comparable>)WallBlock.WATERLOGGED, fluidState4.getFluid() == Fluids.WATER);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)WallBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (facing == Direction.DOWN) {
            return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        }
        final Direction direction7 = facing.getOpposite();
        final boolean boolean8 = (facing == Direction.NORTH) ? this.shouldConnectTo(neighborState, Block.isSolidFullSquare(neighborState, world, neighborPos, direction7), direction7) : state.<Boolean>get((Property<Boolean>)WallBlock.NORTH);
        final boolean boolean9 = (facing == Direction.EAST) ? this.shouldConnectTo(neighborState, Block.isSolidFullSquare(neighborState, world, neighborPos, direction7), direction7) : state.<Boolean>get((Property<Boolean>)WallBlock.EAST);
        final boolean boolean10 = (facing == Direction.SOUTH) ? this.shouldConnectTo(neighborState, Block.isSolidFullSquare(neighborState, world, neighborPos, direction7), direction7) : state.<Boolean>get((Property<Boolean>)WallBlock.SOUTH);
        final boolean boolean11 = (facing == Direction.WEST) ? this.shouldConnectTo(neighborState, Block.isSolidFullSquare(neighborState, world, neighborPos, direction7), direction7) : state.<Boolean>get((Property<Boolean>)WallBlock.WEST);
        final boolean boolean12 = (!boolean8 || boolean9 || !boolean10 || boolean11) && (boolean8 || !boolean9 || boolean10 || !boolean11);
        return ((((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)WallBlock.UP, boolean12 || !world.isAir(pos.up()))).with((Property<Comparable>)WallBlock.NORTH, boolean8)).with((Property<Comparable>)WallBlock.EAST, boolean9)).with((Property<Comparable>)WallBlock.SOUTH, boolean10)).<Comparable, Boolean>with((Property<Comparable>)WallBlock.WEST, boolean11);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(WallBlock.UP, WallBlock.NORTH, WallBlock.EAST, WallBlock.WEST, WallBlock.SOUTH, WallBlock.WATERLOGGED);
    }
    
    static {
        UP = Properties.UP_BOOL;
    }
}
