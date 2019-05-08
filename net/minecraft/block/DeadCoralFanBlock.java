package net.minecraft.block;

import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class DeadCoralFanBlock extends CoralParentBlock
{
    private static final VoxelShape SHAPE;
    
    protected DeadCoralFanBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return DeadCoralFanBlock.SHAPE;
    }
    
    static {
        SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    }
}
