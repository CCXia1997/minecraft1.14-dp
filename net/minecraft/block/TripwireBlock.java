package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Vec3i;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.BooleanProperty;

public class TripwireBlock extends Block
{
    public static final BooleanProperty POWERED;
    public static final BooleanProperty ATTACHED;
    public static final BooleanProperty DISARMED;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES;
    protected static final VoxelShape ATTACHED_SHAPE;
    protected static final VoxelShape DETACHED_SHAPE;
    private final TripwireHookBlock hookBlock;
    
    public TripwireBlock(final TripwireHookBlock hookBlock, final Settings settings) {
        super(settings);
        this.setDefaultState(((((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)TripwireBlock.POWERED, false)).with((Property<Comparable>)TripwireBlock.ATTACHED, false)).with((Property<Comparable>)TripwireBlock.DISARMED, false)).with((Property<Comparable>)TripwireBlock.NORTH, false)).with((Property<Comparable>)TripwireBlock.EAST, false)).with((Property<Comparable>)TripwireBlock.SOUTH, false)).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.WEST, false));
        this.hookBlock = hookBlock;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return state.<Boolean>get((Property<Boolean>)TripwireBlock.ATTACHED) ? TripwireBlock.ATTACHED_SHAPE : TripwireBlock.DETACHED_SHAPE;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockView blockView2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        return (((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)TripwireBlock.NORTH, this.shouldConnectTo(blockView2.getBlockState(blockPos3.north()), Direction.NORTH))).with((Property<Comparable>)TripwireBlock.EAST, this.shouldConnectTo(blockView2.getBlockState(blockPos3.east()), Direction.EAST))).with((Property<Comparable>)TripwireBlock.SOUTH, this.shouldConnectTo(blockView2.getBlockState(blockPos3.south()), Direction.SOUTH))).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.WEST, this.shouldConnectTo(blockView2.getBlockState(blockPos3.west()), Direction.WEST));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing.getAxis().isHorizontal()) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.FACING_PROPERTIES.get(facing), this.shouldConnectTo(neighborState, facing));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        this.update(world, pos, state);
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (boolean5 || state.getBlock() == newState.getBlock()) {
            return;
        }
        this.update(world, pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.POWERED, true));
    }
    
    @Override
    public void onBreak(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        if (!world.isClient && !player.getMainHandStack().isEmpty() && player.getMainHandStack().getItem() == Items.lW) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.DISARMED, true), 4);
        }
        super.onBreak(world, pos, state, player);
    }
    
    private void update(final World world, final BlockPos blockPos, final BlockState blockState) {
        for (final Direction direction7 : new Direction[] { Direction.SOUTH, Direction.WEST }) {
            int integer8 = 1;
            while (integer8 < 42) {
                final BlockPos blockPos2 = blockPos.offset(direction7, integer8);
                final BlockState blockState2 = world.getBlockState(blockPos2);
                if (blockState2.getBlock() == this.hookBlock) {
                    if (blockState2.<Comparable>get((Property<Comparable>)TripwireHookBlock.FACING) == direction7.getOpposite()) {
                        this.hookBlock.update(world, blockPos2, blockState2, false, true, integer8, blockState);
                        break;
                    }
                    break;
                }
                else {
                    if (blockState2.getBlock() != this) {
                        break;
                    }
                    ++integer8;
                }
            }
        }
    }
    
    @Override
    public void onEntityCollision(final BlockState state, final World world, final BlockPos pos, final Entity entity) {
        if (world.isClient) {
            return;
        }
        if (state.<Boolean>get((Property<Boolean>)TripwireBlock.POWERED)) {
            return;
        }
        this.updatePowered(world, pos);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.isClient) {
            return;
        }
        if (!world.getBlockState(pos).<Boolean>get((Property<Boolean>)TripwireBlock.POWERED)) {
            return;
        }
        this.updatePowered(world, pos);
    }
    
    private void updatePowered(final World world, final BlockPos blockPos) {
        BlockState blockState3 = world.getBlockState(blockPos);
        final boolean boolean4 = blockState3.<Boolean>get((Property<Boolean>)TripwireBlock.POWERED);
        boolean boolean5 = false;
        final List<? extends Entity> list6 = world.getEntities((Entity)null, blockState3.getOutlineShape(world, blockPos).getBoundingBox().offset(blockPos));
        if (!list6.isEmpty()) {
            for (final Entity entity8 : list6) {
                if (!entity8.canAvoidTraps()) {
                    boolean5 = true;
                    break;
                }
            }
        }
        if (boolean5 != boolean4) {
            blockState3 = ((AbstractPropertyContainer<O, BlockState>)blockState3).<Comparable, Boolean>with((Property<Comparable>)TripwireBlock.POWERED, boolean5);
            world.setBlockState(blockPos, blockState3, 3);
            this.update(world, blockPos, blockState3);
        }
        if (boolean5) {
            world.getBlockTickScheduler().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
        }
    }
    
    public boolean shouldConnectTo(final BlockState state, final Direction facing) {
        final Block block3 = state.getBlock();
        if (block3 == this.hookBlock) {
            return state.<Comparable>get((Property<Comparable>)TripwireHookBlock.FACING) == facing.getOpposite();
        }
        return block3 == this;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        switch (rotation) {
            case ROT_180: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)TripwireBlock.NORTH, (Comparable)state.<V>get((Property<V>)TripwireBlock.SOUTH))).with((Property<Comparable>)TripwireBlock.EAST, (Comparable)state.<V>get((Property<V>)TripwireBlock.WEST))).with((Property<Comparable>)TripwireBlock.SOUTH, (Comparable)state.<V>get((Property<V>)TripwireBlock.NORTH))).<Comparable, Comparable>with((Property<Comparable>)TripwireBlock.WEST, (Comparable)state.<V>get((Property<V>)TripwireBlock.EAST));
            }
            case ROT_270: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)TripwireBlock.NORTH, (Comparable)state.<V>get((Property<V>)TripwireBlock.EAST))).with((Property<Comparable>)TripwireBlock.EAST, (Comparable)state.<V>get((Property<V>)TripwireBlock.SOUTH))).with((Property<Comparable>)TripwireBlock.SOUTH, (Comparable)state.<V>get((Property<V>)TripwireBlock.WEST))).<Comparable, Comparable>with((Property<Comparable>)TripwireBlock.WEST, (Comparable)state.<V>get((Property<V>)TripwireBlock.NORTH));
            }
            case ROT_90: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)TripwireBlock.NORTH, (Comparable)state.<V>get((Property<V>)TripwireBlock.WEST))).with((Property<Comparable>)TripwireBlock.EAST, (Comparable)state.<V>get((Property<V>)TripwireBlock.NORTH))).with((Property<Comparable>)TripwireBlock.SOUTH, (Comparable)state.<V>get((Property<V>)TripwireBlock.EAST))).<Comparable, Comparable>with((Property<Comparable>)TripwireBlock.WEST, (Comparable)state.<V>get((Property<V>)TripwireBlock.SOUTH));
            }
            default: {
                return state;
            }
        }
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)TripwireBlock.NORTH, (Comparable)state.<V>get((Property<V>)TripwireBlock.SOUTH))).<Comparable, Comparable>with((Property<Comparable>)TripwireBlock.SOUTH, (Comparable)state.<V>get((Property<V>)TripwireBlock.NORTH));
            }
            case FRONT_BACK: {
                return (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)TripwireBlock.EAST, (Comparable)state.<V>get((Property<V>)TripwireBlock.WEST))).<Comparable, Comparable>with((Property<Comparable>)TripwireBlock.WEST, (Comparable)state.<V>get((Property<V>)TripwireBlock.EAST));
            }
            default: {
                return super.mirror(state, mirror);
            }
        }
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(TripwireBlock.POWERED, TripwireBlock.ATTACHED, TripwireBlock.DISARMED, TripwireBlock.NORTH, TripwireBlock.EAST, TripwireBlock.WEST, TripwireBlock.SOUTH);
    }
    
    static {
        POWERED = Properties.POWERED;
        ATTACHED = Properties.ATTACHED;
        DISARMED = Properties.DISARMED;
        NORTH = ConnectedPlantBlock.NORTH;
        EAST = ConnectedPlantBlock.EAST;
        SOUTH = ConnectedPlantBlock.SOUTH;
        WEST = ConnectedPlantBlock.WEST;
        FACING_PROPERTIES = HorizontalConnectedBlock.FACING_PROPERTIES;
        ATTACHED_SHAPE = Block.createCuboidShape(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
        DETACHED_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    }
}
