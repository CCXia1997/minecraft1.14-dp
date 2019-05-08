package net.minecraft.client.render.entity;

import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.mob.PhantomEntity;

@Environment(EnvType.CLIENT)
public class PhantomEntityRenderer extends MobEntityRenderer<PhantomEntity, PhantomEntityModel<PhantomEntity>>
{
    private static final Identifier SKIN;
    
    public PhantomEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PhantomEntityModel(), 0.75f);
        this.addFeature((FeatureRenderer<PhantomEntity, PhantomEntityModel<PhantomEntity>>)new PhantomEyesFeatureRenderer((FeatureRendererContext<Entity, PhantomEntityModel<Entity>>)this));
    }
    
    protected Identifier getTexture(final PhantomEntity phantomEntity) {
        return PhantomEntityRenderer.SKIN;
    }
    
    @Override
    protected void scale(final PhantomEntity entity, final float tickDelta) {
        final int integer3 = entity.getPhantomSize();
        final float float4 = 1.0f + 0.15f * integer3;
        GlStateManager.scalef(float4, float4, float4);
        GlStateManager.translatef(0.0f, 1.3125f, 0.1875f);
    }
    
    @Override
    protected void setupTransforms(final PhantomEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
        GlStateManager.rotatef(entity.pitch, 1.0f, 0.0f, 0.0f);
    }
    
    static {
        SKIN = new Identifier("textures/entity/phantom.png");
    }
}
