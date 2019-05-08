package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BarrierBlock extends Block
{
    protected BarrierBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean isTranslucent(final BlockState state, final BlockView view, final BlockPos pos) {
        return true;
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.a;
    }
    
    @Override
    public boolean isFullBoundsCubeForCulling(final BlockState state) {
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightLevel(final BlockState state, final BlockView view, final BlockPos pos) {
        return 1.0f;
    }
    
    @Override
    public boolean allowsSpawning(final BlockState state, final BlockView blockView, final BlockPos blockPos, final EntityType<?> entityType) {
        return false;
    }
}
