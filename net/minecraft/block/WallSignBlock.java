package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.DirectionProperty;

public class WallSignBlock extends AbstractSignBlock
{
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE;
    
    public WallSignBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)WallSignBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)WallSignBlock.WATERLOGGED, false));
    }
    
    @Override
    public String getTranslationKey() {
        return this.getItem().getTranslationKey();
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return WallSignBlock.FACING_TO_SHAPE.get(state.get((Property<Object>)WallSignBlock.FACING));
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        return world.getBlockState(pos.offset(state.<Direction>get((Property<Direction>)WallSignBlock.FACING).getOpposite())).getMaterial().isSolid();
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        BlockState blockState2 = this.getDefaultState();
        final FluidState fluidState3 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        final ViewableWorld viewableWorld4 = ctx.getWorld();
        final BlockPos blockPos5 = ctx.getBlockPos();
        final Direction[] placementFacings;
        final Direction[] arr6 = placementFacings = ctx.getPlacementFacings();
        for (final Direction direction10 : placementFacings) {
            if (direction10.getAxis().isHorizontal()) {
                final Direction direction11 = direction10.getOpposite();
                blockState2 = ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Direction>with((Property<Comparable>)WallSignBlock.FACING, direction11);
                if (blockState2.canPlaceAt(viewableWorld4, blockPos5)) {
                    return ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Boolean>with((Property<Comparable>)WallSignBlock.WATERLOGGED, fluidState3.getFluid() == Fluids.WATER);
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing.getOpposite() == state.<Direction>get((Property<Direction>)WallSignBlock.FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)WallSignBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)WallSignBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)WallSignBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(WallSignBlock.FACING, WallSignBlock.WATERLOGGED);
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        FACING_TO_SHAPE = Maps.<Direction, Object>newEnumMap(ImmutableMap.<Direction, VoxelShape>of(Direction.NORTH, Block.createCuboidShape(0.0, 4.5, 14.0, 16.0, 12.5, 16.0), Direction.SOUTH, Block.createCuboidShape(0.0, 4.5, 0.0, 16.0, 12.5, 2.0), Direction.EAST, Block.createCuboidShape(0.0, 4.5, 0.0, 2.0, 12.5, 16.0), Direction.WEST, Block.createCuboidShape(14.0, 4.5, 0.0, 16.0, 12.5, 16.0)));
    }
}
