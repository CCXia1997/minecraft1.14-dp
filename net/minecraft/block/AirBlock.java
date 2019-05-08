package net.minecraft.block;

import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class AirBlock extends Block
{
    protected AirBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.a;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return VoxelShapes.empty();
    }
    
    @Override
    public boolean isAir(final BlockState state) {
        return true;
    }
}
