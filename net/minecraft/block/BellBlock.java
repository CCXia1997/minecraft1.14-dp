package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.block.entity.BellBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.enums.Attachment;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.DirectionProperty;

public class BellBlock extends BlockWithEntity
{
    public static final DirectionProperty FACING;
    private static final EnumProperty<Attachment> ATTACHMENT;
    private static final VoxelShape NORTH_SOUTH_SHAPE;
    private static final VoxelShape EAST_WEST_SHAPE;
    private static final VoxelShape e;
    private static final VoxelShape f;
    private static final VoxelShape BELL_SHAPE;
    private static final VoxelShape NORTH_SOUTH_WALLS_SHAPE;
    private static final VoxelShape EAST_WEST_WALLS_SHAPE;
    private static final VoxelShape WEST_WALL_SHAPE;
    private static final VoxelShape EAST_WALL_SHAPE;
    private static final VoxelShape NORTH_WALL_SHAPE;
    private static final VoxelShape SOUTH_WALL_SHAPE;
    private static final VoxelShape HANGING_SHAPE;
    
    public BellBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)BellBlock.FACING, Direction.NORTH)).<Attachment, Attachment>with(BellBlock.ATTACHMENT, Attachment.a));
    }
    
    @Override
    public void onProjectileHit(final World world, final BlockState state, final BlockHitResult hitResult, final Entity entity) {
        if (entity instanceof ProjectileEntity) {
            final Entity entity2 = ((ProjectileEntity)entity).getOwner();
            final PlayerEntity playerEntity6 = (entity2 instanceof PlayerEntity) ? ((PlayerEntity)entity2) : null;
            this.ring(world, state, world.getBlockEntity(hitResult.getBlockPos()), hitResult, playerEntity6);
        }
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        return this.ring(world, state, world.getBlockEntity(pos), blockHitResult, player);
    }
    
    public boolean ring(final World world, final BlockState state, @Nullable final BlockEntity blockEntity, final BlockHitResult hitPosition, @Nullable final PlayerEntity playerEntity) {
        final Direction direction6 = hitPosition.getSide();
        final BlockPos blockPos7 = hitPosition.getBlockPos();
        if (!world.isClient && blockEntity instanceof BellBlockEntity && this.isPointOnBell(state, direction6, hitPosition.getPos().y - blockPos7.getY())) {
            ((BellBlockEntity)blockEntity).activate(direction6);
            this.ring(world, blockPos7);
            if (playerEntity != null) {
                playerEntity.incrementStat(Stats.ax);
            }
            return true;
        }
        return true;
    }
    
    private boolean isPointOnBell(final BlockState state, final Direction side, final double y) {
        if (side.getAxis() == Direction.Axis.Y || y > 0.8123999834060669) {
            return false;
        }
        final Direction direction5 = state.<Direction>get((Property<Direction>)BellBlock.FACING);
        final Attachment attachment6 = state.<Attachment>get(BellBlock.ATTACHMENT);
        switch (attachment6) {
            case a: {
                return direction5.getAxis() == side.getAxis();
            }
            case c:
            case d: {
                return direction5.getAxis() != side.getAxis();
            }
            case b: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void ring(final World world, final BlockPos pos) {
        world.playSound(null, pos, SoundEvents.Y, SoundCategory.e, 2.0f, 1.0f);
    }
    
    private VoxelShape getShape(final BlockState state) {
        final Direction direction2 = state.<Direction>get((Property<Direction>)BellBlock.FACING);
        final Attachment attachment3 = state.<Attachment>get(BellBlock.ATTACHMENT);
        if (attachment3 == Attachment.a) {
            if (direction2 == Direction.NORTH || direction2 == Direction.SOUTH) {
                return BellBlock.NORTH_SOUTH_SHAPE;
            }
            return BellBlock.EAST_WEST_SHAPE;
        }
        else {
            if (attachment3 == Attachment.b) {
                return BellBlock.HANGING_SHAPE;
            }
            if (attachment3 == Attachment.d) {
                if (direction2 == Direction.NORTH || direction2 == Direction.SOUTH) {
                    return BellBlock.NORTH_SOUTH_WALLS_SHAPE;
                }
                return BellBlock.EAST_WEST_WALLS_SHAPE;
            }
            else {
                if (direction2 == Direction.NORTH) {
                    return BellBlock.NORTH_WALL_SHAPE;
                }
                if (direction2 == Direction.SOUTH) {
                    return BellBlock.SOUTH_WALL_SHAPE;
                }
                if (direction2 == Direction.EAST) {
                    return BellBlock.EAST_WALL_SHAPE;
                }
                return BellBlock.WEST_WALL_SHAPE;
            }
        }
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        return this.getShape(state);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return this.getShape(state);
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final Direction direction3 = ctx.getFacing();
        final BlockPos blockPos4 = ctx.getBlockPos();
        final World world5 = ctx.getWorld();
        final Direction.Axis axis6 = direction3.getAxis();
        if (axis6 == Direction.Axis.Y) {
            final BlockState blockState2 = (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with(BellBlock.ATTACHMENT, (direction3 == Direction.DOWN) ? Attachment.b : Attachment.a)).<Comparable, Direction>with((Property<Comparable>)BellBlock.FACING, ctx.getPlayerHorizontalFacing());
            if (blockState2.canPlaceAt(ctx.getWorld(), blockPos4)) {
                return blockState2;
            }
        }
        else {
            final boolean boolean7 = (axis6 == Direction.Axis.X && Block.isSolidFullSquare(world5.getBlockState(blockPos4.west()), world5, blockPos4.west(), Direction.EAST) && Block.isSolidFullSquare(world5.getBlockState(blockPos4.east()), world5, blockPos4.east(), Direction.WEST)) || (axis6 == Direction.Axis.Z && Block.isSolidFullSquare(world5.getBlockState(blockPos4.north()), world5, blockPos4.north(), Direction.SOUTH) && Block.isSolidFullSquare(world5.getBlockState(blockPos4.south()), world5, blockPos4.south(), Direction.NORTH));
            BlockState blockState2 = (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)BellBlock.FACING, direction3.getOpposite())).<Attachment, Attachment>with(BellBlock.ATTACHMENT, boolean7 ? Attachment.d : Attachment.c);
            if (blockState2.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                return blockState2;
            }
            final boolean boolean8 = Block.isSolidFullSquare(world5.getBlockState(blockPos4.down()), world5, blockPos4.down(), Direction.UP);
            blockState2 = ((AbstractPropertyContainer<O, BlockState>)blockState2).<Attachment, Attachment>with(BellBlock.ATTACHMENT, boolean8 ? Attachment.a : Attachment.b);
            if (blockState2.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                return blockState2;
            }
        }
        return null;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        final Attachment attachment7 = state.<Attachment>get(BellBlock.ATTACHMENT);
        final Direction direction8 = getPlacementSide(state).getOpposite();
        if (direction8 == facing && !state.canPlaceAt(world, pos) && attachment7 != Attachment.d) {
            return Blocks.AIR.getDefaultState();
        }
        if (facing.getAxis() == state.<Direction>get((Property<Direction>)BellBlock.FACING).getAxis()) {
            if (attachment7 == Attachment.d && !Block.isSolidFullSquare(neighborState, world, neighborPos, facing)) {
                return (((AbstractPropertyContainer<O, BlockState>)state).with(BellBlock.ATTACHMENT, Attachment.c)).<Comparable, Direction>with((Property<Comparable>)BellBlock.FACING, facing.getOpposite());
            }
            if (attachment7 == Attachment.c && direction8.getOpposite() == facing && Block.isSolidFullSquare(neighborState, world, neighborPos, state.<Direction>get((Property<Direction>)BellBlock.FACING))) {
                return ((AbstractPropertyContainer<O, BlockState>)state).<Attachment, Attachment>with(BellBlock.ATTACHMENT, Attachment.d);
            }
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return WallMountedBlock.canPlaceAt(world, pos, getPlacementSide(state).getOpposite());
    }
    
    private static Direction getPlacementSide(final BlockState state) {
        switch (state.<Attachment>get(BellBlock.ATTACHMENT)) {
            case b: {
                return Direction.DOWN;
            }
            case a: {
                return Direction.UP;
            }
            default: {
                return state.<Direction>get((Property<Direction>)BellBlock.FACING).getOpposite();
            }
        }
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.b;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(BellBlock.FACING, BellBlock.ATTACHMENT);
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        return new BellBlockEntity();
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        ATTACHMENT = Properties.ATTACHMENT;
        NORTH_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
        EAST_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
        e = Block.createCuboidShape(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
        f = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
        BELL_SHAPE = VoxelShapes.union(BellBlock.f, BellBlock.e);
        NORTH_SOUTH_WALLS_SHAPE = VoxelShapes.union(BellBlock.BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
        EAST_WEST_WALLS_SHAPE = VoxelShapes.union(BellBlock.BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
        WEST_WALL_SHAPE = VoxelShapes.union(BellBlock.BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
        EAST_WALL_SHAPE = VoxelShapes.union(BellBlock.BELL_SHAPE, Block.createCuboidShape(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
        NORTH_WALL_SHAPE = VoxelShapes.union(BellBlock.BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
        SOUTH_WALL_SHAPE = VoxelShapes.union(BellBlock.BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
        HANGING_SHAPE = VoxelShapes.union(BellBlock.BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));
    }
}
