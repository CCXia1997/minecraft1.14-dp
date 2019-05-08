package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public abstract class AbstractGlassBlock extends TransparentBlock
{
    protected AbstractGlassBlock(final Settings settings) {
        super(settings);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightLevel(final BlockState state, final BlockView view, final BlockPos pos) {
        return 1.0f;
    }
    
    @Override
    public boolean isTranslucent(final BlockState state, final BlockView view, final BlockPos pos) {
        return true;
    }
    
    @Override
    public boolean canSuffocate(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Override
    public boolean isSimpleFullBlock(final BlockState state, final BlockView view, final BlockPos pos) {
        return false;
    }
    
    @Override
    public boolean allowsSpawning(final BlockState state, final BlockView blockView, final BlockPos blockPos, final EntityType<?> entityType) {
        return false;
    }
}
