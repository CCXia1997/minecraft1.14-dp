package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeadItem;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;

public class FenceBlock extends HorizontalConnectedBlock
{
    private final VoxelShape[] SHAPES;
    
    public FenceBlock(final Settings settings) {
        super(2.0f, 2.0f, 16.0f, 16.0f, 24.0f, settings);
        this.setDefaultState(((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)FenceBlock.NORTH, false)).with((Property<Comparable>)FenceBlock.EAST, false)).with((Property<Comparable>)FenceBlock.SOUTH, false)).with((Property<Comparable>)FenceBlock.WEST, false)).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WATERLOGGED, false));
        this.SHAPES = this.createShapes(2.0f, 1.0f, 16.0f, 6.0f, 15.0f);
    }
    
    @Override
    public VoxelShape h(final BlockState state, final BlockView view, final BlockPos pos) {
        return this.SHAPES[this.getShapeIndex(state)];
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    public boolean canConnect(final BlockState blockState, final boolean boolean2, final Direction direction) {
        final Block block4 = blockState.getBlock();
        final boolean boolean3 = block4.matches(BlockTags.G) && blockState.getMaterial() == this.material;
        final boolean boolean4 = block4 instanceof FenceGateBlock && FenceGateBlock.canWallConnect(blockState, direction);
        return (!Block.canConnect(block4) && boolean2) || boolean3 || boolean4;
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            final ItemStack itemStack7 = player.getStackInHand(hand);
            return itemStack7.getItem() == Items.oq || itemStack7.isEmpty();
        }
        return LeadItem.attachNearbyEntities(player, world, pos);
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockView blockView2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        final FluidState fluidState4 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        final BlockPos blockPos4 = blockPos3.north();
        final BlockPos blockPos5 = blockPos3.east();
        final BlockPos blockPos6 = blockPos3.south();
        final BlockPos blockPos7 = blockPos3.west();
        final BlockState blockState9 = blockView2.getBlockState(blockPos4);
        final BlockState blockState10 = blockView2.getBlockState(blockPos5);
        final BlockState blockState11 = blockView2.getBlockState(blockPos6);
        final BlockState blockState12 = blockView2.getBlockState(blockPos7);
        return ((((((AbstractPropertyContainer<O, BlockState>)super.getPlacementState(ctx)).with((Property<Comparable>)FenceBlock.NORTH, this.canConnect(blockState9, Block.isSolidFullSquare(blockState9, blockView2, blockPos4, Direction.SOUTH), Direction.SOUTH))).with((Property<Comparable>)FenceBlock.EAST, this.canConnect(blockState10, Block.isSolidFullSquare(blockState10, blockView2, blockPos5, Direction.WEST), Direction.WEST))).with((Property<Comparable>)FenceBlock.SOUTH, this.canConnect(blockState11, Block.isSolidFullSquare(blockState11, blockView2, blockPos6, Direction.NORTH), Direction.NORTH))).with((Property<Comparable>)FenceBlock.WEST, this.canConnect(blockState12, Block.isSolidFullSquare(blockState12, blockView2, blockPos7, Direction.EAST), Direction.EAST))).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.WATERLOGGED, fluidState4.getFluid() == Fluids.WATER);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)FenceBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (facing.getAxis().getType() == Direction.Type.HORIZONTAL) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)FenceBlock.FACING_PROPERTIES.get(facing), this.canConnect(neighborState, Block.isSolidFullSquare(neighborState, world, neighborPos, facing.getOpposite()), facing.getOpposite()));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FenceBlock.NORTH, FenceBlock.EAST, FenceBlock.WEST, FenceBlock.SOUTH, FenceBlock.WATERLOGGED);
    }
}
