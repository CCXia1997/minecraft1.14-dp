package net.minecraft.block;

import net.minecraft.world.ViewableWorld;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class PlantBlock extends Block
{
    protected PlantBlock(final Settings settings) {
        super(settings);
    }
    
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        final Block block4 = floor.getBlock();
        return block4 == Blocks.i || block4 == Blocks.j || block4 == Blocks.k || block4 == Blocks.l || block4 == Blocks.bV;
    }
    
    @Override
    public BlockState getStateForNeighborUpdate(final BlockState state, final Direction facing, final BlockState neighborState, final IWorld world, final BlockPos pos, final BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }
    
    @Override
    public boolean canPlaceAt(final BlockState state, final ViewableWorld world, final BlockPos pos) {
        final BlockPos blockPos4 = pos.down();
        return this.canPlantOnTop(world.getBlockState(blockPos4), world, blockPos4);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public boolean isTranslucent(final BlockState state, final BlockView view, final BlockPos pos) {
        return true;
    }
}
