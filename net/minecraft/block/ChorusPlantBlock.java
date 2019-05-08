package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;

public class ChorusPlantBlock extends ConnectedPlantBlock
{
    protected ChorusPlantBlock(final Settings settings) {
        super(0.3125f, settings);
        this.setDefaultState((((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)ChorusPlantBlock.NORTH, false)).with((Property<Comparable>)ChorusPlantBlock.EAST, false)).with((Property<Comparable>)ChorusPlantBlock.SOUTH, false)).with((Property<Comparable>)ChorusPlantBlock.WEST, false)).with((Property<Comparable>)ChorusPlantBlock.UP, false)).<Comparable, Boolean>with((Property<Comparable>)ChorusPlantBlock.DOWN, false));
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
    }
    
    public BlockState withConnectionProperties(final BlockView blockView, final BlockPos blockPos) {
        final Block block3 = blockView.getBlockState(blockPos.down()).getBlock();
        final Block block4 = blockView.getBlockState(blockPos.up()).getBlock();
        final Block block5 = blockView.getBlockState(blockPos.north()).getBlock();
        final Block block6 = blockView.getBlockState(blockPos.east()).getBlock();
        final Block block7 = blockView.getBlockState(blockPos.south()).getBlock();
        final Block block8 = blockView.getBlockState(blockPos.west()).getBlock();
        return (((((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)ChorusPlantBlock.DOWN, block3 == this || block3 == Blocks.iq || block3 == Blocks.dW)).with((Property<Comparable>)ChorusPlantBlock.UP, block4 == this || block4 == Blocks.iq)).with((Property<Comparable>)ChorusPlantBlock.NORTH, block5 == this || block5 == Blocks.iq)).with((Property<Comparable>)ChorusPlantBlock.EAST, block6 == this || block6 == Blocks.iq)).with((Property<Comparable>)ChorusPlantBlock.SOUTH, block7 == this || block7 == Blocks.iq)).<Comparable, Boolean>with((Property<Comparable>)ChorusPlantBlock.WEST, block8 == this || block8 == Blocks.iq);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.getBlockTickScheduler().schedule(pos, this, 1);
            return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        }
        final Block block7 = neighborState.getBlock();
        final boolean boolean8 = block7 == this || block7 == Blocks.iq || (facing == Direction.DOWN && block7 == Blocks.dW);
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)ChorusPlantBlock.FACING_PROPERTIES.get(facing), boolean8);
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockState blockState4 = world.getBlockState(pos.down());
        final boolean boolean5 = !world.getBlockState(pos.up()).isAir() && !blockState4.isAir();
        for (final Direction direction7 : Direction.Type.HORIZONTAL) {
            final BlockPos blockPos8 = pos.offset(direction7);
            final Block block9 = world.getBlockState(blockPos8).getBlock();
            if (block9 == this) {
                if (boolean5) {
                    return false;
                }
                final Block block10 = world.getBlockState(blockPos8.down()).getBlock();
                if (block10 == this || block10 == Blocks.dW) {
                    return true;
                }
                continue;
            }
        }
        final Block block11 = blockState4.getBlock();
        return block11 == this || block11 == Blocks.dW;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(ChorusPlantBlock.NORTH, ChorusPlantBlock.EAST, ChorusPlantBlock.SOUTH, ChorusPlantBlock.WEST, ChorusPlantBlock.UP, ChorusPlantBlock.DOWN);
    }
    
    @Override
    public boolean canPlaceAtSide(final BlockState world, final BlockView view, final BlockPos pos, final BlockPlacementEnvironment env) {
        return false;
    }
}
