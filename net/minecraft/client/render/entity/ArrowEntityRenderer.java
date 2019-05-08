package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.ArrowEntity;

@Environment(EnvType.CLIENT)
public class ArrowEntityRenderer extends ProjectileEntityRenderer<ArrowEntity>
{
    public static final Identifier SKIN;
    public static final Identifier TIPPED_SKIN;
    
    public ArrowEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    protected Identifier getTexture(final ArrowEntity arrowEntity) {
        return (arrowEntity.getColor() > 0) ? ArrowEntityRenderer.TIPPED_SKIN : ArrowEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/projectiles/arrow.png");
        TIPPED_SKIN = new Identifier("textures/entity/projectiles/tipped_arrow.png");
    }
}
