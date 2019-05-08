package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import java.util.Iterator;
import net.minecraft.util.math.Vec3i;
import net.minecraft.fluid.FluidState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import java.util.Random;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.util.shape.VoxelShape;

public class ScaffoldingBlock extends Block implements Waterloggable
{
    private static final VoxelShape NORMAL_OUTLINE_SHAPE;
    private static final VoxelShape BOTTOM_OUTLINE_SHAPE;
    private static final VoxelShape COLLISION_SHAPE;
    private static final VoxelShape OUTLINE_SHAPE;
    public static final IntegerProperty DISTANCE;
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty BOTTOM;
    
    protected ScaffoldingBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)ScaffoldingBlock.DISTANCE, 7)).with((Property<Comparable>)ScaffoldingBlock.WATERLOGGED, false)).<Comparable, Boolean>with((Property<Comparable>)ScaffoldingBlock.BOTTOM, false));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ScaffoldingBlock.DISTANCE, ScaffoldingBlock.WATERLOGGED, ScaffoldingBlock.BOTTOM);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        if (!verticalEntityPosition.a(state.getBlock().getItem())) {
            return state.<Boolean>get((Property<Boolean>)ScaffoldingBlock.BOTTOM) ? ScaffoldingBlock.BOTTOM_OUTLINE_SHAPE : ScaffoldingBlock.NORMAL_OUTLINE_SHAPE;
        }
        return VoxelShapes.fullCube();
    }
    
    @Override
    public VoxelShape getRayTraceShape(final BlockState state, final BlockView view, final BlockPos pos) {
        return VoxelShapes.fullCube();
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public boolean canReplace(final BlockState state, final ItemPlacementContext ctx) {
        return ctx.getItemStack().getItem() == this.getItem();
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockPos blockPos2 = ctx.getBlockPos();
        final World world3 = ctx.getWorld();
        final int integer4 = calculateDistance(world3, blockPos2);
        return ((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)ScaffoldingBlock.WATERLOGGED, world3.getFluidState(blockPos2).getFluid() == Fluids.WATER)).with((Property<Comparable>)ScaffoldingBlock.DISTANCE, integer4)).<Comparable, Boolean>with((Property<Comparable>)ScaffoldingBlock.BOTTOM, this.shouldBeBottom(world3, blockPos2, integer4));
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (!world.isClient) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)ScaffoldingBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (!world.isClient()) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
        return state;
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        final int integer5 = calculateDistance(world, pos);
        final BlockState blockState6 = (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)ScaffoldingBlock.DISTANCE, integer5)).<Comparable, Boolean>with((Property<Comparable>)ScaffoldingBlock.BOTTOM, this.shouldBeBottom(world, pos, integer5));
        if (blockState6.<Integer>get((Property<Integer>)ScaffoldingBlock.DISTANCE) == 7) {
            if (state.<Integer>get((Property<Integer>)ScaffoldingBlock.DISTANCE) == 7) {
                world.spawnEntity(new FallingBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, ((AbstractPropertyContainer<O, BlockState>)blockState6).<Comparable, Boolean>with((Property<Comparable>)ScaffoldingBlock.WATERLOGGED, false)));
            }
            else {
                world.breakBlock(pos, true);
            }
        }
        else if (state != blockState6) {
            world.setBlockState(pos, blockState6, 3);
        }
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return calculateDistance(world, pos) < 7;
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        if (ePos.isAboveBlock(VoxelShapes.fullCube(), pos, true) && !ePos.isSneaking()) {
            return ScaffoldingBlock.NORMAL_OUTLINE_SHAPE;
        }
        if (state.<Integer>get((Property<Integer>)ScaffoldingBlock.DISTANCE) != 0 && state.<Boolean>get((Property<Boolean>)ScaffoldingBlock.BOTTOM) && ePos.isAboveBlock(ScaffoldingBlock.OUTLINE_SHAPE, pos, true)) {
            return ScaffoldingBlock.COLLISION_SHAPE;
        }
        return VoxelShapes.empty();
    }
    
    @Override
    public FluidState getFluidState(final BlockState state) {
        if (state.<Boolean>get((Property<Boolean>)ScaffoldingBlock.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
    
    private boolean shouldBeBottom(final BlockView world, final BlockPos pos, final int distance) {
        return distance > 0 && world.getBlockState(pos.down()).getBlock() != this;
    }
    
    public static int calculateDistance(final BlockView world, final BlockPos pos) {
        final BlockPos.Mutable mutable3 = new BlockPos.Mutable(pos).setOffset(Direction.DOWN);
        final BlockState blockState4 = world.getBlockState(mutable3);
        int integer5 = 7;
        if (blockState4.getBlock() == Blocks.lI) {
            integer5 = blockState4.<Integer>get((Property<Integer>)ScaffoldingBlock.DISTANCE);
        }
        else if (Block.isSolidFullSquare(blockState4, world, mutable3, Direction.UP)) {
            return 0;
        }
        for (final Direction direction7 : Direction.Type.HORIZONTAL) {
            final BlockState blockState5 = world.getBlockState(mutable3.set(pos).setOffset(direction7));
            if (blockState5.getBlock() != Blocks.lI) {
                continue;
            }
            integer5 = Math.min(integer5, blockState5.<Integer>get((Property<Integer>)ScaffoldingBlock.DISTANCE) + 1);
            if (integer5 == 1) {
                break;
            }
        }
        return integer5;
    }
    
    static {
        COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        OUTLINE_SHAPE = VoxelShapes.fullCube().offset(0.0, -1.0, 0.0);
        DISTANCE = Properties.DISTANCE_0_7;
        WATERLOGGED = Properties.WATERLOGGED;
        BOTTOM = Properties.BOTTOM;
        final VoxelShape voxelShape1 = Block.createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
        final VoxelShape voxelShape2 = Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
        final VoxelShape voxelShape3 = Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
        final VoxelShape voxelShape4 = Block.createCuboidShape(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
        final VoxelShape voxelShape5 = Block.createCuboidShape(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
        NORMAL_OUTLINE_SHAPE = VoxelShapes.union(voxelShape1, voxelShape2, voxelShape3, voxelShape4, voxelShape5);
        final VoxelShape voxelShape6 = Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
        final VoxelShape voxelShape7 = Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        final VoxelShape voxelShape8 = Block.createCuboidShape(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
        final VoxelShape voxelShape9 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
        BOTTOM_OUTLINE_SHAPE = VoxelShapes.union(ScaffoldingBlock.COLLISION_SHAPE, ScaffoldingBlock.NORMAL_OUTLINE_SHAPE, voxelShape7, voxelShape6, voxelShape9, voxelShape8);
    }
}
