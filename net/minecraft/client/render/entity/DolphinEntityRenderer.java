package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.entity.passive.DolphinEntity;

@Environment(EnvType.CLIENT)
public class DolphinEntityRenderer extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>>
{
    private static final Identifier SKIN;
    
    public DolphinEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new DolphinEntityModel(), 0.7f);
        this.addFeature(new DolphinHeldItemFeatureRenderer(this));
    }
    
    protected Identifier getTexture(final DolphinEntity dolphinEntity) {
        return DolphinEntityRenderer.SKIN;
    }
    
    @Override
    protected void scale(final DolphinEntity entity, final float tickDelta) {
        final float float3 = 1.0f;
        GlStateManager.scalef(1.0f, 1.0f, 1.0f);
    }
    
    @Override
    protected void setupTransforms(final DolphinEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
    }
    
    static {
        SKIN = new Identifier("textures/entity/dolphin.png");
    }
}
