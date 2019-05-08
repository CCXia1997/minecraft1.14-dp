package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import java.util.stream.IntStream;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.DirectionProperty;

public class StairsBlock extends Block implements Waterloggable
{
    public static final DirectionProperty FACING;
    public static final EnumProperty<BlockHalf> HALF;
    public static final EnumProperty<StairShape> SHAPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape TOP_SHAPE;
    protected static final VoxelShape BOTTOM_SHAPE;
    protected static final VoxelShape g;
    protected static final VoxelShape h;
    protected static final VoxelShape i;
    protected static final VoxelShape j;
    protected static final VoxelShape k;
    protected static final VoxelShape w;
    protected static final VoxelShape x;
    protected static final VoxelShape y;
    protected static final VoxelShape[] z;
    protected static final VoxelShape[] A;
    private static final int[] B;
    private final Block baseBlock;
    private final BlockState baseBlockState;
    
    private static VoxelShape[] a(final VoxelShape voxelShape1, final VoxelShape voxelShape2, final VoxelShape voxelShape3, final VoxelShape voxelShape4, final VoxelShape voxelShape5) {
        return IntStream.range(0, 16).mapToObj(integer -> a(integer, voxelShape1, voxelShape2, voxelShape3, voxelShape4, voxelShape5)).<VoxelShape>toArray(VoxelShape[]::new);
    }
    
    private static VoxelShape a(final int integer, final VoxelShape voxelShape2, final VoxelShape voxelShape3, final VoxelShape voxelShape4, final VoxelShape voxelShape5, final VoxelShape voxelShape6) {
        VoxelShape voxelShape7 = voxelShape2;
        if ((integer & 0x1) != 0x0) {
            voxelShape7 = VoxelShapes.union(voxelShape7, voxelShape3);
        }
        if ((integer & 0x2) != 0x0) {
            voxelShape7 = VoxelShapes.union(voxelShape7, voxelShape4);
        }
        if ((integer & 0x4) != 0x0) {
            voxelShape7 = VoxelShapes.union(voxelShape7, voxelShape5);
        }
        if ((integer & 0x8) != 0x0) {
            voxelShape7 = VoxelShapes.union(voxelShape7, voxelShape6);
        }
        return voxelShape7;
    }
    
