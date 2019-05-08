package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DustParticleParameters;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import java.util.Iterator;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import com.google.common.collect.Sets;
import net.minecraft.util.math.BlockPos;
import java.util.Set;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;

public class RedstoneWireBlock extends Block
{
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST;
    public static final IntegerProperty POWER;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;
    protected static final VoxelShape[] WIRE_CONNECTIONS_TO_SHAPE;
    private boolean wiresGivePower;
    private final Set<BlockPos> i;
    
    public RedstoneWireBlock(final Settings settings) {
        super(settings);
        this.wiresGivePower = true;
        this.i = Sets.newHashSet();
        this.setDefaultState(((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.c)).with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.c)).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.c)).with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.c)).<Comparable, Integer>with((Property<Comparable>)RedstoneWireBlock.POWER, 0));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return RedstoneWireBlock.WIRE_CONNECTIONS_TO_SHAPE[getWireConnectionMask(state)];
    }
    
    private static int getWireConnectionMask(final BlockState state) {
        int integer2 = 0;
        final boolean boolean3 = state.<WireConnection>get(RedstoneWireBlock.WIRE_CONNECTION_NORTH) != WireConnection.c;
        final boolean boolean4 = state.<WireConnection>get(RedstoneWireBlock.WIRE_CONNECTION_EAST) != WireConnection.c;
        final boolean boolean5 = state.<WireConnection>get(RedstoneWireBlock.WIRE_CONNECTION_SOUTH) != WireConnection.c;
        final boolean boolean6 = state.<WireConnection>get(RedstoneWireBlock.WIRE_CONNECTION_WEST) != WireConnection.c;
        if (boolean3 || (boolean5 && !boolean3 && !boolean4 && !boolean6)) {
            integer2 |= 1 << Direction.NORTH.getHorizontal();
        }
        if (boolean4 || (boolean6 && !boolean3 && !boolean4 && !boolean5)) {
            integer2 |= 1 << Direction.EAST.getHorizontal();
        }
        if (boolean5 || (boolean3 && !boolean4 && !boolean5 && !boolean6)) {
            integer2 |= 1 << Direction.SOUTH.getHorizontal();
        }
        if (boolean6 || (boolean4 && !boolean3 && !boolean5 && !boolean6)) {
            integer2 |= 1 << Direction.WEST.getHorizontal();
        }
        return integer2;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockView blockView2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        return (((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with(RedstoneWireBlock.WIRE_CONNECTION_WEST, this.getRenderConnectionType(blockView2, blockPos3, Direction.WEST))).with(RedstoneWireBlock.WIRE_CONNECTION_EAST, this.getRenderConnectionType(blockView2, blockPos3, Direction.EAST))).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, this.getRenderConnectionType(blockView2, blockPos3, Direction.NORTH))).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(blockView2, blockPos3, Direction.SOUTH));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == Direction.DOWN) {
            return state;
        }
        if (facing == Direction.UP) {
            return (((((AbstractPropertyContainer<O, BlockState>)state).with(RedstoneWireBlock.WIRE_CONNECTION_WEST, this.getRenderConnectionType(world, pos, Direction.WEST))).with(RedstoneWireBlock.WIRE_CONNECTION_EAST, this.getRenderConnectionType(world, pos, Direction.EAST))).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, this.getRenderConnectionType(world, pos, Direction.NORTH))).<WireConnection, WireConnection>with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(world, pos, Direction.SOUTH));
        }
        return ((AbstractPropertyContainer<O, BlockState>)state).<WireConnection, WireConnection>with(RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(facing), this.getRenderConnectionType(world, pos, facing));
    }
    
    @Override
    public void b(final BlockState state, final IWorld world, final BlockPos pos, final int flags) {
        try (final BlockPos.PooledMutable pooledMutable5 = BlockPos.PooledMutable.get()) {
            for (final Direction direction8 : Direction.Type.HORIZONTAL) {
                final WireConnection wireConnection9 = state.<WireConnection>get(RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction8));
                if (wireConnection9 != WireConnection.c && world.getBlockState(pooledMutable5.set((Vec3i)pos).setOffset(direction8)).getBlock() != this) {
                    pooledMutable5.setOffset(Direction.DOWN);
                    final BlockState blockState10 = world.getBlockState(pooledMutable5);
                    if (blockState10.getBlock() != Blocks.iG) {
                        final BlockPos blockPos11 = pooledMutable5.offset(direction8.getOpposite());
                        final BlockState blockState11 = blockState10.getStateForNeighborUpdate(direction8.getOpposite(), world.getBlockState(blockPos11), world, pooledMutable5, blockPos11);
                        Block.replaceBlock(blockState10, blockState11, world, pooledMutable5, flags);
                    }
                    pooledMutable5.set((Vec3i)pos).setOffset(direction8).setOffset(Direction.UP);
                    final BlockState blockState12 = world.getBlockState(pooledMutable5);
                    if (blockState12.getBlock() == Blocks.iG) {
                        continue;
                    }
                    final BlockPos blockPos12 = pooledMutable5.offset(direction8.getOpposite());
                    final BlockState blockState13 = blockState12.getStateForNeighborUpdate(direction8.getOpposite(), world.getBlockState(blockPos12), world, pooledMutable5, blockPos12);
                    Block.replaceBlock(blockState12, blockState13, world, pooledMutable5, flags);
                }
            }
        }
    }
    
    private WireConnection getRenderConnectionType(final BlockView view, final BlockPos pos, final Direction direction) {
        final BlockPos blockPos4 = pos.offset(direction);
        final BlockState blockState5 = view.getBlockState(blockPos4);
        final BlockPos blockPos5 = pos.up();
        final BlockState blockState6 = view.getBlockState(blockPos5);
        if (!blockState6.isSimpleFullBlock(view, blockPos5)) {
            final boolean boolean8 = Block.isSolidFullSquare(blockState5, view, blockPos4, Direction.UP) || blockState5.getBlock() == Blocks.fq;
            if (boolean8 && j(view.getBlockState(blockPos4.up()))) {
                if (Block.isShapeFullCube(blockState5.getCollisionShape(view, blockPos4))) {
                    return WireConnection.a;
                }
                return WireConnection.b;
            }
        }
        if (a(blockState5, direction) || (!blockState5.isSimpleFullBlock(view, blockPos4) && j(view.getBlockState(blockPos4.down())))) {
            return WireConnection.b;
        }
        return WireConnection.c;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        final BlockState blockState5 = world.getBlockState(blockPos4);
        return Block.isSolidFullSquare(blockState5, world, blockPos4, Direction.UP) || blockState5.getBlock() == Blocks.fq;
    }
    
    private BlockState a(final World world, final BlockPos blockPos, BlockState blockState) {
        blockState = this.b(world, blockPos, blockState);
        final List<BlockPos> list4 = Lists.newArrayList(this.i);
        this.i.clear();
        for (final BlockPos blockPos2 : list4) {
            world.updateNeighborsAlways(blockPos2, this);
        }
        return blockState;
    }
    
    private BlockState b(final World world, final BlockPos blockPos, BlockState blockState) {
        final BlockState blockState2 = blockState;
        final int integer5 = blockState2.<Integer>get((Property<Integer>)RedstoneWireBlock.POWER);
        this.wiresGivePower = false;
        final int integer6 = world.getReceivedRedstonePower(blockPos);
        this.wiresGivePower = true;
        int integer7 = 0;
        if (integer6 < 15) {
            for (final Direction direction9 : Direction.Type.HORIZONTAL) {
                final BlockPos blockPos2 = blockPos.offset(direction9);
                final BlockState blockState3 = world.getBlockState(blockPos2);
                integer7 = this.a(integer7, blockState3);
                final BlockPos blockPos3 = blockPos.up();
                if (blockState3.isSimpleFullBlock(world, blockPos2) && !world.getBlockState(blockPos3).isSimpleFullBlock(world, blockPos3)) {
                    integer7 = this.a(integer7, world.getBlockState(blockPos2.up()));
                }
                else {
                    if (blockState3.isSimpleFullBlock(world, blockPos2)) {
                        continue;
                    }
                    integer7 = this.a(integer7, world.getBlockState(blockPos2.down()));
                }
            }
        }
        int integer8 = integer7 - 1;
        if (integer6 > integer8) {
            integer8 = integer6;
        }
        if (integer5 != integer8) {
            blockState = ((AbstractPropertyContainer<O, BlockState>)blockState).<Comparable, Integer>with((Property<Comparable>)RedstoneWireBlock.POWER, integer8);
            if (world.getBlockState(blockPos) == blockState2) {
                world.setBlockState(blockPos, blockState, 2);
            }
            this.i.add(blockPos);
            for (final Direction direction10 : Direction.values()) {
                this.i.add(blockPos.offset(direction10));
            }
        }
        return blockState;
    }
    
    private void a(final World world, final BlockPos blockPos) {
        if (world.getBlockState(blockPos).getBlock() != this) {
            return;
        }
        world.updateNeighborsAlways(blockPos, this);
        for (final Direction direction6 : Direction.values()) {
            world.updateNeighborsAlways(blockPos.offset(direction6), this);
        }
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock() || world.isClient) {
            return;
        }
        this.a(world, pos, state);
        for (final Direction direction7 : Direction.Type.VERTICAL) {
            world.updateNeighborsAlways(pos.offset(direction7), this);
        }
        for (final Direction direction7 : Direction.Type.HORIZONTAL) {
            this.a(world, pos.offset(direction7));
        }
        for (final Direction direction7 : Direction.Type.HORIZONTAL) {
            final BlockPos blockPos8 = pos.offset(direction7);
            if (world.getBlockState(blockPos8).isSimpleFullBlock(world, blockPos8)) {
                this.a(world, blockPos8.up());
            }
            else {
                this.a(world, blockPos8.down());
            }
        }
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (boolean5 || state.getBlock() == newState.getBlock()) {
            return;
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
        if (world.isClient) {
            return;
        }
        for (final Direction direction9 : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction9), this);
        }
        this.a(world, pos, state);
        for (final Direction direction10 : Direction.Type.HORIZONTAL) {
            this.a(world, pos.offset(direction10));
        }
        for (final Direction direction10 : Direction.Type.HORIZONTAL) {
            final BlockPos blockPos8 = pos.offset(direction10);
            if (world.getBlockState(blockPos8).isSimpleFullBlock(world, blockPos8)) {
                this.a(world, blockPos8.up());
            }
            else {
                this.a(world, blockPos8.down());
            }
        }
    }
    
    private int a(final int integer, final BlockState blockState) {
        if (blockState.getBlock() != this) {
            return integer;
        }
        final int integer2 = blockState.<Integer>get((Property<Integer>)RedstoneWireBlock.POWER);
        if (integer2 > integer) {
            return integer2;
        }
        return integer;
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (world.isClient) {
            return;
        }
        if (state.canPlaceAt(world, pos)) {
            this.a(world, pos, state);
        }
        else {
            Block.dropStacks(state, world, pos);
            world.clearBlockState(pos, false);
        }
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (!this.wiresGivePower) {
            return 0;
        }
        return state.getWeakRedstonePower(view, pos, facing);
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (!this.wiresGivePower) {
            return 0;
        }
        final int integer5 = state.<Integer>get((Property<Integer>)RedstoneWireBlock.POWER);
        if (integer5 == 0) {
            return 0;
        }
        if (facing == Direction.UP) {
            return integer5;
        }
        final EnumSet<Direction> enumSet6 = EnumSet.<Direction>noneOf(Direction.class);
        for (final Direction direction8 : Direction.Type.HORIZONTAL) {
            if (this.b(view, pos, direction8)) {
                enumSet6.add(direction8);
            }
        }
        if (facing.getAxis().isHorizontal() && enumSet6.isEmpty()) {
            return integer5;
        }
        if (enumSet6.contains(facing) && !enumSet6.contains(facing.rotateYCounterclockwise()) && !enumSet6.contains(facing.rotateYClockwise())) {
            return integer5;
        }
        return 0;
    }
    
    private boolean b(final BlockView blockView, final BlockPos blockPos, final Direction direction) {
        final BlockPos blockPos2 = blockPos.offset(direction);
        final BlockState blockState5 = blockView.getBlockState(blockPos2);
        final boolean boolean6 = blockState5.isSimpleFullBlock(blockView, blockPos2);
        final BlockPos blockPos3 = blockPos.up();
        final boolean boolean7 = blockView.getBlockState(blockPos3).isSimpleFullBlock(blockView, blockPos3);
        return (!boolean7 && boolean6 && a(blockView, blockPos2.up())) || a(blockState5, direction) || (blockState5.getBlock() == Blocks.cQ && blockState5.<Boolean>get((Property<Boolean>)AbstractRedstoneGateBlock.POWERED) && blockState5.<Comparable>get((Property<Comparable>)AbstractRedstoneGateBlock.FACING) == direction) || (!boolean6 && a(blockView, blockPos2.down()));
    }
    
    protected static boolean a(final BlockView blockView, final BlockPos blockPos) {
        return j(blockView.getBlockState(blockPos));
    }
    
    protected static boolean j(final BlockState blockState) {
        return a(blockState, null);
    }
    
    protected static boolean a(final BlockState blockState, @Nullable final Direction direction) {
        final Block block3 = blockState.getBlock();
        if (block3 == Blocks.bQ) {
            return true;
        }
        if (blockState.getBlock() == Blocks.cQ) {
            final Direction direction2 = blockState.<Direction>get((Property<Direction>)RepeaterBlock.FACING);
            return direction2 == direction || direction2.getOpposite() == direction;
        }
        if (Blocks.iG == blockState.getBlock()) {
            return direction == blockState.<Direction>get((Property<Direction>)ObserverBlock.FACING);
        }
        return blockState.emitsRedstonePower() && direction != null;
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return this.wiresGivePower;
    }
    
    @Environment(EnvType.CLIENT)
    public static int getWireColor(final int powerLevel) {
        final float float2 = powerLevel / 15.0f;
        float float3 = float2 * 0.6f + 0.4f;
        if (powerLevel == 0) {
            float3 = 0.3f;
        }
        float float4 = float2 * float2 * 0.7f - 0.5f;
        float float5 = float2 * float2 * 0.6f - 0.7f;
        if (float4 < 0.0f) {
            float4 = 0.0f;
        }
        if (float5 < 0.0f) {
            float5 = 0.0f;
        }
        final int integer6 = MathHelper.clamp((int)(float3 * 255.0f), 0, 255);
        final int integer7 = MathHelper.clamp((int)(float4 * 255.0f), 0, 255);
        final int integer8 = MathHelper.clamp((int)(float5 * 255.0f), 0, 255);
        return 0xFF000000 | integer6 << 16 | integer7 << 8 | integer8;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        final int integer5 = state.<Integer>get((Property<Integer>)RedstoneWireBlock.POWER);
        if (integer5 == 0) {
            return;
        }
        final double double6 = pos.getX() + 0.5 + (rnd.nextFloat() - 0.5) * 0.2;
        final double double7 = pos.getY() + 0.0625f;
        final double double8 = pos.getZ() + 0.5 + (rnd.nextFloat() - 0.5) * 0.2;
        final float float12 = integer5 / 15.0f;
        final float float13 = float12 * 0.6f + 0.4f;
        final float float14 = Math.max(0.0f, float12 * float12 * 0.7f - 0.5f);
        final float float15 = Math.max(0.0f, float12 * float12 * 0.6f - 0.7f);
        world.addParticle(new DustParticleParameters(float13, float14, float15, 1.0f), double6, double7, double8, 0.0, 0.0, 0.0);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        switch (rotation) {
            case ROT_180: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_SOUTH))).with(RedstoneWireBlock.WIRE_CONNECTION_EAST, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_WEST))).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_NORTH))).<WireConnection, Comparable>with(RedstoneWireBlock.WIRE_CONNECTION_WEST, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_EAST));
            }
            case ROT_270: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_EAST))).with(RedstoneWireBlock.WIRE_CONNECTION_EAST, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_SOUTH))).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_WEST))).<WireConnection, Comparable>with(RedstoneWireBlock.WIRE_CONNECTION_WEST, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_NORTH));
            }
            case ROT_90: {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_WEST))).with(RedstoneWireBlock.WIRE_CONNECTION_EAST, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_NORTH))).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_EAST))).<WireConnection, Comparable>with(RedstoneWireBlock.WIRE_CONNECTION_WEST, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_SOUTH));
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
                return (((AbstractPropertyContainer<O, BlockState>)state).with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_SOUTH))).<WireConnection, Comparable>with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_NORTH));
            }
            case FRONT_BACK: {
                return (((AbstractPropertyContainer<O, BlockState>)state).with(RedstoneWireBlock.WIRE_CONNECTION_EAST, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_WEST))).<WireConnection, Comparable>with(RedstoneWireBlock.WIRE_CONNECTION_WEST, (Comparable)state.<V>get((Property<V>)RedstoneWireBlock.WIRE_CONNECTION_EAST));
            }
            default: {
                return super.mirror(state, mirror);
            }
        }
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(RedstoneWireBlock.WIRE_CONNECTION_NORTH, RedstoneWireBlock.WIRE_CONNECTION_EAST, RedstoneWireBlock.WIRE_CONNECTION_SOUTH, RedstoneWireBlock.WIRE_CONNECTION_WEST, RedstoneWireBlock.POWER);
    }
    
    static {
        WIRE_CONNECTION_NORTH = Properties.WIRE_CONNECTION_NORTH;
        WIRE_CONNECTION_EAST = Properties.WIRE_CONNECTION_EAST;
        WIRE_CONNECTION_SOUTH = Properties.WIRE_CONNECTION_SOUTH;
        WIRE_CONNECTION_WEST = Properties.WIRE_CONNECTION_WEST;
        POWER = Properties.POWER;
        DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.<Direction, Object>newEnumMap(ImmutableMap.<Direction, EnumProperty<WireConnection>>of(Direction.NORTH, RedstoneWireBlock.WIRE_CONNECTION_NORTH, Direction.EAST, RedstoneWireBlock.WIRE_CONNECTION_EAST, Direction.SOUTH, RedstoneWireBlock.WIRE_CONNECTION_SOUTH, Direction.WEST, RedstoneWireBlock.WIRE_CONNECTION_WEST));
        WIRE_CONNECTIONS_TO_SHAPE = new VoxelShape[] { Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0), Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0), Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 16.0), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 13.0, 1.0, 13.0), Block.createCuboidShape(0.0, 0.0, 0.0, 13.0, 1.0, 16.0), Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 16.0), Block.createCuboidShape(0.0, 0.0, 3.0, 16.0, 1.0, 13.0), Block.createCuboidShape(0.0, 0.0, 3.0, 16.0, 1.0, 16.0), Block.createCuboidShape(3.0, 0.0, 0.0, 16.0, 1.0, 13.0), Block.createCuboidShape(3.0, 0.0, 0.0, 16.0, 1.0, 16.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 13.0), Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0) };
    }
}
