package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.TaskPriority;
import net.minecraft.state.property.Property;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.shape.VoxelShape;

public abstract class AbstractRedstoneGateBlock extends HorizontalFacingBlock
{
    protected static final VoxelShape SHAPE;
    public static final BooleanProperty POWERED;
    
    protected AbstractRedstoneGateBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return AbstractRedstoneGateBlock.SHAPE;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return Block.isSolidMediumSquare(world, pos.down());
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (this.isLocked(world, pos, state)) {
            return;
        }
        final boolean boolean5 = state.<Boolean>get((Property<Boolean>)AbstractRedstoneGateBlock.POWERED);
        final boolean boolean6 = this.hasPower(world, pos, state);
        if (boolean5 && !boolean6) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)AbstractRedstoneGateBlock.POWERED, false), 2);
        }
        else if (!boolean5) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)AbstractRedstoneGateBlock.POWERED, true), 2);
            if (!boolean6) {
                world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), TaskPriority.c);
            }
        }
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return state.getWeakRedstonePower(view, pos, facing);
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (!state.<Boolean>get((Property<Boolean>)AbstractRedstoneGateBlock.POWERED)) {
            return 0;
        }
        if (state.<Comparable>get((Property<Comparable>)AbstractRedstoneGateBlock.FACING) == facing) {
            return this.getOutputLevel(view, pos, state);
        }
        return 0;
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (state.canPlaceAt(world, pos)) {
            this.updatePowered(world, pos, state);
            return;
        }
        final BlockEntity blockEntity7 = this.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity7);
        world.clearBlockState(pos, false);
        for (final Direction direction11 : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction11), this);
        }
    }
    
    protected void updatePowered(final World world, final BlockPos pos, final BlockState state) {
        if (this.isLocked(world, pos, state)) {
            return;
        }
        final boolean boolean4 = state.<Boolean>get((Property<Boolean>)AbstractRedstoneGateBlock.POWERED);
        final boolean boolean5 = this.hasPower(world, pos, state);
        if (boolean4 != boolean5 && !world.getBlockTickScheduler().isTicking(pos, this)) {
            TaskPriority taskPriority6 = TaskPriority.c;
            if (this.isTargetNotAligned(world, pos, state)) {
                taskPriority6 = TaskPriority.a;
            }
            else if (boolean4) {
                taskPriority6 = TaskPriority.b;
            }
            world.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), taskPriority6);
        }
    }
    
    public boolean isLocked(final ViewableWorld world, final BlockPos pos, final BlockState state) {
        return false;
    }
    
    protected boolean hasPower(final World world, final BlockPos pos, final BlockState state) {
        return this.getPower(world, pos, state) > 0;
    }
    
    protected int getPower(final World world, final BlockPos pos, final BlockState state) {
        final Direction direction4 = state.<Direction>get((Property<Direction>)AbstractRedstoneGateBlock.FACING);
        final BlockPos blockPos5 = pos.offset(direction4);
        final int integer6 = world.getEmittedRedstonePower(blockPos5, direction4);
        if (integer6 >= 15) {
            return integer6;
        }
        final BlockState blockState7 = world.getBlockState(blockPos5);
        return Math.max(integer6, (blockState7.getBlock() == Blocks.bQ) ? ((int)blockState7.<Integer>get((Property<Integer>)RedstoneWireBlock.POWER)) : 0);
    }
    
    protected int getMaxInputLevelSides(final ViewableWorld view, final BlockPos pos, final BlockState blockState) {
        final Direction direction4 = blockState.<Direction>get((Property<Direction>)AbstractRedstoneGateBlock.FACING);
        final Direction direction5 = direction4.rotateYClockwise();
        final Direction direction6 = direction4.rotateYCounterclockwise();
        return Math.max(this.getInputLevel(view, pos.offset(direction5), direction5), this.getInputLevel(view, pos.offset(direction6), direction6));
    }
    
    protected int getInputLevel(final ViewableWorld view, final BlockPos pos, final Direction direction) {
        final BlockState blockState4 = view.getBlockState(pos);
        final Block block5 = blockState4.getBlock();
        if (!this.isValidInput(blockState4)) {
            return 0;
        }
        if (block5 == Blocks.fo) {
            return 15;
        }
        if (block5 == Blocks.bQ) {
            return blockState4.<Integer>get((Property<Integer>)RedstoneWireBlock.POWER);
        }
        return view.getEmittedStrongRedstonePower(pos, direction);
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)AbstractRedstoneGateBlock.FACING, ctx.getPlayerHorizontalFacing().getOpposite());
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        if (this.hasPower(world, pos, state)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        this.updateTarget(world, pos, state);
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (boolean5 || state.getBlock() == newState.getBlock()) {
            return;
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
        this.updateTarget(world, pos, state);
    }
    
    protected void updateTarget(final World world, final BlockPos blockPos, final BlockState blockState) {
        final Direction direction4 = blockState.<Direction>get((Property<Direction>)AbstractRedstoneGateBlock.FACING);
        final BlockPos blockPos2 = blockPos.offset(direction4.getOpposite());
        world.updateNeighbor(blockPos2, this, blockPos);
        world.updateNeighborsExcept(blockPos2, this, direction4);
    }
    
    protected boolean isValidInput(final BlockState blockState) {
        return blockState.emitsRedstonePower();
    }
    
    protected int getOutputLevel(final BlockView view, final BlockPos pos, final BlockState blockState) {
        return 15;
    }
    
    public static boolean isRedstoneGate(final BlockState state) {
        return state.getBlock() instanceof AbstractRedstoneGateBlock;
    }
    
    public boolean isTargetNotAligned(final BlockView world, final BlockPos pos, final BlockState state) {
        final Direction direction4 = state.<Direction>get((Property<Direction>)AbstractRedstoneGateBlock.FACING).getOpposite();
        final BlockState blockState5 = world.getBlockState(pos.offset(direction4));
        return isRedstoneGate(blockState5) && blockState5.<Comparable>get((Property<Comparable>)AbstractRedstoneGateBlock.FACING) != direction4;
    }
    
    protected abstract int getUpdateDelayInternal(final BlockState arg1);
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return true;
    }
    
    static {
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        POWERED = Properties.POWERED;
    }
}
