package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.math.Direction;
import java.util.Map;
import net.minecraft.state.property.DirectionProperty;

public class WallSkullBlock extends AbstractSkullBlock
{
    public static final DirectionProperty FACING;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE;
    
    protected WallSkullBlock(final SkullBlock.SkullType skullType, final Settings settings) {
        super(skullType, settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)WallSkullBlock.FACING, Direction.NORTH));
    }
    
    @Override
    public String getTranslationKey() {
        return this.getItem().getTranslationKey();
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return WallSkullBlock.FACING_TO_SHAPE.get(state.get((Property<Object>)WallSkullBlock.FACING));
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        BlockState blockState2 = this.getDefaultState();
        final BlockView blockView3 = ctx.getWorld();
        final BlockPos blockPos4 = ctx.getBlockPos();
        final Direction[] placementFacings;
        final Direction[] arr5 = placementFacings = ctx.getPlacementFacings();
        for (final Direction direction9 : placementFacings) {
            if (direction9.getAxis().isHorizontal()) {
                final Direction direction10 = direction9.getOpposite();
                blockState2 = ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Direction>with((Property<Comparable>)WallSkullBlock.FACING, direction10);
                if (!blockView3.getBlockState(blockPos4.offset(direction9)).canReplace(ctx)) {
                    return blockState2;
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)WallSkullBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)WallSkullBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)WallSkullBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(WallSkullBlock.FACING);
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        FACING_TO_SHAPE = Maps.<Direction, Object>newEnumMap(ImmutableMap.<Direction, VoxelShape>of(Direction.NORTH, Block.createCuboidShape(4.0, 4.0, 8.0, 12.0, 12.0, 16.0), Direction.SOUTH, Block.createCuboidShape(4.0, 4.0, 0.0, 12.0, 12.0, 8.0), Direction.EAST, Block.createCuboidShape(0.0, 4.0, 4.0, 8.0, 12.0, 12.0), Direction.WEST, Block.createCuboidShape(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)));
    }
}
