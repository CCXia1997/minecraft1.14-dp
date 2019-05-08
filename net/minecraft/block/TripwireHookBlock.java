package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import java.util.Random;
import com.google.common.base.MoreObjects;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class TripwireHookBlock extends Block
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty ATTACHED;
    protected static final VoxelShape d;
    protected static final VoxelShape e;
    protected static final VoxelShape f;
    protected static final VoxelShape g;
    
    public TripwireHookBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)TripwireHookBlock.FACING, Direction.NORTH)).with((Property<Comparable>)TripwireHookBlock.POWERED, false)).<Comparable, Boolean>with((Property<Comparable>)TripwireHookBlock.ATTACHED, false));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        switch (state.<Direction>get((Property<Direction>)TripwireHookBlock.FACING)) {
            default: {
                return TripwireHookBlock.g;
            }
            case WEST: {
                return TripwireHookBlock.f;
            }
            case SOUTH: {
                return TripwireHookBlock.e;
            }
            case NORTH: {
                return TripwireHookBlock.d;
            }
        }
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final Direction direction4 = state.<Direction>get((Property<Direction>)TripwireHookBlock.FACING);
        final BlockPos blockPos5 = pos.offset(direction4.getOpposite());
        final BlockState blockState6 = world.getBlockState(blockPos5);
        return direction4.getAxis().isHorizontal() && Block.isSolidFullSquare(blockState6, world, blockPos5, direction4) && !blockState6.emitsRedstonePower();
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing.getOpposite() == state.<Direction>get((Property<Direction>)TripwireHookBlock.FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        BlockState blockState2 = (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)TripwireHookBlock.POWERED, false)).<Comparable, Boolean>with((Property<Comparable>)TripwireHookBlock.ATTACHED, false);
        final ViewableWorld viewableWorld3 = ctx.getWorld();
        final BlockPos blockPos4 = ctx.getBlockPos();
        final Direction[] placementFacings;
        final Direction[] arr5 = placementFacings = ctx.getPlacementFacings();
        for (final Direction direction9 : placementFacings) {
            if (direction9.getAxis().isHorizontal()) {
                final Direction direction10 = direction9.getOpposite();
                blockState2 = ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Direction>with((Property<Comparable>)TripwireHookBlock.FACING, direction10);
                if (blockState2.canPlaceAt(viewableWorld3, blockPos4)) {
                    return blockState2;
                }
            }
        }
        return null;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        this.update(world, pos, state, false, false, -1, null);
    }
    
    public void update(final World world, final BlockPos blockPos, final BlockState blockState3, final boolean boolean4, final boolean boolean5, final int integer, @Nullable final BlockState blockState7) {
        final Direction direction8 = blockState3.<Direction>get((Property<Direction>)TripwireHookBlock.FACING);
        final boolean boolean6 = blockState3.<Boolean>get((Property<Boolean>)TripwireHookBlock.ATTACHED);
        final boolean boolean7 = blockState3.<Boolean>get((Property<Boolean>)TripwireHookBlock.POWERED);
        boolean boolean8 = !boolean4;
        boolean boolean9 = false;
        int integer2 = 0;
        final BlockState[] arr14 = new BlockState[42];
        int integer3 = 1;
        while (integer3 < 42) {
            final BlockPos blockPos2 = blockPos.offset(direction8, integer3);
            BlockState blockState8 = world.getBlockState(blockPos2);
            if (blockState8.getBlock() == Blocks.ed) {
                if (blockState8.<Comparable>get((Property<Comparable>)TripwireHookBlock.FACING) == direction8.getOpposite()) {
                    integer2 = integer3;
                    break;
                }
                break;
            }
            else {
                if (blockState8.getBlock() == Blocks.ee || integer3 == integer) {
                    if (integer3 == integer) {
                        blockState8 = MoreObjects.<BlockState>firstNonNull(blockState7, blockState8);
                    }
                    final boolean boolean10 = !blockState8.<Boolean>get((Property<Boolean>)TripwireBlock.DISARMED);
                    final boolean boolean11 = blockState8.<Boolean>get((Property<Boolean>)TripwireBlock.POWERED);
                    boolean9 |= (boolean10 && boolean11);
                    arr14[integer3] = blockState8;
                    if (integer3 == integer) {
                        world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
                        boolean8 &= boolean10;
                    }
                }
                else {
                    arr14[integer3] = null;
                    boolean8 = false;
                }
                ++integer3;
            }
        }
        boolean8 &= (integer2 > 1);
        boolean9 &= boolean8;
        final BlockState blockState9 = (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)TripwireHookBlock.ATTACHED, boolean8)).<Comparable, Boolean>with((Property<Comparable>)TripwireHookBlock.POWERED, boolean9);
        if (integer2 > 0) {
            final BlockPos blockPos2 = blockPos.offset(direction8, integer2);
            final Direction direction9 = direction8.getOpposite();
            world.setBlockState(blockPos2, ((AbstractPropertyContainer<O, BlockState>)blockState9).<Comparable, Direction>with((Property<Comparable>)TripwireHookBlock.FACING, direction9), 3);
            this.updateNeighborsOnAxis(world, blockPos2, direction9);
            this.playSound(world, blockPos2, boolean8, boolean9, boolean6, boolean7);
        }
        this.playSound(world, blockPos, boolean8, boolean9, boolean6, boolean7);
        if (!boolean4) {
            world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)blockState9).<Comparable, Direction>with((Property<Comparable>)TripwireHookBlock.FACING, direction8), 3);
            if (boolean5) {
                this.updateNeighborsOnAxis(world, blockPos, direction8);
            }
        }
        if (boolean6 != boolean8) {
            for (int integer4 = 1; integer4 < integer2; ++integer4) {
                final BlockPos blockPos3 = blockPos.offset(direction8, integer4);
                final BlockState blockState10 = arr14[integer4];
                if (blockState10 != null) {
                    world.setBlockState(blockPos3, ((AbstractPropertyContainer<O, BlockState>)blockState10).<Comparable, Boolean>with((Property<Comparable>)TripwireHookBlock.ATTACHED, boolean8), 3);
                    if (!world.getBlockState(blockPos3).isAir()) {}
                }
            }
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        this.update(world, pos, state, false, true, -1, null);
    }
    
    private void playSound(final World world, final BlockPos pos, final boolean attached, final boolean on, final boolean detached, final boolean off) {
        if (on && !off) {
            world.playSound(null, pos, SoundEvents.lP, SoundCategory.e, 0.4f, 0.6f);
        }
        else if (!on && off) {
            world.playSound(null, pos, SoundEvents.lO, SoundCategory.e, 0.4f, 0.5f);
        }
        else if (attached && !detached) {
            world.playSound(null, pos, SoundEvents.lN, SoundCategory.e, 0.4f, 0.7f);
        }
        else if (!attached && detached) {
            world.playSound(null, pos, SoundEvents.lQ, SoundCategory.e, 0.4f, 1.2f / (world.random.nextFloat() * 0.2f + 0.9f));
        }
    }
    
    private void updateNeighborsOnAxis(final World world, final BlockPos pos, final Direction direction) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(direction.getOpposite()), this);
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (boolean5 || state.getBlock() == newState.getBlock()) {
            return;
        }
        final boolean boolean6 = state.<Boolean>get((Property<Boolean>)TripwireHookBlock.ATTACHED);
        final boolean boolean7 = state.<Boolean>get((Property<Boolean>)TripwireHookBlock.POWERED);
        if (boolean6 || boolean7) {
            this.update(world, pos, state, true, false, -1, null);
        }
        if (boolean7) {
            world.updateNeighborsAlways(pos, this);
            world.updateNeighborsAlways(pos.offset(state.<Direction>get((Property<Direction>)TripwireHookBlock.FACING).getOpposite()), this);
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return state.<Boolean>get((Property<Boolean>)TripwireHookBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        if (!state.<Boolean>get((Property<Boolean>)TripwireHookBlock.POWERED)) {
            return 0;
        }
        if (state.<Comparable>get((Property<Comparable>)TripwireHookBlock.FACING) == facing) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.MIPPED_CUTOUT;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)TripwireHookBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)TripwireHookBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)TripwireHookBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(TripwireHookBlock.FACING, TripwireHookBlock.POWERED, TripwireHookBlock.ATTACHED);
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        POWERED = Properties.POWERED;
        ATTACHED = Properties.ATTACHED;
        d = Block.createCuboidShape(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
        e = Block.createCuboidShape(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
        f = Block.createCuboidShape(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
        g = Block.createCuboidShape(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);
    }
}
