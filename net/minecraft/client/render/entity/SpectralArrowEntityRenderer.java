package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.SpectralArrowEntity;

@Environment(EnvType.CLIENT)
public class SpectralArrowEntityRenderer extends ProjectileEntityRenderer<SpectralArrowEntity>
{
    public static final Identifier SKIN;
    
    public SpectralArrowEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    protected Identifier getTexture(final SpectralArrowEntity spectralArrowEntity) {
        return SpectralArrowEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/projectiles/spectral_arrow.png");
    }
}