    protected StairsBlock(final BlockState blockState, final Settings settings) {
        super(settings);
        this.setDefaultState((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)StairsBlock.FACING, Direction.NORTH)).with(StairsBlock.HALF, BlockHalf.BOTTOM)).with(StairsBlock.SHAPE, StairShape.a)).<Comparable, Boolean>with((Property<Comparable>)StairsBlock.WATERLOGGED, false));
        this.baseBlock = blockState.getBlock();
        this.baseBlockState = blockState;
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return ((state.<BlockHalf>get(StairsBlock.HALF) == BlockHalf.TOP) ? StairsBlock.z : StairsBlock.A)[StairsBlock.B[this.q(state)]];
    }
    
    private int q(final BlockState blockState) {
        return blockState.<StairShape>get(StairsBlock.SHAPE).ordinal() * 4 + blockState.<Direction>get((Property<Direction>)StairsBlock.FACING).getHorizontal();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(final BlockState state, final World world, final BlockPos pos, final Random rnd) {
        this.baseBlock.randomDisplayTick(state, world, pos, rnd);
    }
    
    @Override
    public void onBlockBreakStart(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player) {
        this.baseBlockState.onBlockBreakStart(world, pos, player);
    }
    
    @Override
    public void onBroken(final IWorld world, final BlockPos pos, final BlockState state) {
        this.baseBlock.onBroken(world, pos, state);
    }
    
    @Override
    public float getBlastResistance() {
        return this.baseBlock.getBlastResistance();
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return this.baseBlock.getRenderLayer();
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return this.baseBlock.getTickRate(world);
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (state.getBlock() == state.getBlock()) {
            return;
        }
        this.baseBlockState.neighborUpdate(world, pos, Blocks.AIR, pos, false);
        this.baseBlock.onBlockAdded(this.baseBlockState, world, pos, oldState, false);
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        this.baseBlockState.onBlockRemoved(world, pos, newState, boolean5);
    }
    
    @Override
    public void onSteppedOn(final World world, final BlockPos pos, final Entity entity) {
        this.baseBlock.onSteppedOn(world, pos, entity);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        this.baseBlock.onScheduledTick(state, world, pos, random);
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        return this.baseBlockState.activate(world, player, hand, blockHitResult);
    }
    
    @Override
    public void onDestroyedByExplosion(final World world, final BlockPos pos, final Explosion explosion) {
        this.baseBlock.onDestroyedByExplosion(world, pos, explosion);
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final Direction direction2 = ctx.getFacing();
        final BlockPos blockPos3 = ctx.getBlockPos();
        final FluidState fluidState4 = ctx.getWorld().getFluidState(blockPos3);
        final BlockState blockState5 = ((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)StairsBlock.FACING, ctx.getPlayerHorizontalFacing())).with(StairsBlock.HALF, (direction2 == Direction.DOWN || (direction2 != Direction.UP && ctx.getPos().y - blockPos3.getY() > 0.5)) ? BlockHalf.TOP : BlockHalf.BOTTOM)).<Comparable, Boolean>with((Property<Comparable>)StairsBlock.WATERLOGGED, fluidState4.getFluid() == Fluids.WATER);
        return ((AbstractPropertyContainer<O, BlockState>)blockState5).<StairShape, StairShape>with(StairsBlock.SHAPE, m(blockState5, ctx.getWorld(), blockPos3));
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)StairsBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (facing.getAxis().isHorizontal()) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<StairShape, StairShape>with(StairsBlock.SHAPE, m(state, world, pos));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    private static StairShape m(final BlockState blockState, final BlockView blockView, final BlockPos blockPos) {
        final Direction direction4 = blockState.<Direction>get((Property<Direction>)StairsBlock.FACING);
        final BlockState blockState2 = blockView.getBlockState(blockPos.offset(direction4));
        if (isStairs(blockState2) && blockState.<BlockHalf>get(StairsBlock.HALF) == blockState2.<BlockHalf>get(StairsBlock.HALF)) {
            final Direction direction5 = blockState2.<Direction>get((Property<Direction>)StairsBlock.FACING);
            if (direction5.getAxis() != blockState.<Direction>get((Property<Direction>)StairsBlock.FACING).getAxis() && e(blockState, blockView, blockPos, direction5.getOpposite())) {
                if (direction5 == direction4.rotateYCounterclockwise()) {
                    return StairShape.d;
                }
                return StairShape.e;
            }
        }
        final BlockState blockState3 = blockView.getBlockState(blockPos.offset(direction4.getOpposite()));
        if (isStairs(blockState3) && blockState.<BlockHalf>get(StairsBlock.HALF) == blockState3.<BlockHalf>get(StairsBlock.HALF)) {
            final Direction direction6 = blockState3.<Direction>get((Property<Direction>)StairsBlock.FACING);
            if (direction6.getAxis() != blockState.<Direction>get((Property<Direction>)StairsBlock.FACING).getAxis() && e(blockState, blockView, blockPos, direction6)) {
                if (direction6 == direction4.rotateYCounterclockwise()) {
                    return StairShape.b;
                }
                return StairShape.c;
            }
        }
        return StairShape.a;
    }
    
    private static boolean e(final BlockState blockState, final BlockView blockView, final BlockPos blockPos, final Direction direction) {
        final BlockState blockState2 = blockView.getBlockState(blockPos.offset(direction));
        return !isStairs(blockState2) || blockState2.<Comparable>get((Property<Comparable>)StairsBlock.FACING) != blockState.<Comparable>get((Property<Comparable>)StairsBlock.FACING) || blockState2.<BlockHalf>get(StairsBlock.HALF) != blockState.<BlockHalf>get(StairsBlock.HALF);
    }
    
    public static boolean isStairs(final BlockState state) {
        return state.getBlock() instanceof StairsBlock;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)StairsBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)StairsBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        final Direction direction3 = state.<Direction>get((Property<Direction>)StairsBlock.FACING);
        final StairShape stairShape4 = state.<StairShape>get(StairsBlock.SHAPE);
        Label_0335: {
            switch (mirror) {
                case LEFT_RIGHT: {
                    if (direction3.getAxis() != Direction.Axis.Z) {
                        break;
                    }
                    switch (stairShape4) {
                        case b: {
                            return ((AbstractPropertyContainer<O, BlockState>)state.rotate(BlockRotation.ROT_180)).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.c);
                        }
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state.rotate(BlockRotation.ROT_180)).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.b);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state.rotate(BlockRotation.ROT_180)).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.e);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state.rotate(BlockRotation.ROT_180)).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.d);
                        }
                        default: {
                            return state.rotate(BlockRotation.ROT_180);
                        }
                    }
                    break;
                }
                case FRONT_BACK: {
                    if (direction3.getAxis() != Direction.Axis.X) {
                        break;
                    }
                    switch (stairShape4) {
                        case b: {
                            return ((AbstractPropertyContainer<O, BlockState>)state.rotate(BlockRotation.ROT_180)).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.b);
                        }
                        case c: {
                            return ((AbstractPropertyContainer<O, BlockState>)state.rotate(BlockRotation.ROT_180)).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.c);
                        }
                        case d: {
                            return ((AbstractPropertyContainer<O, BlockState>)state.rotate(BlockRotation.ROT_180)).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.e);
                        }
                        case e: {
                            return ((AbstractPropertyContainer<O, BlockState>)state.rotate(BlockRotation.ROT_180)).<StairShape, StairShape>with(StairsBlock.SHAPE, StairShape.d);
                        }
                        case a: {
                            return state.rotate(BlockRotation.ROT_180);
                        }
                        default: {
                            break Label_0335;
                        }
                    }
                    break;
                }
            }
        }
        return super.mirror(state, mirror);
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(StairsBlock.FACING, StairsBlock.HALF, StairsBlock.SHAPE, StairsBlock.WATERLOGGED);
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)StairsBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        HALF = Properties.BLOCK_HALF;
        SHAPE = Properties.STAIR_SHAPE;
        WATERLOGGED = Properties.WATERLOGGED;
        TOP_SHAPE = SlabBlock.TOP_SHAPE;
        BOTTOM_SHAPE = SlabBlock.BOTTOM_SHAPE;
        g = Block.createCuboidShape(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
        h = Block.createCuboidShape(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
        i = Block.createCuboidShape(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
        j = Block.createCuboidShape(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
        k = Block.createCuboidShape(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
        w = Block.createCuboidShape(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
        x = Block.createCuboidShape(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
        y = Block.createCuboidShape(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
        z = a(StairsBlock.TOP_SHAPE, StairsBlock.g, StairsBlock.k, StairsBlock.h, StairsBlock.w);
        A = a(StairsBlock.BOTTOM_SHAPE, StairsBlock.i, StairsBlock.x, StairsBlock.j, StairsBlock.y);
        B = new int[] { 12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8 };
    }
}
