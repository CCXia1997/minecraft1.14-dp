package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class DeadBushBlock extends PlantBlock
{
    protected static final VoxelShape SHAPE;
    
    protected DeadBushBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return DeadBushBlock.SHAPE;
    }
    
    @Override
    protected boolean canPlantOnTop(final BlockState floor, final BlockView view, final BlockPos pos) {
        final Block block4 = floor.getBlock();
        return block4 == Blocks.C || block4 == Blocks.D || block4 == Blocks.gJ || block4 == Blocks.fx || block4 == Blocks.fy || block4 == Blocks.fz || block4 == Blocks.fA || block4 == Blocks.fB || block4 == Blocks.fC || block4 == Blocks.fD || block4 == Blocks.fE || block4 == Blocks.fF || block4 == Blocks.fG || block4 == Blocks.fH || block4 == Blocks.fI || block4 == Blocks.fJ || block4 == Blocks.fK || block4 == Blocks.fL || block4 == Blocks.fM || block4 == Blocks.j || block4 == Blocks.k || block4 == Blocks.l;
    }
    
    static {
        SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);
    }
}
