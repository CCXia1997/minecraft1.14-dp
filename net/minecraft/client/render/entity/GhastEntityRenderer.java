package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.entity.mob.GhastEntity;

@Environment(EnvType.CLIENT)
public class GhastEntityRenderer extends MobEntityRenderer<GhastEntity, GhastEntityModel<GhastEntity>>
{
    private static final Identifier SKIN;
    private static final Identifier ANGRY_SKIN;
    
    public GhastEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new GhastEntityModel(), 1.5f);
    }
    
    protected Identifier getTexture(final GhastEntity ghastEntity) {
        if (ghastEntity.isShooting()) {
            return GhastEntityRenderer.ANGRY_SKIN;
        }
        return GhastEntityRenderer.SKIN;
    }
    
    @Override
    protected void scale(final GhastEntity entity, final float tickDelta) {
        final float float3 = 1.0f;
        final float float4 = 4.5f;
        final float float5 = 4.5f;
        GlStateManager.scalef(4.5f, 4.5f, 4.5f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    static {
        SKIN = new Identifier("textures/entity/ghast/ghast.png");
        ANGRY_SKIN = new Identifier("textures/entity/ghast/ghast_shooting.png");
    }
}
