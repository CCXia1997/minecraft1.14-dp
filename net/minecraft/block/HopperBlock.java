package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.state.property.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.container.Container;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.stat.Stats;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.block.entity.Hopper;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class HopperBlock extends BlockWithEntity
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty ENABLED;
    private static final VoxelShape c;
    private static final VoxelShape d;
    private static final VoxelShape OUTSIDE_SHAPE;
    private static final VoxelShape DEFAULT_SHAPE;
    private static final VoxelShape DOWN_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;
    private static final VoxelShape DOWN_RAY_TRACE_SHAPE;
    private static final VoxelShape EAST_RAY_TRACE_SHAPE;
    private static final VoxelShape NORTH_RAY_TRACE_SHAPE;
    private static final VoxelShape SOUTH_RAY_TRACE_SHAPE;
    private static final VoxelShape WEST_RAY_TRACE_SHAPE;
    
    public HopperBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)HopperBlock.FACING, Direction.DOWN)).<Comparable, Boolean>with((Property<Comparable>)HopperBlock.ENABLED, true));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        switch (state.<Direction>get((Property<Direction>)HopperBlock.FACING)) {
            case DOWN: {
                return HopperBlock.DOWN_SHAPE;
            }
            case NORTH: {
                return HopperBlock.NORTH_SHAPE;
            }
            case SOUTH: {
                return HopperBlock.SOUTH_SHAPE;
            }
            case WEST: {
                return HopperBlock.WEST_SHAPE;
            }
            case EAST: {
                return HopperBlock.EAST_SHAPE;
            }
            default: {
                return HopperBlock.DEFAULT_SHAPE;
            }
        }
    }
    
    @Override
    public VoxelShape getRayTraceShape(final BlockState state, final BlockView view, final BlockPos pos) {
        switch (state.<Direction>get((Property<Direction>)HopperBlock.FACING)) {
            case DOWN: {
                return HopperBlock.DOWN_RAY_TRACE_SHAPE;
            }
            case NORTH: {
                return HopperBlock.NORTH_RAY_TRACE_SHAPE;
            }
            case SOUTH: {
                return HopperBlock.SOUTH_RAY_TRACE_SHAPE;
            }
            case WEST: {
                return HopperBlock.WEST_RAY_TRACE_SHAPE;
            }
            case EAST: {
                return HopperBlock.EAST_RAY_TRACE_SHAPE;
            }
            default: {
                return Hopper.INSIDE_SHAPE;
            }
        }
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final Direction direction2 = ctx.getFacing().getOpposite();
        return (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)HopperBlock.FACING, (direction2.getAxis() == Direction.Axis.Y) ? Direction.DOWN : direction2)).<Comparable, Boolean>with((Property<Comparable>)HopperBlock.ENABLED, true);
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new HopperBlockEntity();
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            final BlockEntity blockEntity6 = world.getBlockEntity(pos);
            if (blockEntity6 instanceof HopperBlockEntity) {
                ((HopperBlockEntity)blockEntity6).setCustomName(itemStack.getDisplayName());
            }
        }
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        this.updateEnabled(world, pos, state);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (world.isClient) {
            return true;
        }
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (blockEntity7 instanceof HopperBlockEntity) {
            player.openContainer((NameableContainerProvider)blockEntity7);
            player.incrementStat(Stats.ab);
        }
        return true;
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        this.updateEnabled(world, pos, state);
    }
    
    private void updateEnabled(final World world, final BlockPos pos, final BlockState state) {
        final boolean boolean4 = !world.isReceivingRedstonePower(pos);
        if (boolean4 != state.<Boolean>get((Property<Boolean>)HopperBlock.ENABLED)) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)HopperBlock.ENABLED, boolean4), 4);
        }
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (blockEntity6 instanceof HopperBlockEntity) {
            ItemScatterer.spawn(world, pos, (Inventory)blockEntity6);
            world.updateHorizontalAdjacent(pos, this);
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        return Container.calculateComparatorOutput(world.getBlockEntity(pos));
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.MIPPED_CUTOUT;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)HopperBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)HopperBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)HopperBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(HopperBlock.FACING, HopperBlock.ENABLED);
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        final BlockEntity blockEntity5 = world.getBlockEntity(pos);
        if (blockEntity5 instanceof HopperBlockEntity) {
            ((HopperBlockEntity)blockEntity5).onEntityCollided(entity);
        }
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        FACING = Properties.HOPPER_FACING;
        ENABLED = Properties.ENABLED;
        c = Block.createCuboidShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
        d = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
        OUTSIDE_SHAPE = VoxelShapes.union(HopperBlock.d, HopperBlock.c);
        DEFAULT_SHAPE = VoxelShapes.combineAndSimplify(HopperBlock.OUTSIDE_SHAPE, Hopper.INSIDE_SHAPE, BooleanBiFunction.ONLY_FIRST);
        DOWN_SHAPE = VoxelShapes.union(HopperBlock.DEFAULT_SHAPE, Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
        EAST_SHAPE = VoxelShapes.union(HopperBlock.DEFAULT_SHAPE, Block.createCuboidShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
        NORTH_SHAPE = VoxelShapes.union(HopperBlock.DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
        SOUTH_SHAPE = VoxelShapes.union(HopperBlock.DEFAULT_SHAPE, Block.createCuboidShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
        WEST_SHAPE = VoxelShapes.union(HopperBlock.DEFAULT_SHAPE, Block.createCuboidShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
        DOWN_RAY_TRACE_SHAPE = Hopper.INSIDE_SHAPE;
        EAST_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
        NORTH_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
        SOUTH_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
        WEST_RAY_TRACE_SHAPE = VoxelShapes.union(Hopper.INSIDE_SHAPE, Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
    }
}
