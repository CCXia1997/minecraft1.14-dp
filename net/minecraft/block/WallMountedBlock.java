package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.state.property.EnumProperty;

public class WallMountedBlock extends HorizontalFacingBlock
{
    public static final EnumProperty<WallMountLocation> FACE;
    
    protected WallMountedBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return canPlaceAt(world, pos, getDirection(state).getOpposite());
    }
    
    public static boolean canPlaceAt(final ViewableWorld world, final BlockPos pos, final Direction direction) {
        final BlockPos blockPos4 = pos.offset(direction);
        return Block.isSolidFullSquare(world.getBlockState(blockPos4), world, blockPos4, direction.getOpposite());
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        for (final Direction direction5 : ctx.getPlacementFacings()) {
            BlockState blockState6;
            if (direction5.getAxis() == Direction.Axis.Y) {
                blockState6 = (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with(WallMountedBlock.FACE, (direction5 == Direction.UP) ? WallMountLocation.c : WallMountLocation.a)).<Comparable, Direction>with((Property<Comparable>)WallMountedBlock.FACING, ctx.getPlayerHorizontalFacing());
            }
            else {
                blockState6 = (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with(WallMountedBlock.FACE, WallMountLocation.b)).<Comparable, Direction>with((Property<Comparable>)WallMountedBlock.FACING, direction5.getOpposite());
            }
            if (blockState6.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                return blockState6;
            }
        }
        return null;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (getDirection(state).getOpposite() == facing && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    protected static Direction getDirection(final BlockState state) {
        switch (state.<WallMountLocation>get(WallMountedBlock.FACE)) {
            case c: {
                return Direction.DOWN;
            }
            case a: {
                return Direction.UP;
            }
            default: {
                return state.<Direction>get((Property<Direction>)WallMountedBlock.FACING);
            }
        }
    }
    
    static {
        FACE = Properties.WALL_MOUNT_LOCATION;
    }
}
