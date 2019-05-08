package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import net.minecraft.state.StateFactory;
import net.minecraft.world.IWorld;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.world.BlockView;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.ViewableWorld;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.state.property.IntegerProperty;

public class CocoaBlock extends HorizontalFacingBlock implements Fertilizable
{
    public static final IntegerProperty AGE;
    protected static final VoxelShape[] AGE_TO_EAST_SHAPE;
    protected static final VoxelShape[] AGE_TO_WEST_SHAPE;
    protected static final VoxelShape[] AGE_TO_NORTH_SHAPE;
    protected static final VoxelShape[] AGE_TO_SOUTH_SHAPE;
    
    public CocoaBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)CocoaBlock.FACING, Direction.NORTH)).<Comparable, Integer>with((Property<Comparable>)CocoaBlock.AGE, 0));
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.random.nextInt(5) == 0) {
            final int integer5 = state.<Integer>get((Property<Integer>)CocoaBlock.AGE);
            if (integer5 < 2) {
                world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)CocoaBlock.AGE, integer5 + 1), 2);
            }
        }
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final Block block4 = world.getBlockState(pos.offset(state.<Direction>get((Property<Direction>)CocoaBlock.FACING))).getBlock();
        return block4.matches(BlockTags.t);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        final int integer5 = state.<Integer>get((Property<Integer>)CocoaBlock.AGE);
        switch (state.<Direction>get((Property<Direction>)CocoaBlock.FACING)) {
            case SOUTH: {
                return CocoaBlock.AGE_TO_SOUTH_SHAPE[integer5];
            }
            default: {
                return CocoaBlock.AGE_TO_NORTH_SHAPE[integer5];
            }
            case WEST: {
                return CocoaBlock.AGE_TO_WEST_SHAPE[integer5];
            }
            case EAST: {
                return CocoaBlock.AGE_TO_EAST_SHAPE[integer5];
            }
        }
    }
    
    @Nullable
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        BlockState blockState2 = this.getDefaultState();
        final ViewableWorld viewableWorld3 = ctx.getWorld();
        final BlockPos blockPos4 = ctx.getBlockPos();
        for (final Direction direction8 : ctx.getPlacementFacings()) {
            if (direction8.getAxis().isHorizontal()) {
                blockState2 = ((AbstractPropertyContainer<O, BlockState>)blockState2).<Comparable, Direction>with((Property<Comparable>)CocoaBlock.FACING, direction8);
                if (blockState2.canPlaceAt(viewableWorld3, blockPos4)) {
                    return blockState2;
                }
            }
        }
        return null;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (facing == state.<Direction>get((Property<Direction>)CocoaBlock.FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean isFertilizable(final BlockView world, final BlockPos pos, final BlockState state, final boolean boolean4) {
        return state.<Integer>get((Property<Integer>)CocoaBlock.AGE) < 2;
    }
    
    @Override
    public boolean canGrow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        return true;
    }
    
    @Override
    public void grow(final World world, final Random random, final BlockPos pos, final BlockState state) {
        world.setBlockState(pos, ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Integer>with((Property<Comparable>)CocoaBlock.AGE, state.<Integer>get((Property<Integer>)CocoaBlock.AGE) + 1), 2);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(CocoaBlock.FACING, CocoaBlock.AGE);
    }
    
    static {
        AGE = Properties.AGE_2;
        AGE_TO_EAST_SHAPE = new VoxelShape[] { Block.createCuboidShape(11.0, 7.0, 6.0, 15.0, 12.0, 10.0), Block.createCuboidShape(9.0, 5.0, 5.0, 15.0, 12.0, 11.0), Block.createCuboidShape(7.0, 3.0, 4.0, 15.0, 12.0, 12.0) };
        AGE_TO_WEST_SHAPE = new VoxelShape[] { Block.createCuboidShape(1.0, 7.0, 6.0, 5.0, 12.0, 10.0), Block.createCuboidShape(1.0, 5.0, 5.0, 7.0, 12.0, 11.0), Block.createCuboidShape(1.0, 3.0, 4.0, 9.0, 12.0, 12.0) };
        AGE_TO_NORTH_SHAPE = new VoxelShape[] { Block.createCuboidShape(6.0, 7.0, 1.0, 10.0, 12.0, 5.0), Block.createCuboidShape(5.0, 5.0, 1.0, 11.0, 12.0, 7.0), Block.createCuboidShape(4.0, 3.0, 1.0, 12.0, 12.0, 9.0) };
        AGE_TO_SOUTH_SHAPE = new VoxelShape[] { Block.createCuboidShape(6.0, 7.0, 11.0, 10.0, 12.0, 15.0), Block.createCuboidShape(5.0, 5.0, 9.0, 11.0, 12.0, 15.0), Block.createCuboidShape(4.0, 3.0, 7.0, 12.0, 12.0, 15.0) };
    }
}
