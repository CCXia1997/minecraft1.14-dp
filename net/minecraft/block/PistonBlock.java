package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import net.minecraft.world.IWorld;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;

public class PistonBlock extends FacingBlock
{
    public static final BooleanProperty EXTENDED;
    protected static final VoxelShape c;
    protected static final VoxelShape d;
    protected static final VoxelShape e;
    protected static final VoxelShape f;
    protected static final VoxelShape g;
    protected static final VoxelShape h;
    private final boolean isSticky;
    
    public PistonBlock(final boolean boolean1, final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)PistonBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)PistonBlock.EXTENDED, false));
        this.isSticky = boolean1;
    }
    
    @Override
    public boolean canSuffocate(final BlockState state, final BlockView view, final BlockPos pos) {
        return !state.<Boolean>get((Property<Boolean>)PistonBlock.EXTENDED);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        if (!state.<Boolean>get((Property<Boolean>)PistonBlock.EXTENDED)) {
            return VoxelShapes.fullCube();
        }
        switch (state.<Direction>get((Property<Direction>)PistonBlock.FACING)) {
            case DOWN: {
                return PistonBlock.h;
            }
            default: {
                return PistonBlock.g;
            }
            case NORTH: {
                return PistonBlock.f;
            }
            case SOUTH: {
                return PistonBlock.e;
            }
            case WEST: {
                return PistonBlock.d;
            }
            case EAST: {
                return PistonBlock.c;
            }
        }
    }
    
    @Override
    public boolean isSimpleFullBlock(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        if (!world.isClient) {
            this.a(world, pos, state);
        }
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (!world.isClient) {
            this.a(world, pos, state);
        }
    }
    
    @Override
    public void onBlockAdded(final BlockState state, final World world, final BlockPos pos, final BlockState oldState, final boolean boolean5) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        if (!world.isClient && world.getBlockEntity(pos) == null) {
            this.a(world, pos, state);
        }
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)PistonBlock.FACING, ctx.getPlayerFacing().getOpposite())).<Comparable, Boolean>with((Property<Comparable>)PistonBlock.EXTENDED, false);
    }
    
    private void a(final World world, final BlockPos blockPos, final BlockState blockState) {
        final Direction direction4 = blockState.<Direction>get((Property<Direction>)PistonBlock.FACING);
        final boolean boolean5 = this.a(world, blockPos, direction4);
        if (boolean5 && !blockState.<Boolean>get((Property<Boolean>)PistonBlock.EXTENDED)) {
            if (new PistonHandler(world, blockPos, direction4, true).calculatePush()) {
                world.addBlockAction(blockPos, this, 0, direction4.getId());
            }
        }
        else if (!boolean5 && blockState.<Boolean>get((Property<Boolean>)PistonBlock.EXTENDED)) {
            final BlockPos blockPos2 = blockPos.offset(direction4, 2);
            final BlockState blockState2 = world.getBlockState(blockPos2);
            int integer8 = 1;
            if (blockState2.getBlock() == Blocks.bn && blockState2.<Comparable>get((Property<Comparable>)PistonBlock.FACING) == direction4) {
                final BlockEntity blockEntity9 = world.getBlockEntity(blockPos2);
                if (blockEntity9 instanceof PistonBlockEntity) {
                    final PistonBlockEntity pistonBlockEntity10 = (PistonBlockEntity)blockEntity9;
                    if (pistonBlockEntity10.isExtending() && (pistonBlockEntity10.getProgress(0.0f) < 0.5f || world.getTime() == pistonBlockEntity10.getSavedWorldTime() || ((ServerWorld)world).isInsideTick())) {
                        integer8 = 2;
                    }
                }
            }
            world.addBlockAction(blockPos, this, integer8, direction4.getId());
        }
    }
    
    private boolean a(final World world, final BlockPos blockPos, final Direction direction) {
        for (final Direction direction2 : Direction.values()) {
            if (direction2 != direction && world.isEmittingRedstonePower(blockPos.offset(direction2), direction2)) {
                return true;
            }
        }
        if (world.isEmittingRedstonePower(blockPos, Direction.DOWN)) {
            return true;
        }
        final BlockPos blockPos2 = blockPos.up();
        for (final Direction direction3 : Direction.values()) {
            if (direction3 != Direction.DOWN && world.isEmittingRedstonePower(blockPos2.offset(direction3), direction3)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean onBlockAction(final BlockState state, final World world, final BlockPos pos, final int type, final int data) {
        final Direction direction6 = state.<Direction>get((Property<Direction>)PistonBlock.FACING);
        if (!world.isClient) {
            final boolean boolean7 = this.a(world, pos, direction6);
            if (boolean7 && (type == 1 || type == 2)) {
                world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)PistonBlock.EXTENDED, true), 2);
                return false;
            }
            if (!boolean7 && type == 0) {
                return false;
            }
        }
        if (type == 0) {
            if (!this.move(world, pos, direction6, true)) {
                return false;
            }
            world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)PistonBlock.EXTENDED, true), 67);
            world.playSound(null, pos, SoundEvents.ix, SoundCategory.e, 0.5f, world.random.nextFloat() * 0.25f + 0.6f);
        }
        else if (type == 1 || type == 2) {
            final BlockEntity blockEntity7 = world.getBlockEntity(pos.offset(direction6));
            if (blockEntity7 instanceof PistonBlockEntity) {
                ((PistonBlockEntity)blockEntity7).t();
            }
            world.setBlockState(pos, (((AbstractPropertyContainer<O, BlockState>)Blocks.bn.getDefaultState()).with((Property<Comparable>)PistonExtensionBlock.FACING, direction6)).<PistonType, PistonType>with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.b : PistonType.a), 3);
            world.setBlockEntity(pos, PistonExtensionBlock.createBlockEntityPiston(((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)PistonBlock.FACING, Direction.byId(data & 0x7)), direction6, false, true));
            if (this.isSticky) {
                final BlockPos blockPos8 = pos.add(direction6.getOffsetX() * 2, direction6.getOffsetY() * 2, direction6.getOffsetZ() * 2);
                final BlockState blockState9 = world.getBlockState(blockPos8);
                final Block block10 = blockState9.getBlock();
                boolean boolean8 = false;
                if (block10 == Blocks.bn) {
                    final BlockEntity blockEntity8 = world.getBlockEntity(blockPos8);
                    if (blockEntity8 instanceof PistonBlockEntity) {
                        final PistonBlockEntity pistonBlockEntity13 = (PistonBlockEntity)blockEntity8;
                        if (pistonBlockEntity13.getFacing() == direction6 && pistonBlockEntity13.isExtending()) {
                            pistonBlockEntity13.t();
                            boolean8 = true;
                        }
                    }
                }
                if (!boolean8) {
                    if (type == 1 && !blockState9.isAir() && isMovable(blockState9, world, blockPos8, direction6.getOpposite(), false, direction6) && (blockState9.getPistonBehavior() == PistonBehavior.a || block10 == Blocks.aV || block10 == Blocks.aO)) {
                        this.move(world, pos, direction6, false);
                    }
                    else {
                        world.clearBlockState(pos.offset(direction6), false);
                    }
                }
            }
            else {
                world.clearBlockState(pos.offset(direction6), false);
            }
            world.playSound(null, pos, SoundEvents.iw, SoundCategory.e, 0.5f, world.random.nextFloat() * 0.15f + 0.6f);
        }
        return true;
    }
    
    public static boolean isMovable(final BlockState state, final World world, final BlockPos pos, final Direction facing, final boolean canBreak, final Direction direction6) {
        final Block block7 = state.getBlock();
        if (block7 == Blocks.bJ) {
            return false;
        }
        if (!world.getWorldBorder().contains(pos)) {
            return false;
        }
        if (pos.getY() < 0 || (facing == Direction.DOWN && pos.getY() == 0)) {
            return false;
        }
        if (pos.getY() > world.getHeight() - 1 || (facing == Direction.UP && pos.getY() == world.getHeight() - 1)) {
            return false;
        }
        if (block7 == Blocks.aV || block7 == Blocks.aO) {
            if (state.<Boolean>get((Property<Boolean>)PistonBlock.EXTENDED)) {
                return false;
            }
        }
        else {
            if (state.getHardness(world, pos) == -1.0f) {
                return false;
            }
            switch (state.getPistonBehavior()) {
                case c: {
                    return false;
                }
                case b: {
                    return canBreak;
                }
                case e: {
                    return facing == direction6;
                }
            }
        }
        return !block7.hasBlockEntity();
    }
    
    private boolean move(final World world, final BlockPos pos, final Direction dir, final boolean boolean4) {
        final BlockPos blockPos5 = pos.offset(dir);
        if (!boolean4 && world.getBlockState(blockPos5).getBlock() == Blocks.aW) {
            world.setBlockState(blockPos5, Blocks.AIR.getDefaultState(), 20);
        }
        final PistonHandler pistonHandler6 = new PistonHandler(world, pos, dir, boolean4);
        if (!pistonHandler6.calculatePush()) {
            return false;
        }
        final List<BlockPos> list7 = pistonHandler6.getMovedBlocks();
        final List<BlockState> list8 = Lists.newArrayList();
        for (int integer9 = 0; integer9 < list7.size(); ++integer9) {
            final BlockPos blockPos6 = list7.get(integer9);
            list8.add(world.getBlockState(blockPos6));
        }
        final List<BlockPos> list9 = pistonHandler6.getBrokenBlocks();
        int integer10 = list7.size() + list9.size();
        final BlockState[] arr11 = new BlockState[integer10];
        final Direction direction12 = boolean4 ? dir : dir.getOpposite();
        final Set<BlockPos> set13 = Sets.newHashSet(list7);
        for (int integer11 = list9.size() - 1; integer11 >= 0; --integer11) {
            final BlockPos blockPos7 = list9.get(integer11);
            final BlockState blockState16 = world.getBlockState(blockPos7);
            final BlockEntity blockEntity17 = blockState16.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos7) : null;
            Block.dropStacks(blockState16, world, blockPos7, blockEntity17);
            world.setBlockState(blockPos7, Blocks.AIR.getDefaultState(), 18);
            arr11[--integer10] = blockState16;
        }
        for (int integer11 = list7.size() - 1; integer11 >= 0; --integer11) {
            BlockPos blockPos7 = list7.get(integer11);
            final BlockState blockState16 = world.getBlockState(blockPos7);
            blockPos7 = blockPos7.offset(direction12);
            set13.remove(blockPos7);
            world.setBlockState(blockPos7, ((AbstractPropertyContainer<O, BlockState>)Blocks.bn.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)PistonBlock.FACING, dir), 68);
            world.setBlockEntity(blockPos7, PistonExtensionBlock.createBlockEntityPiston(list8.get(integer11), dir, boolean4, false));
            arr11[--integer10] = blockState16;
        }
        if (boolean4) {
            final PistonType pistonType14 = this.isSticky ? PistonType.b : PistonType.a;
            final BlockState blockState17 = (((AbstractPropertyContainer<O, BlockState>)Blocks.aW.getDefaultState()).with((Property<Comparable>)PistonHeadBlock.FACING, dir)).<PistonType, PistonType>with(PistonHeadBlock.TYPE, pistonType14);
            final BlockState blockState16 = (((AbstractPropertyContainer<O, BlockState>)Blocks.bn.getDefaultState()).with((Property<Comparable>)PistonExtensionBlock.FACING, dir)).<PistonType, PistonType>with(PistonExtensionBlock.TYPE, this.isSticky ? PistonType.b : PistonType.a);
            set13.remove(blockPos5);
            world.setBlockState(blockPos5, blockState16, 68);
            world.setBlockEntity(blockPos5, PistonExtensionBlock.createBlockEntityPiston(blockState17, dir, true, true));
        }
        final Iterator<BlockPos> iterator = set13.iterator();
        while (iterator.hasNext()) {
            final BlockPos blockPos7 = iterator.next();
            world.setBlockState(blockPos7, Blocks.AIR.getDefaultState(), 66);
        }
        for (int integer11 = list9.size() - 1; integer11 >= 0; --integer11) {
            final BlockState blockState17 = arr11[integer10++];
            final BlockPos blockPos8 = list9.get(integer11);
            blockState17.b(world, blockPos8, 2);
            world.updateNeighborsAlways(blockPos8, blockState17.getBlock());
        }
        for (int integer11 = list7.size() - 1; integer11 >= 0; --integer11) {
            world.updateNeighborsAlways(list7.get(integer11), arr11[integer10++].getBlock());
        }
        if (boolean4) {
            world.updateNeighborsAlways(blockPos5, Blocks.aW);
        }
        return true;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)PistonBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)PistonBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)PistonBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PistonBlock.FACING, PistonBlock.EXTENDED);
    }
    
    @Override
    public boolean n(final BlockState state) {
        return state.<Boolean>get((Property<Boolean>)PistonBlock.EXTENDED);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        EXTENDED = Properties.EXTENDED;
        c = Block.createCuboidShape(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
        d = Block.createCuboidShape(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        e = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
        f = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
        g = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
        h = Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    }
}
