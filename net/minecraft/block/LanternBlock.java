package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.state.property.Properties;
import net.minecraft.world.IWorld;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.state.StateFactory;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import javax.annotation.Nullable;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;

public class LanternBlock extends Block
{
    public static final BooleanProperty HANGING;
    protected static final VoxelShape STANDING_SHAPE;
    protected static final VoxelShape HANGING_SHAPE;
    
    public LanternBlock(final Settings settings) {
        super(settings);
        this.setDefaultState(((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)LanternBlock.HANGING, false));
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        for (final Direction direction5 : ctx.getPlacementFacings()) {
            if (direction5.getAxis() == Direction.Axis.Y) {
                final BlockState blockState6 = ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Boolean>with((Property<Comparable>)LanternBlock.HANGING, direction5 == Direction.UP);
                if (blockState6.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                    return blockState6;
                }
            }
        }
        return null;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return state.<Boolean>get((Property<Boolean>)LanternBlock.HANGING) ? LanternBlock.HANGING_SHAPE : LanternBlock.STANDING_SHAPE;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(LanternBlock.HANGING);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final Direction direction4 = j(state).getOpposite();
        return Block.isSolidSmallSquare(world, pos.offset(direction4), direction4.getOpposite());
    }
    
    protected static Direction j(final BlockState blockState) {
        return blockState.<Boolean>get((Property<Boolean>)LanternBlock.HANGING) ? Direction.DOWN : Direction.UP;
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.b;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (j(state).getOpposite() == facing && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        HANGING = Properties.HANGING;
        STANDING_SHAPE = VoxelShapes.union(Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), Block.createCuboidShape(6.0, 7.0, 6.0, 10.0, 9.0, 10.0));
        HANGING_SHAPE = VoxelShapes.union(Block.createCuboidShape(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), Block.createCuboidShape(6.0, 8.0, 6.0, 10.0, 10.0, 10.0));
    }
}
