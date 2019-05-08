package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.util.shape.VoxelShape;

public class StructureVoidBlock extends Block
{
    private static final VoxelShape SHAPE;
    
    protected StructureVoidBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.a;
    }
    
    @Override
    public VoxelShape getOutlineShape(final BlockState state, final BlockView view, final BlockPos pos, final VerticalEntityPosition verticalEntityPosition) {
        return StructureVoidBlock.SHAPE;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightLevel(final BlockState state, final BlockView view, final BlockPos pos) {
        return 1.0f;
    }
    
    @Override
    public PistonBehavior getPistonBehavior(final BlockState state) {
        return PistonBehavior.b;
    }
    
    static {
        SHAPE = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
    }
}
