package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import java.util.stream.Collector;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.state.StateFactory;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import java.util.Iterator;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.BooleanProperty;

public class VineBlock extends Block
{
    public static final BooleanProperty UP;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES;
    protected static final VoxelShape UP_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape EAST_SHAPE;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    
    public VineBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)VineBlock.UP, false)).with((Property<Comparable>)VineBlock.NORTH, false)).with((Property<Comparable>)VineBlock.EAST, false)).with((Property<Comparable>)VineBlock.SOUTH, false)).<Comparable, Boolean>with((Property<Comparable>)VineBlock.WEST, false));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        VoxelShape voxelShape5 = VoxelShapes.empty();
        if (state.<Boolean>get((Property<Boolean>)VineBlock.UP)) {
            voxelShape5 = VoxelShapes.union(voxelShape5, VineBlock.UP_SHAPE);
        }
        if (state.<Boolean>get((Property<Boolean>)VineBlock.NORTH)) {
            voxelShape5 = VoxelShapes.union(voxelShape5, VineBlock.NORTH_SHAPE);
        }
        if (state.<Boolean>get((Property<Boolean>)VineBlock.EAST)) {
            voxelShape5 = VoxelShapes.union(voxelShape5, VineBlock.EAST_SHAPE);
        }
        if (state.<Boolean>get((Property<Boolean>)VineBlock.SOUTH)) {
            voxelShape5 = VoxelShapes.union(voxelShape5, VineBlock.SOUTH_SHAPE);
        }
        if (state.<Boolean>get((Property<Boolean>)VineBlock.WEST)) {
            voxelShape5 = VoxelShapes.union(voxelShape5, VineBlock.WEST_SHAPE);
        }
        return voxelShape5;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return this.hasAdjacentBlocks(this.getPlacementShape(state, world, pos));
    }
    
    private boolean hasAdjacentBlocks(final BlockState state) {
        return this.getAdjacentBlockCount(state) > 0;
    }
    
    private int getAdjacentBlockCount(final BlockState state) {
        int integer2 = 0;
        for (final BooleanProperty booleanProperty4 : VineBlock.FACING_PROPERTIES.values()) {
            if (state.<Boolean>get((Property<Boolean>)booleanProperty4)) {
                ++integer2;
            }
        }
        return integer2;
    }
    
    private boolean shouldHaveSide(final BlockView world, final BlockPos pos, final Direction side) {
        if (side == Direction.DOWN) {
            return false;
        }
        final BlockPos blockPos4 = pos.offset(side);
        if (shouldConnectTo(world, blockPos4, side)) {
            return true;
        }
        if (side.getAxis() != Direction.Axis.Y) {
            final BooleanProperty booleanProperty5 = VineBlock.FACING_PROPERTIES.get(side);
            final BlockState blockState6 = world.getBlockState(pos.up());
            return blockState6.getBlock() == this && blockState6.<Boolean>get((Property<Boolean>)booleanProperty5);
        }
        return false;
    }
    
    public static boolean shouldConnectTo(final BlockView world, final BlockPos pos, final Direction direction) {
        final BlockState blockState4 = world.getBlockState(pos);
        return Block.isFaceFullSquare(blockState4.getCollisionShape(world, pos), direction.getOpposite());
    }
    
    private BlockState getPlacementShape(BlockState state, final BlockView world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.up();
        if (state.<Boolean>get((Property<Boolean>)VineBlock.UP)) {
            state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)VineBlock.UP, shouldConnectTo(world, blockPos4, Direction.DOWN));
        }
        BlockState blockState5 = null;
        for (final Direction direction7 : Direction.Type.HORIZONTAL) {
            final BooleanProperty booleanProperty8 = getFacingProperty(direction7);
            if (state.<Boolean>get((Property<Boolean>)booleanProperty8)) {
                boolean boolean9 = this.shouldHaveSide(world, pos, direction7);
                if (!boolean9) {
                    if (blockState5 == null) {
                        blockState5 = world.getBlockState(blockPos4);
                    }
                    boolean9 = (blockState5.getBlock() == this && blockState5.<Boolean>get((Property<Boolean>)booleanProperty8));
                }
                state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)booleanProperty8, boolean9);
            }
        }
        return state;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN) {
            return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        }
        final BlockState blockState7 = this.getPlacementShape(state, world, pos);
        if (!this.hasAdjacentBlocks(blockState7)) {
            return Blocks.AIR.getDefaultState();
        }
        return blockState7;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.isClient) {
            return;
        }
        final BlockState blockState5 = this.getPlacementShape(state, world, pos);
        if (blockState5 != state) {
            if (this.hasAdjacentBlocks(blockState5)) {
                world.setBlockState(pos, blockState5, 2);
            }
            else {
                Block.dropStacks(state, world, pos);
                world.clearBlockState(pos, false);
            }
            return;
        }
        if (world.random.nextInt(4) != 0) {
            return;
        }
        final Direction direction6 = Direction.random(random);
        final BlockPos blockPos7 = pos.up();
        if (!direction6.getAxis().isHorizontal() || state.<Boolean>get((Property<Boolean>)getFacingProperty(direction6))) {
            if (direction6 == Direction.UP && pos.getY() < 255) {
                if (this.shouldHaveSide(world, pos, direction6)) {
                    world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)VineBlock.UP, true), 2);
                    return;
                }
                if (world.isAir(blockPos7)) {
                    if (!this.canGrowAt(world, pos)) {
                        return;
                    }
                    BlockState blockState6 = state;
                    for (final Direction direction7 : Direction.Type.HORIZONTAL) {
                        if (random.nextBoolean() || !shouldConnectTo(world, blockPos7.offset(direction7), Direction.UP)) {
                            blockState6 = ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)getFacingProperty(direction7), false);
                        }
                    }
                    if (this.hasHorizontalSide(blockState6)) {
                        world.setBlockState(blockPos7, blockState6, 2);
                    }
                    return;
                }
            }
            if (pos.getY() > 0) {
                final BlockPos blockPos8 = pos.down();
                final BlockState blockState7 = world.getBlockState(blockPos8);
                if (blockState7.isAir() || blockState7.getBlock() == this) {
                    final BlockState blockState8 = blockState7.isAir() ? this.getDefaultState() : blockState7;
                    final BlockState blockState9 = this.getGrownState(state, blockState8, random);
                    if (blockState8 != blockState9 && this.hasHorizontalSide(blockState9)) {
                        world.setBlockState(blockPos8, blockState9, 2);
                    }
                }
            }
            return;
        }
        if (!this.canGrowAt(world, pos)) {
            return;
        }
        final BlockPos blockPos8 = pos.offset(direction6);
        final BlockState blockState7 = world.getBlockState(blockPos8);
        if (blockState7.isAir()) {
            final Direction direction7 = direction6.rotateYClockwise();
            final Direction direction8 = direction6.rotateYCounterclockwise();
            final boolean boolean12 = state.<Boolean>get((Property<Boolean>)getFacingProperty(direction7));
            final boolean boolean13 = state.<Boolean>get((Property<Boolean>)getFacingProperty(direction8));
            final BlockPos blockPos9 = blockPos8.offset(direction7);
            final BlockPos blockPos10 = blockPos8.offset(direction8);
            if (boolean12 && shouldConnectTo(world, blockPos9, direction7)) {
                world.setBlockState(blockPos8, ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)getFacingProperty(direction7), true), 2);
            }
            else if (boolean13 && shouldConnectTo(world, blockPos10, direction8)) {
                world.setBlockState(blockPos8, ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)getFacingProperty(direction8), true), 2);
            }
            else {
                final Direction direction9 = direction6.getOpposite();
                if (boolean12 && world.isAir(blockPos9) && shouldConnectTo(world, pos.offset(direction7), direction9)) {
                    world.setBlockState(blockPos9, ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)getFacingProperty(direction9), true), 2);
                }
                else if (boolean13 && world.isAir(blockPos10) && shouldConnectTo(world, pos.offset(direction8), direction9)) {
                    world.setBlockState(blockPos10, ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)getFacingProperty(direction9), true), 2);
                }
                else if (world.random.nextFloat() < 0.05 && shouldConnectTo(world, blockPos8.up(), Direction.UP)) {
                    world.setBlockState(blockPos8, ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)VineBlock.UP, true), 2);
                }
            }
        }
        else if (shouldConnectTo(world, blockPos8, direction6)) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)getFacingProperty(direction6), true), 2);
        }
    }
    
    private BlockState getGrownState(final BlockState above, BlockState state, final Random random) {
        for (final Direction direction5 : Direction.Type.HORIZONTAL) {
            if (random.nextBoolean()) {
                final BooleanProperty booleanProperty6 = getFacingProperty(direction5);
                if (!above.<Boolean>get((Property<Boolean>)booleanProperty6)) {
                    continue;
                }
                state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)booleanProperty6, true);
            }
        }
        return state;
    }
    
    private boolean hasHorizontalSide(final BlockState state) {
        return state.<Boolean>get((Property<Boolean>)VineBlock.NORTH) || state.<Boolean>get((Property<Boolean>)VineBlock.EAST) || state.<Boolean>get((Property<Boolean>)VineBlock.SOUTH) || state.<Boolean>get((Property<Boolean>)VineBlock.WEST);
    }
    
    private boolean canGrowAt(final BlockView world, final BlockPos pos) {
        final int integer3 = 4;
        final Iterable<BlockPos> iterable4 = BlockPos.iterate(pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4, pos.getX() + 4, pos.getY() + 1, pos.getZ() + 4);
        int integer4 = 5;
        for (final BlockPos blockPos7 : iterable4) {
            if (world.getBlockState(blockPos7).getBlock() == this && --integer4 <= 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean canReplace(final BlockState state, final ItemPlacementContext ctx) {
        final BlockState blockState3 = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState3.getBlock() == this) {
            return this.getAdjacentBlockCount(blockState3) < VineBlock.FACING_PROPERTIES.size();
        }
        return super.canReplace(state, ctx);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockState blockState2 = ctx.getWorld().getBlockState(ctx.getBlockPos());
        final boolean boolean3 = blockState2.getBlock() == this;
        final BlockState blockState3 = boolean3 ? blockState2 : this.getDefaultState();
        for (final Direction direction8 : ctx.getPlacementFacings()) {
            if (direction8 != Direction.DOWN) {
                final BooleanProperty booleanProperty9 = getFacingProperty(direction8);
                final boolean boolean4 = boolean3 && blockState2.<Boolean>get((Property<Boolean>)booleanProperty9);
                if (!boolean4 && this.shouldHaveSide(ctx.getWorld(), ctx.getBlockPos(), direction8)) {
                    return ((AbstractPropertyContainer<O, BlockState>)blockState3).<Comparable, Boolean>with((Property<Comparable>)booleanProperty9, true);
                }
            }
        }
        return boolean3 ? blockState3 : null;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(VineBlock.UP, VineBlock.NORTH, VineBlock.EAST, VineBlock.SOUTH, VineBlock.WEST);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        switch (rotation) {
            case ROT_180: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)VineBlock.NORTH, (Comparable)state.<V>get((Property<V>)VineBlock.SOUTH))).with((Property<Comparable>)VineBlock.EAST, (Comparable)state.<V>get((Property<V>)VineBlock.WEST))).with((Property<Comparable>)VineBlock.SOUTH, (Comparable)state.<V>get((Property<V>)VineBlock.NORTH))).<Comparable, Comparable>with((Property<Comparable>)VineBlock.WEST, (Comparable)state.<V>get((Property<V>)VineBlock.EAST));
            }
            case ROT_270: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)VineBlock.NORTH, (Comparable)state.<V>get((Property<V>)VineBlock.EAST))).with((Property<Comparable>)VineBlock.EAST, (Comparable)state.<V>get((Property<V>)VineBlock.SOUTH))).with((Property<Comparable>)VineBlock.SOUTH, (Comparable)state.<V>get((Property<V>)VineBlock.WEST))).<Comparable, Comparable>with((Property<Comparable>)VineBlock.WEST, (Comparable)state.<V>get((Property<V>)VineBlock.NORTH));
            }
            case ROT_90: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)VineBlock.NORTH, (Comparable)state.<V>get((Property<V>)VineBlock.WEST))).with((Property<Comparable>)VineBlock.EAST, (Comparable)state.<V>get((Property<V>)VineBlock.NORTH))).with((Property<Comparable>)VineBlock.SOUTH, (Comparable)state.<V>get((Property<V>)VineBlock.EAST))).<Comparable, Comparable>with((Property<Comparable>)VineBlock.WEST, (Comparable)state.<V>get((Property<V>)VineBlock.SOUTH));
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
                return (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)VineBlock.NORTH, (Comparable)state.<V>get((Property<V>)VineBlock.SOUTH))).<Comparable, Comparable>with((Property<Comparable>)VineBlock.SOUTH, (Comparable)state.<V>get((Property<V>)VineBlock.NORTH));
            }
            case FRONT_BACK: {
                return (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)VineBlock.EAST, (Comparable)state.<V>get((Property<V>)VineBlock.WEST))).<Comparable, Comparable>with((Property<Comparable>)VineBlock.WEST, (Comparable)state.<V>get((Property<V>)VineBlock.EAST));
            }
            default: {
                return super.mirror(state, mirror);
            }
        }
    }
    
    public static BooleanProperty getFacingProperty(final Direction direction) {
        return VineBlock.FACING_PROPERTIES.get(direction);
    }
    
    static {
        UP = ConnectedPlantBlock.UP;
        NORTH = ConnectedPlantBlock.NORTH;
        EAST = ConnectedPlantBlock.EAST;
        SOUTH = ConnectedPlantBlock.SOUTH;
        WEST = ConnectedPlantBlock.WEST;
        FACING_PROPERTIES = ConnectedPlantBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(SystemUtil.<Direction, BooleanProperty>toMap());
        UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
        WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
        EAST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
        SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    }
}
