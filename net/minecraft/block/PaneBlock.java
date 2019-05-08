package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.StateFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;

public class PaneBlock extends HorizontalConnectedBlock
{
    protected PaneBlock(final Settings settings) {
        super(1.0f, 1.0f, 16.0f, 16.0f, 16.0f, settings);
        this.setDefaultState(((((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)PaneBlock.NORTH, false)).with((Property<Comparable>)PaneBlock.EAST, false)).with((Property<Comparable>)PaneBlock.SOUTH, false)).with((Property<Comparable>)PaneBlock.WEST, false)).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.WATERLOGGED, false));
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        final BlockView blockView2 = ctx.getWorld();
        final BlockPos blockPos3 = ctx.getBlockPos();
        final FluidState fluidState4 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        final BlockPos blockPos4 = blockPos3.north();
        final BlockPos blockPos5 = blockPos3.south();
        final BlockPos blockPos6 = blockPos3.west();
        final BlockPos blockPos7 = blockPos3.east();
        final BlockState blockState9 = blockView2.getBlockState(blockPos4);
        final BlockState blockState10 = blockView2.getBlockState(blockPos5);
        final BlockState blockState11 = blockView2.getBlockState(blockPos6);
        final BlockState blockState12 = blockView2.getBlockState(blockPos7);
        return ((((((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).with((Property<Comparable>)PaneBlock.NORTH, this.connectsTo(blockState9, Block.isSolidFullSquare(blockState9, blockView2, blockPos4, Direction.SOUTH)))).with((Property<Comparable>)PaneBlock.SOUTH, this.connectsTo(blockState10, Block.isSolidFullSquare(blockState10, blockView2, blockPos5, Direction.NORTH)))).with((Property<Comparable>)PaneBlock.WEST, this.connectsTo(blockState11, Block.isSolidFullSquare(blockState11, blockView2, blockPos6, Direction.EAST)))).with((Property<Comparable>)PaneBlock.EAST, this.connectsTo(blockState12, Block.isSolidFullSquare(blockState12, blockView2, blockPos7, Direction.WEST)))).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.WATERLOGGED, fluidState4.getFluid() == Fluids.WATER);
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (state.<Boolean>get((Property<Boolean>)PaneBlock.WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (facing.getAxis().isHorizontal()) {
            return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Boolean>with((Property<Comparable>)PaneBlock.FACING_PROPERTIES.get(facing), this.connectsTo(neighborState, Block.isSolidFullSquare(neighborState, world, neighborPos, facing.getOpposite())));
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean skipRenderingSide(final BlockState state, final BlockState neighbor, final Direction facing) {
        if (neighbor.getBlock() == this) {
            if (!facing.getAxis().isHorizontal()) {
                return true;
            }
            if (state.<Boolean>get((Property<Boolean>)PaneBlock.FACING_PROPERTIES.get(facing)) && neighbor.<Boolean>get((Property<Boolean>)PaneBlock.FACING_PROPERTIES.get(facing.getOpposite()))) {
                return true;
            }
        }
        return super.skipRenderingSide(state, neighbor, facing);
    }
    
    public final boolean connectsTo(final BlockState blockState, final boolean boolean2) {
        final Block block3 = blockState.getBlock();
        return (!Block.canConnect(block3) && boolean2) || block3 instanceof PaneBlock;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.MIPPED_CUTOUT;
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(PaneBlock.NORTH, PaneBlock.EAST, PaneBlock.WEST, PaneBlock.SOUTH, PaneBlock.WATERLOGGED);
    }
}
