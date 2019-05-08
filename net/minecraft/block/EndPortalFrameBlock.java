package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.state.property.Properties;
import java.util.function.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;

public class EndPortalFrameBlock extends Block
{
    public static final DirectionProperty FACING;
    public static final BooleanProperty EYE;
    protected static final VoxelShape FRAME_SHAPE;
    protected static final VoxelShape EYE_SHAPE;
    protected static final VoxelShape FRAME_WITH_EYE_SHAPE;
    private static BlockPattern COMPLETED_FRAME;
    
    public EndPortalFrameBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)EndPortalFrameBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, false));
    }
    
    @Override
    public boolean n(final BlockState state) {
        return true;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return state.<Boolean>get((Property<Boolean>)EndPortalFrameBlock.EYE) ? EndPortalFrameBlock.FRAME_WITH_EYE_SHAPE : EndPortalFrameBlock.FRAME_SHAPE;
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return (((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)EndPortalFrameBlock.FACING, ctx.getPlayerHorizontalFacing().getOpposite())).<Comparable, Boolean>with((Property<Comparable>)EndPortalFrameBlock.EYE, false);
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        if (state.<Boolean>get((Property<Boolean>)EndPortalFrameBlock.EYE)) {
            return 15;
        }
        return 0;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)EndPortalFrameBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)EndPortalFrameBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)EndPortalFrameBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(EndPortalFrameBlock.FACING, EndPortalFrameBlock.EYE);
    }
    
    public static BlockPattern getCompletedFramePattern() {
        if (EndPortalFrameBlock.COMPLETED_FRAME == null) {
            EndPortalFrameBlock.COMPLETED_FRAME = BlockPatternBuilder.start().aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?").where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY)).where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.dV).<Comparable>with((Property<Comparable>)EndPortalFrameBlock.EYE, Predicates.equalTo(true)).<Comparable>with((Property<Comparable>)EndPortalFrameBlock.FACING, Predicates.equalTo(Direction.SOUTH)))).where('>', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.dV).<Comparable>with((Property<Comparable>)EndPortalFrameBlock.EYE, Predicates.equalTo(true)).<Comparable>with((Property<Comparable>)EndPortalFrameBlock.FACING, Predicates.equalTo(Direction.WEST)))).where('v', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.dV).<Comparable>with((Property<Comparable>)EndPortalFrameBlock.EYE, Predicates.equalTo(true)).<Comparable>with((Property<Comparable>)EndPortalFrameBlock.FACING, Predicates.equalTo(Direction.NORTH)))).where('<', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.dV).<Comparable>with((Property<Comparable>)EndPortalFrameBlock.EYE, Predicates.equalTo(true)).<Comparable>with((Property<Comparable>)EndPortalFrameBlock.FACING, Predicates.equalTo(Direction.EAST)))).build();
        }
        return EndPortalFrameBlock.COMPLETED_FRAME;
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
    
    static {
        FACING = HorizontalFacingBlock.FACING;
        EYE = Properties.EYE;
        FRAME_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
        EYE_SHAPE = Block.createCuboidShape(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
        FRAME_WITH_EYE_SHAPE = VoxelShapes.union(EndPortalFrameBlock.FRAME_SHAPE, EndPortalFrameBlock.EYE_SHAPE);
    }
}
