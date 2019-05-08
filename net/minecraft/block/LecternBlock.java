package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.state.property.Properties;
import net.minecraft.stat.Stats;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class LecternBlock extends BlockWithEntity
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty HAS_BOOK;
    public static final VoxelShape BOTTOM_SHAPE;
    public static final VoxelShape MIDDLE_SHAPE;
    public static final VoxelShape BASE_SHAPE;
    public static final VoxelShape COLLISION_SHAPE_TOP;
    public static final VoxelShape COLLISION_SHAPE;
    public static final VoxelShape WEST_SHAPE;
    public static final VoxelShape NORTH_SHAPE;
    public static final VoxelShape EAST_SHAPE;
    public static final VoxelShape SOUTH_SHAPE;
    
    protected LecternBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)LecternBlock.FACING, Direction.NORTH)).with((Property<Comparable>)LecternBlock.POWERED, false)).<Comparable, Boolean>with((Property<Comparable>)LecternBlock.HAS_BOOK, false));
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public VoxelShape h(final BlockState state, final BlockView view, final BlockPos pos) {
        return LecternBlock.BASE_SHAPE;
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)LecternBlock.FACING, ctx.getPlayerHorizontalFacing().getOpposite());
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return LecternBlock.COLLISION_SHAPE;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        switch (state.<Direction>get((Property<Direction>)LecternBlock.FACING)) {
            case NORTH: {
                return LecternBlock.NORTH_SHAPE;
            }
            case SOUTH: {
                return LecternBlock.SOUTH_SHAPE;
            }
            case EAST: {
                return LecternBlock.EAST_SHAPE;
            }
            case WEST: {
                return LecternBlock.WEST_SHAPE;
            }
            default: {
                return LecternBlock.BASE_SHAPE;
            }
        }
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)LecternBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)LecternBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)LecternBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(LecternBlock.FACING, LecternBlock.POWERED, LecternBlock.HAS_BOOK);
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new LecternBlockEntity();
    }
    
    public static boolean putBookIfAbsent(final World world, final BlockPos pos, final BlockState state, final ItemStack book) {
        if (!state.<Boolean>get((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            if (!world.isClient) {
                putBook(world, pos, state, book);
            }
            return true;
        }
        return false;
    }
    
    private static void putBook(final World world, final BlockPos pos, final BlockState state, final ItemStack book) {
        final BlockEntity blockEntity5 = world.getBlockEntity(pos);
        if (blockEntity5 instanceof LecternBlockEntity) {
            final LecternBlockEntity lecternBlockEntity6 = (LecternBlockEntity)blockEntity5;
            lecternBlockEntity6.setBook(book.split(1));
            setHasBook(world, pos, state, true);
            world.playSound(null, pos, SoundEvents.ai, SoundCategory.e, 1.0f, 1.0f);
        }
    }
    
    public static void setHasBook(final World world, final BlockPos pos, final BlockState state, final boolean hasBook) {
        world.setBlockState(pos, (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)LecternBlock.POWERED, false)).<Comparable, Boolean>with((Property<Comparable>)LecternBlock.HAS_BOOK, hasBook), 3);
        updateNeighborAlways(world, pos, state);
    }
    
    public static void setPowered(final World world, final BlockPos pos, final BlockState state) {
        setPowered(world, pos, state, true);
        world.getBlockTickScheduler().schedule(pos, state.getBlock(), 2);
        world.playLevelEvent(1043, pos, 0);
    }
    
    private static void setPowered(final World world, final BlockPos blockPos, final BlockState state, final boolean powered) {
        world.setBlockState(blockPos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)LecternBlock.POWERED, powered), 3);
        updateNeighborAlways(world, blockPos, state);
    }
    
    private static void updateNeighborAlways(final World world, final BlockPos pos, final BlockState state) {
        world.updateNeighborsAlways(pos.down(), state.getBlock());
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.isClient) {
            return;
        }
        setPowered(world, pos, state, false);
    }
    
    @Override
    public void onBlockRemoved(final BlockState state, final World world, final BlockPos pos, final BlockState newState, final boolean boolean5) {
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        if (state.<Boolean>get((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            this.dropBook(state, world, pos);
        }
        if (state.<Boolean>get((Property<Boolean>)LecternBlock.POWERED)) {
            world.updateNeighborsAlways(pos.down(), this);
        }
        super.onBlockRemoved(state, world, pos, newState, boolean5);
    }
    
    private void dropBook(final BlockState state, final World world, final BlockPos pos) {
        final BlockEntity blockEntity4 = world.getBlockEntity(pos);
        if (blockEntity4 instanceof LecternBlockEntity) {
            final LecternBlockEntity lecternBlockEntity5 = (LecternBlockEntity)blockEntity4;
            final Direction direction6 = state.<Direction>get((Property<Direction>)LecternBlock.FACING);
            final ItemStack itemStack7 = lecternBlockEntity5.getBook().copy();
            final float float8 = 0.25f * direction6.getOffsetX();
            final float float9 = 0.25f * direction6.getOffsetZ();
            final ItemEntity itemEntity10 = new ItemEntity(world, pos.getX() + 0.5 + float8, pos.getY() + 1, pos.getZ() + 0.5 + float9, itemStack7);
            itemEntity10.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity10);
            lecternBlockEntity5.clear();
        }
    }
    
    @Override
    public boolean emitsRedstonePower(final BlockState state) {
        return true;
    }
    
    @Override
    public int getWeakRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return state.<Boolean>get((Property<Boolean>)LecternBlock.POWERED) ? 15 : 0;
    }
    
    @Override
    public int getStrongRedstonePower(final BlockState state, final BlockView view, final BlockPos pos, final Direction facing) {
        return (facing == Direction.UP && state.<Boolean>get((Property<Boolean>)LecternBlock.POWERED)) ? 15 : 0;
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        if (state.<Boolean>get((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            final BlockEntity blockEntity4 = world.getBlockEntity(pos);
            if (blockEntity4 instanceof LecternBlockEntity) {
                return ((LecternBlockEntity)blockEntity4).getComparatorOutput();
            }
        }
        return 0;
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (state.<Boolean>get((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            if (!world.isClient) {
                this.openContainer(world, pos, player);
            }
            return true;
        }
        return false;
    }
    
    @Nullable
    @Override
    public NameableContainerProvider createContainerProvider(final BlockState state, final World world, final BlockPos pos) {
        if (!state.<Boolean>get((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            return null;
        }
        return super.createContainerProvider(state, world, pos);
    }
    
    private void openContainer(final World world, final BlockPos pos, final PlayerEntity player) {
        final BlockEntity blockEntity4 = world.getBlockEntity(pos);
        if (blockEntity4 instanceof LecternBlockEntity) {
            player.openContainer((NameableContainerProvider)blockEntity4);
            player.incrementStat(Stats.as);
        }
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        POWERED = Properties.POWERED;
        HAS_BOOK = Properties.HAS_BOOK;
        BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
        BASE_SHAPE = VoxelShapes.union(LecternBlock.BOTTOM_SHAPE, LecternBlock.MIDDLE_SHAPE);
        COLLISION_SHAPE_TOP = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
        COLLISION_SHAPE = VoxelShapes.union(LecternBlock.BASE_SHAPE, LecternBlock.COLLISION_SHAPE_TOP);
        WEST_SHAPE = VoxelShapes.union(Block.createCuboidShape(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0), Block.createCuboidShape(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0), Block.createCuboidShape(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0), LecternBlock.BASE_SHAPE);
        NORTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333), Block.createCuboidShape(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667), Block.createCuboidShape(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0), LecternBlock.BASE_SHAPE);
        EAST_SHAPE = VoxelShapes.union(Block.createCuboidShape(15.0, 10.0, 0.0, 10.666667, 14.0, 16.0), Block.createCuboidShape(10.666667, 12.0, 0.0, 6.333333, 16.0, 16.0), Block.createCuboidShape(6.333333, 14.0, 0.0, 2.0, 18.0, 16.0), LecternBlock.BASE_SHAPE);
        SOUTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(0.0, 10.0, 15.0, 16.0, 14.0, 10.666667), Block.createCuboidShape(0.0, 12.0, 10.666667, 16.0, 16.0, 6.333333), Block.createCuboidShape(0.0, 14.0, 6.333333, 16.0, 18.0, 2.0), LecternBlock.BASE_SHAPE);
    }
}
