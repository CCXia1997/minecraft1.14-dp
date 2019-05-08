package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.DirectionProperty;

public class WallBannerBlock extends AbstractBannerBlock
{
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE;
    
    public WallBannerBlock(final DyeColor dyeColor, final Settings settings) {
        super(dyeColor, settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallBannerBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public String getTranslationKey() {
        return this.getItem().getTranslationKey();
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return world.getBlockState(pos.offset(state.<Direction>get((Property<Direction>)WallBannerBlock.FACING).getOpposite())).getMaterial().isSolid();
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == state.<Direction>get((Property<Direction>)WallBannerBlock.FACING).getOpposite() && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return WallBannerBlock.FACING_TO_SHAPE.get(state.get((Property<Object>)WallBannerBlock.FACING));
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        BlockState blockState2 = this.getDefaultState();
        final ViewableWorld viewableWorld3 = ctx.getWorld();
        final BlockPos blockPos4 = ctx.getBlockPos();
        final Direction[] placementFacings;
        final Direction[] arr5 = placementFacings = ctx.getPlacementFacings();
        for (final Direction direction9 : placementFacings) {
            if (direction9.getAxis().isHorizontal()) {
                final Direction direction10 = direction9.getOpposite();
                blockState2 = ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Direction>with((Property<Comparable>)WallBannerBlock.FACING, direction10);
                if (blockState2.canPlaceAt(viewableWorld3, blockPos4)) {
                    return blockState2;
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)WallBannerBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)WallBannerBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)WallBannerBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(WallBannerBlock.FACING);
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        FACING_TO_SHAPE = Maps.<Direction, Object>newEnumMap(ImmutableMap.<Direction, VoxelShape>of(Direction.NORTH, Block.createCuboidShape(0.0, 0.0, 14.0, 16.0, 12.5, 16.0), Direction.SOUTH, Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.5, 2.0), Direction.WEST, Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 12.5, 16.0), Direction.EAST, Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 12.5, 16.0)));
    }
}
