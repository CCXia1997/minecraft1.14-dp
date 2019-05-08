package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.IWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.DirectionProperty;

public class DeadCoralWallFanBlock extends DeadCoralFanBlock
{
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE;
    
    protected DeadCoralWallFanBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)DeadCoralWallFanBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)DeadCoralWallFanBlock.WATERLOGGED, true));
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return DeadCoralWallFanBlock.FACING_TO_SHAPE.get(state.get((Property<Object>)DeadCoralWallFanBlock.FACING));
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)DeadCoralWallFanBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)DeadCoralWallFanBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)DeadCoralWallFanBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(DeadCoralWallFanBlock.FACING, DeadCoralWallFanBlock.WATERLOGGED);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)DeadCoralWallFanBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (facing.getOpposite() == state.<Direction>get((Property<Direction>)DeadCoralWallFanBlock.FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final Direction direction4 = state.<Direction>get((Property<Direction>)DeadCoralWallFanBlock.FACING);
        final BlockPos blockPos5 = pos.offset(direction4.getOpposite());
        final BlockState blockState6 = world.getBlockState(blockPos5);
        return Block.isSolidFullSquare(blockState6, world, blockPos5, direction4);
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        BlockState blockState2 = super.getPlacementState(ctx);
        final ViewableWorld viewableWorld3 = ctx.getWorld();
        final BlockPos blockPos4 = ctx.getBlockPos();
        final Direction[] placementFacings;
        final Direction[] arr5 = placementFacings = ctx.getPlacementFacings();
        for (final Direction direction9 : placementFacings) {
            if (direction9.getAxis().isHorizontal()) {
                blockState2 = ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Direction>with((Property<Comparable>)DeadCoralWallFanBlock.FACING, direction9.getOpposite());
                if (blockState2.canPlaceAt(viewableWorld3, blockPos4)) {
                    return blockState2;
                }
            }
        }
        return null;
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        FACING_TO_SHAPE = Maps.<Direction, Object>newEnumMap(ImmutableMap.<Direction, VoxelShape>of(Direction.NORTH, Block.createCuboidShape(0.0, 4.0, 5.0, 16.0, 12.0, 16.0), Direction.SOUTH, Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 12.0, 11.0), Direction.WEST, Block.createCuboidShape(5.0, 4.0, 0.0, 16.0, 12.0, 16.0), Direction.EAST, Block.createCuboidShape(0.0, 4.0, 0.0, 11.0, 12.0, 16.0)));
    }
}
