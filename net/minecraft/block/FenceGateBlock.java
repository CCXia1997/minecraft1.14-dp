package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;

public class FenceGateBlock extends HorizontalFacingBlock
{
    public static final BooleanProperty OPEN;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty IN_WALL;
    protected static final VoxelShape d;
    protected static final VoxelShape e;
    protected static final VoxelShape f;
    protected static final VoxelShape g;
    protected static final VoxelShape h;
    protected static final VoxelShape i;
    protected static final VoxelShape j;
    protected static final VoxelShape k;
    protected static final VoxelShape w;
    protected static final VoxelShape x;
    
    public FenceGateBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)FenceGateBlock.OPEN, false)).with((Property<Comparable>)FenceGateBlock.POWERED, false)).<Comparable, Boolean>with((Property<Comparable>)FenceGateBlock.IN_WALL, false));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        if (state.<Boolean>get((Property<Boolean>)FenceGateBlock.IN_WALL)) {
            return (state.<Direction>get((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.X) ? FenceGateBlock.g : FenceGateBlock.f;
        }
        return (state.<Direction>get((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.X) ? FenceGateBlock.e : FenceGateBlock.d;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        final Direction.Axis axis7 = facing.getAxis();
        if (state.<Direction>get((Property<Direction>)FenceGateBlock.FACING).rotateYClockwise().getAxis() == axis7) {
            final boolean boolean8 = this.isWall(neighborState) || this.isWall(world.getBlockState(pos.offset(facing.getOpposite())));
            return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)FenceGateBlock.IN_WALL, boolean8);
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public VoxelShape getCollisionShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition ePos) {
        if (state.<Boolean>get((Property<Boolean>)FenceGateBlock.OPEN)) {
            return VoxelShapes.empty();
        }
        return (state.<Direction>get((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.Z) ? FenceGateBlock.h : FenceGateBlock.i;
    }
    
    @Override
    public VoxelShape h(final BlockState state, final BlockView view, final BlockPos pos) {
        if (state.<Boolean>get((Property<Boolean>)FenceGateBlock.IN_WALL)) {
            return (state.<Direction>get((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.X) ? FenceGateBlock.x : FenceGateBlock.w;
        }
        return (state.<Direction>get((Property<Direction>)FenceGateBlock.FACING).getAxis() == Direction.Axis.X) ? FenceGateBlock.k : FenceGateBlock.j;
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        switch (env) {
            case a: {
                return world.<Boolean>get((Property<Boolean>)FenceGateBlock.OPEN);
            }
            case b: {
                return false;
            }
            case c: {
                return world.<Boolean>get((Property<Boolean>)FenceGateBlock.OPEN);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final World world2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        final boolean boolean4 = world2.isReceivingRedstonePower(blockPos3);
        final Direction direction5 = ctx.getPlayerHorizontalFacing();
        final Direction.Axis axis6 = direction5.getAxis();
        final boolean boolean5 = (axis6 == Direction.Axis.Z && (this.isWall(world2.getBlockState(blockPos3.west())) || this.isWall(world2.getBlockState(blockPos3.east())))) || (axis6 == Direction.Axis.X && (this.isWall(world2.getBlockState(blockPos3.north())) || this.isWall(world2.getBlockState(blockPos3.south()))));
        return (((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)FenceGateBlock.FACING, direction5)).with((Property<Comparable>)FenceGateBlock.OPEN, boolean4)).with((Property<Comparable>)FenceGateBlock.POWERED, boolean4)).<Comparable, Boolean>with((Property<Comparable>)FenceGateBlock.IN_WALL, boolean5);
    }
    
    private boolean isWall(final BlockState state) {
        return state.getBlock().matches(BlockTags.z);
    }
    
    @Override
    public boolean activate(BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        if (state.<Boolean>get((Property<Boolean>)FenceGateBlock.OPEN)) {
            state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)FenceGateBlock.OPEN, false);
            world.setBlockState(pos, state, 10);
        }
        else {
            final Direction direction7 = player.getHorizontalFacing();
            if (state.<Comparable>get((Property<Comparable>)FenceGateBlock.FACING) == direction7.getOpposite()) {
                state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)FenceGateBlock.FACING, direction7);
            }
            state = ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)FenceGateBlock.OPEN, true);
            world.setBlockState(pos, state, 10);
        }
        world.playLevelEvent(player, ((boolean)state.<Boolean>get((Property<Boolean>)FenceGateBlock.OPEN)) ? 1008 : 1014, pos, 0);
        return true;
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (world.isClient) {
            return;
        }
        final boolean boolean7 = world.isReceivingRedstonePower(pos);
        if (state.<Boolean>get((Property<Boolean>)FenceGateBlock.POWERED) != boolean7) {
            world.setBlockState(pos, (((AbstractPropertyContainer<O, BlockState>)state).with((Property<Comparable>)FenceGateBlock.POWERED, boolean7)).<Comparable, Boolean>with((Property<Comparable>)FenceGateBlock.OPEN, boolean7), 2);
            if (state.<Boolean>get((Property<Boolean>)FenceGateBlock.OPEN) != boolean7) {
                world.playLevelEvent(null, boolean7 ? 1008 : 1014, pos, 0);
            }
        }
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FenceGateBlock.FACING, FenceGateBlock.OPEN, FenceGateBlock.POWERED, FenceGateBlock.IN_WALL);
    }
    
    public static boolean canWallConnect(final BlockState state, final Direction side) {
        return state.<Direction>get((Property<Direction>)FenceGateBlock.FACING).getAxis() == side.rotateYClockwise().getAxis();
    }
    
    static {
        OPEN = Properties.OPEN;
        POWERED = Properties.POWERED;
        IN_WALL = Properties.IN_WALL;
        d = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
        e = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
        f = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
        g = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
        h = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
        i = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
        j = VoxelShapes.union(Block.createCuboidShape(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), Block.createCuboidShape(14.0, 5.0, 7.0, 16.0, 16.0, 9.0));
        k = VoxelShapes.union(Block.createCuboidShape(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), Block.createCuboidShape(7.0, 5.0, 14.0, 9.0, 16.0, 16.0));
        w = VoxelShapes.union(Block.createCuboidShape(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), Block.createCuboidShape(14.0, 2.0, 7.0, 16.0, 13.0, 9.0));
        x = VoxelShapes.union(Block.createCuboidShape(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), Block.createCuboidShape(7.0, 2.0, 14.0, 9.0, 13.0, 16.0));
    }
}
