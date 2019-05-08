package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.world.IWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import javax.annotation.Nullable;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.BooleanProperty;

public class TrapdoorBlock extends HorizontalFacingBlock implements Waterloggable
{
    public static final BooleanProperty OPEN;
    public static final EnumProperty<BlockHalf> HALF;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape EAST_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape OPEN_BOTTOM_SHAPE;
    protected static final VoxelShape OPEN_TOP_SHAPE;
    
    protected TrapdoorBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)TrapdoorBlock.FACING, Direction.NORTH)).with((Property<Comparable>)TrapdoorBlock.OPEN, false)).with(TrapdoorBlock.HALF, BlockHalf.BOTTOM)).with((Property<Comparable>)TrapdoorBlock.POWERED, false)).<Comparable, Boolean>with((Property<Comparable>)TrapdoorBlock.WATERLOGGED, false));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        if (!state.<Boolean>get((Property<Boolean>)TrapdoorBlock.OPEN)) {
            return (state.<BlockHalf>get(TrapdoorBlock.HALF) == BlockHalf.TOP) ? TrapdoorBlock.OPEN_TOP_SHAPE : TrapdoorBlock.OPEN_BOTTOM_SHAPE;
        }
        switch (state.<Direction>get((Property<Direction>)TrapdoorBlock.FACING)) {
            default: {
                return TrapdoorBlock.NORTH_SHAPE;
            }
            case SOUTH: {
                return TrapdoorBlock.SOUTH_SHAPE;
            }
            case WEST: {
                return TrapdoorBlock.WEST_SHAPE;
            }
            case EAST: {
                return TrapdoorBlock.EAST_SHAPE;
            }
        }
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        switch (env) {
            case a: {
                return world.<Boolean>get((Property<Boolean>)TrapdoorBlock.OPEN);
            }
            case b: {
                return world.<Boolean>get((Property<Boolean>)TrapdoorBlock.WATERLOGGED);
            }
            case c: {
                return world.<Boolean>get((Property<Boolean>)TrapdoorBlock.OPEN);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean activate(BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (this.material == Material.METAL) {
            return false;
        }
        state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)TrapdoorBlock.OPEN);
        world.setBlockState(pos, state, 2);
        if (state.<Boolean>get((Property<Boolean>)TrapdoorBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        this.playToggleSound(player, world, pos, state.<Boolean>get((Property<Boolean>)TrapdoorBlock.OPEN));
        return true;
    }
    
    protected void playToggleSound(@Nullable final PlayerEntity player, final World world, final BlockPos pos, final boolean open) {
        if (open) {
            final int integer5 = (this.material == Material.METAL) ? 1037 : 1007;
            world.playLevelEvent(player, integer5, pos, 0);
        }
        else {
            final int integer5 = (this.material == Material.METAL) ? 1036 : 1013;
            world.playLevelEvent(player, integer5, pos, 0);
        }
    }
    
    @Override
    public void neighborUpdate(BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (world.isClient) {
            return;
        }
        final boolean boolean7 = world.isReceivingRedstonePower(pos);
        if (boolean7 != state.<Boolean>get((Property<Boolean>)TrapdoorBlock.POWERED)) {
            if (state.<Boolean>get((Property<Boolean>)TrapdoorBlock.OPEN) != boolean7) {
                state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)TrapdoorBlock.OPEN, boolean7);
                this.playToggleSound(null, world, pos, boolean7);
            }
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)TrapdoorBlock.POWERED, boolean7), 2);
            if (state.<Boolean>get((Property<Boolean>)TrapdoorBlock.WATERLOGGED)) {
                world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }
        }
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        BlockState blockState2 = this.getDefaultState();
        final FluidState fluidState3 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        final Direction direction4 = ctx.getFacing();
        if (ctx.c() || !direction4.getAxis().isHorizontal()) {
            blockState2 = (((AbstractPropertyContainer<O, BlockState>)blockState2).with((Property<Comparable>)TrapdoorBlock.FACING, ctx.getPlayerHorizontalFacing().getOpposite())).<BlockHalf, BlockHalf>with(TrapdoorBlock.HALF, (direction4 == Direction.UP) ? BlockHalf.BOTTOM : BlockHalf.TOP);
        }
        else {
            blockState2 = (((AbstractPropertyContainer<O, BlockState>)blockState2).with((Property<Comparable>)TrapdoorBlock.FACING, direction4)).<BlockHalf, BlockHalf>with(TrapdoorBlock.HALF, (ctx.getPos().y - ctx.getBlockPos().getY() > 0.5) ? BlockHalf.TOP : BlockHalf.BOTTOM);
        }
        if (ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())) {
            blockState2 = (((AbstractPropertyContainer<O, BlockState>)blockState2).with((Property<Comparable>)TrapdoorBlock.OPEN, true)).<Comparable, Boolean>with((Property<Comparable>)TrapdoorBlock.POWERED, true);
        }
        return ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Boolean>with((Property<Comparable>)TrapdoorBlock.WATERLOGGED, fluidState3.getFluid() == Fluids.WATER);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(TrapdoorBlock.FACING, TrapdoorBlock.OPEN, TrapdoorBlock.HALF, TrapdoorBlock.POWERED, TrapdoorBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)TrapdoorBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)TrapdoorBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    static {
        OPEN = Properties.OPEN;
        HALF = Properties.BLOCK_HALF;
        POWERED = Properties.POWERED;
        WATERLOGGED = Properties.WATERLOGGED;
        EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
        WEST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
        OPEN_BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
        OPEN_TOP_SHAPE = Block.createCuboidShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
    }
}
