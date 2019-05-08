package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.BooleanProperty;

public class ObserverBlock extends FacingBlock
{
    public static final BooleanProperty POWERED;
    
    public ObserverBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)ObserverBlock.FACING, Direction.SOUTH)).<Comparable, Boolean>with((Property<Comparable>)ObserverBlock.POWERED, false));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ObserverBlock.FACING, ObserverBlock.POWERED);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)ObserverBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)ObserverBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)ObserverBlock.FACING)));
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (state.<Boolean>get((Property<Boolean>)ObserverBlock.POWERED)) {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)ObserverBlock.POWERED, false), 2);
        }
        else {
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)ObserverBlock.POWERED, true), 2);
            world.getBlockTickScheduler().schedule(pos, this, 2);
        }
        this.updateNeighbors(world, pos, state);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Comparable>get((Property<Comparable>)ObserverBlock.FACING) == facing && !state.<Boolean>get((Property<Boolean>)ObserverBlock.POWERED)) {
            this.scheduleTick(world, pos);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    private void scheduleTick(final IWorld world, final BlockPos pos) {
        if (!world.isClient() && !world.getBlockTickScheduler().isScheduled(pos, this)) {
            world.getBlockTickScheduler().schedule(pos, this, 2);
        }
    }
    
    protected void updateNeighbors(final World world, final BlockPos pos, final BlockState state) {
        final Direction direction4 = state.<Direction>get((Property<Direction>)ObserverBlock.FACING);
        final BlockPos blockPos5 = pos.offset(direction4.getOpposite());
        world.updateNeighbor(blockPos5, this, pos);
        world.updateNeighborsExcept(blockPos5, this, direction4);
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return state.getWeakRedstonePower(view, pos, facing);
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (state.<Boolean>get((Property<Boolean>)ObserverBlock.POWERED) && state.<Comparable>get((Property<Comparable>)ObserverBlock.FACING) == facing) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (state.getBlock() == oldState.getBlock()) {
            return;
        }
        if (!world.isClient() && state.<Boolean>get((Property<Boolean>)ObserverBlock.POWERED) && !world.getBlockTickScheduler().isScheduled(pos, this)) {
            final BlockState blockState6 = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)ObserverBlock.POWERED, false);
            world.setBlockState(pos, blockState6, 18);
            this.updateNeighbors(world, pos, blockState6);
        }
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        if (!world.isClient && state.<Boolean>get((Property<Boolean>)ObserverBlock.POWERED) && world.getBlockTickScheduler().isScheduled(pos, this)) {
            this.updateNeighbors(world, pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)ObserverBlock.POWERED, false));
        }
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)ObserverBlock.FACING, ctx.getPlayerFacing().getOpposite().getOpposite());
    }
    
    static {
        POWERED = Properties.POWERED;
    }
}
