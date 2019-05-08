package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class DoorBlock extends Block
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;
    public static final EnumProperty<DoorHinge> HINGE;
    public static final BooleanProperty POWERED;
    public static final EnumProperty<DoubleBlockHalf> HALF;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape EAST_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    
    protected DoorBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)DoorBlock.FACING, Direction.NORTH)).with((Property<Comparable>)DoorBlock.OPEN, false)).with(DoorBlock.HINGE, DoorHinge.a)).with((Property<Comparable>)DoorBlock.POWERED, false)).<DoubleBlockHalf, DoubleBlockHalf>with(DoorBlock.HALF, DoubleBlockHalf.b));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final Direction direction5 = state.<Direction>get((Property<Direction>)DoorBlock.FACING);
        final boolean boolean6 = !state.<Boolean>get((Property<Boolean>)DoorBlock.OPEN);
        final boolean boolean7 = state.<DoorHinge>get(DoorBlock.HINGE) == DoorHinge.b;
        switch (direction5) {
            default: {
                return boolean6 ? DoorBlock.WEST_SHAPE : (boolean7 ? DoorBlock.SOUTH_SHAPE : DoorBlock.NORTH_SHAPE);
            }
            case SOUTH: {
                return boolean6 ? DoorBlock.NORTH_SHAPE : (boolean7 ? DoorBlock.WEST_SHAPE : DoorBlock.EAST_SHAPE);
            }
            case WEST: {
                return boolean6 ? DoorBlock.EAST_SHAPE : (boolean7 ? DoorBlock.NORTH_SHAPE : DoorBlock.SOUTH_SHAPE);
            }
            case NORTH: {
                return boolean6 ? DoorBlock.SOUTH_SHAPE : (boolean7 ? DoorBlock.EAST_SHAPE : DoorBlock.WEST_SHAPE);
            }
        }
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        final DoubleBlockHalf doubleBlockHalf7 = state.<DoubleBlockHalf>get(DoorBlock.HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleBlockHalf7 == DoubleBlockHalf.b == (facing == Direction.UP)) {
            if (neighborState.getBlock() == this && neighborState.<DoubleBlockHalf>get(DoorBlock.HALF) != doubleBlockHalf7) {
                return (((((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)DoorBlock.FACING, (Comparable)neighborState.<V>get((Property<V>)DoorBlock.FACING))).with((Property<Comparable>)DoorBlock.OPEN, (Comparable)neighborState.<V>get((Property<V>)DoorBlock.OPEN))).with(DoorBlock.HINGE, (Comparable)neighborState.<V>get((Property<V>)DoorBlock.HINGE))).<Comparable, Comparable>with((Property<Comparable>)DoorBlock.POWERED, (Comparable)neighborState.<V>get((Property<V>)DoorBlock.POWERED));
            }
            return Blocks.AIR.getDefaultState();
        }
        else {
            if (doubleBlockHalf7 == DoubleBlockHalf.b && facing == Direction.DOWN && !state.canPlaceAt(world, pos)) {
                return Blocks.AIR.getDefaultState();
            }
            return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        }
    }
    
    @Override
    public void afterBreak(final World world, final PlayerEntity player, final BlockPos pos, final BlockState state, @Nullable final BlockEntity blockEntity, final ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
    }
    
    @Override
    public void onBreak(final World world, final BlockPos pos, final BlockState state, final PlayerEntity player) {
        final DoubleBlockHalf doubleBlockHalf5 = state.<DoubleBlockHalf>get(DoorBlock.HALF);
        final BlockPos blockPos6 = (doubleBlockHalf5 == DoubleBlockHalf.b) ? pos.up() : pos.down();
        final BlockState blockState7 = world.getBlockState(blockPos6);
        if (blockState7.getBlock() == this && blockState7.<DoubleBlockHalf>get(DoorBlock.HALF) != doubleBlockHalf5) {
            world.setBlockState(blockPos6, Blocks.AIR.getDefaultState(), 35);
            world.playLevelEvent(player, 2001, blockPos6, Block.getRawIdFromState(blockState7));
            final ItemStack itemStack8 = player.getMainHandStack();
            if (!world.isClient && !player.isCreative()) {
                Block.dropStacks(state, world, pos, null, player, itemStack8);
                Block.dropStacks(blockState7, world, blockPos6, null, player, itemStack8);
            }
        }
        super.onBreak(world, pos, state, player);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        switch (env) {
            case a: {
                return world.<Boolean>get((Property<Boolean>)DoorBlock.OPEN);
            }
            case b: {
                return false;
            }
            case c: {
                return world.<Boolean>get((Property<Boolean>)DoorBlock.OPEN);
            }
            default: {
                return false;
            }
        }
    }
    
    private int getOpenSoundEventId() {
        return (this.material == Material.METAL) ? 1011 : 1012;
    }
    
    private int getCloseSoundEventId() {
        return (this.material == Material.METAL) ? 1005 : 1006;
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockPos blockPos2 = ctx.getBlockPos();
        if (blockPos2.getY() < 255 && ctx.getWorld().getBlockState(blockPos2.up()).canReplace(ctx)) {
            final World world3 = ctx.getWorld();
            final boolean boolean4 = world3.isReceivingRedstonePower(blockPos2) || world3.isReceivingRedstonePower(blockPos2.up());
            return ((((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)DoorBlock.FACING, ctx.getPlayerHorizontalFacing())).with(DoorBlock.HINGE, this.getHinge(ctx))).with((Property<Comparable>)DoorBlock.POWERED, boolean4)).with((Property<Comparable>)DoorBlock.OPEN, boolean4)).<DoubleBlockHalf, DoubleBlockHalf>with(DoorBlock.HALF, DoubleBlockHalf.b);
        }
        return null;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        world.setBlockState(pos.up(), ((AbstractPropertyContainer<O, BlockState>)state).<DoubleBlockHalf, DoubleBlockHalf>with(DoorBlock.HALF, DoubleBlockHalf.a), 3);
    }
    
    private DoorHinge getHinge(final ItemPlacementContext ctx) {
        final BlockView blockView2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        final Direction direction4 = ctx.getPlayerHorizontalFacing();
        final BlockPos blockPos4 = blockPos3.up();
        final Direction direction5 = direction4.rotateYCounterclockwise();
        final BlockPos blockPos5 = blockPos3.offset(direction5);
        final BlockState blockState8 = blockView2.getBlockState(blockPos5);
        final BlockPos blockPos6 = blockPos4.offset(direction5);
        final BlockState blockState9 = blockView2.getBlockState(blockPos6);
        final Direction direction6 = direction4.rotateYClockwise();
        final BlockPos blockPos7 = blockPos3.offset(direction6);
        final BlockState blockState10 = blockView2.getBlockState(blockPos7);
        final BlockPos blockPos8 = blockPos4.offset(direction6);
        final BlockState blockState11 = blockView2.getBlockState(blockPos8);
        final int integer16 = (Block.isShapeFullCube(blockState8.getCollisionShape(blockView2, blockPos5)) ? -1 : 0) + (Block.isShapeFullCube(blockState9.getCollisionShape(blockView2, blockPos6)) ? -1 : 0) + (Block.isShapeFullCube(blockState10.getCollisionShape(blockView2, blockPos7)) ? 1 : 0) + (Block.isShapeFullCube(blockState11.getCollisionShape(blockView2, blockPos8)) ? 1 : 0);
        final boolean boolean17 = blockState8.getBlock() == this && blockState8.<DoubleBlockHalf>get(DoorBlock.HALF) == DoubleBlockHalf.b;
        final boolean boolean18 = blockState10.getBlock() == this && blockState10.<DoubleBlockHalf>get(DoorBlock.HALF) == DoubleBlockHalf.b;
        if ((boolean17 && !boolean18) || integer16 > 0) {
            return DoorHinge.b;
        }
        if ((boolean18 && !boolean17) || integer16 < 0) {
            return DoorHinge.a;
        }
        final int integer17 = direction4.getOffsetX();
        final int integer18 = direction4.getOffsetZ();
        final Vec3d vec3d21 = ctx.getPos();
        final double double22 = vec3d21.x - blockPos3.getX();
        final double double23 = vec3d21.z - blockPos3.getZ();
        return ((integer17 < 0 && double23 < 0.5) || (integer17 > 0 && double23 > 0.5) || (integer18 < 0 && double22 > 0.5) || (integer18 > 0 && double22 < 0.5)) ? DoorHinge.b : DoorHinge.a;
    }
    
    @Override
    public boolean activate(BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (this.material == Material.METAL) {
            return false;
        }
        state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable>cycle((Property<Comparable>)DoorBlock.OPEN);
        world.setBlockState(pos, state, 10);
        world.playLevelEvent(player, ((boolean)state.<Boolean>get((Property<Boolean>)DoorBlock.OPEN)) ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), pos, 0);
        return true;
    }
    
    public void setOpen(final World world, final BlockPos pos, final boolean open) {
        final BlockState blockState4 = world.getBlockState(pos);
        if (blockState4.getBlock() != this || blockState4.<Boolean>get((Property<Boolean>)DoorBlock.OPEN) == open) {
            return;
        }
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)blockState4).<Comparable, Boolean>with((Property<Comparable>)DoorBlock.OPEN, open), 10);
        this.playOpenCloseSound(world, pos, open);
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        final boolean boolean7 = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.offset((state.<DoubleBlockHalf>get(DoorBlock.HALF) == DoubleBlockHalf.b) ? Direction.UP : Direction.DOWN));
        if (block != this && boolean7 != state.<Boolean>get((Property<Boolean>)DoorBlock.POWERED)) {
            if (boolean7 != state.<Boolean>get((Property<Boolean>)DoorBlock.OPEN)) {
                this.playOpenCloseSound(world, pos, boolean7);
            }
            world.setBlockState(pos, (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)DoorBlock.POWERED, boolean7)).<Comparable, Boolean>with((Property<Comparable>)DoorBlock.OPEN, boolean7), 2);
        }
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        final BlockState blockState5 = world.getBlockState(blockPos4);
        if (state.<DoubleBlockHalf>get(DoorBlock.HALF) == DoubleBlockHalf.b) {
            return Block.isSolidFullSquare(blockState5, world, blockPos4, Direction.UP);
        }
        return blockState5.getBlock() == this;
    }
    
    private void playOpenCloseSound(final World world, final BlockPos blockPos, final boolean open) {
        world.playLevelEvent(null, open ? this.getCloseSoundEventId() : this.getOpenSoundEventId(), blockPos, 0);
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.b;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)DoorBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)DoorBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        if (mirror == BlockMirror.NONE) {
            return state;
        }
        return ((AbstractPropertyContainer<O, BlockState>)state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)DoorBlock.FACING)))).<DoorHinge>cycle(DoorBlock.HINGE);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public long getRenderingSeed(final BlockState state, final BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down((state.<DoubleBlockHalf>get(DoorBlock.HALF) != DoubleBlockHalf.b) ? 1 : 0).getY(), pos.getZ());
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(DoorBlock.HALF, DoorBlock.FACING, DoorBlock.OPEN, DoorBlock.HINGE, DoorBlock.POWERED);
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        OPEN = Properties.OPEN;
        HINGE = Properties.DOOR_HINGE;
        POWERED = Properties.POWERED;
        HALF = Properties.DOUBLE_BLOCK_HALF;
        NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
        EAST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
    }
}
