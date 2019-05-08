package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.entity.mob.WitchEntity;

@Environment(EnvType.CLIENT)
public class WitchEntityRenderer extends MobEntityRenderer<WitchEntity, WitchEntityModel<WitchEntity>>
{
    private static final Identifier SKIN;
    
    public WitchEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WitchEntityModel(0.0f), 0.5f);
        this.addFeature((FeatureRenderer<WitchEntity, WitchEntityModel<WitchEntity>>)new WitchHeldItemFeatureRenderer((FeatureRendererContext<LivingEntity, WitchEntityModel<LivingEntity>>)this));
    }
    
    @Override
    public void render(final WitchEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        ((WitchEntityModel)this.model).b(!entity.getMainHandStack().isEmpty());
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    protected Identifier getTexture(final WitchEntity witchEntity) {
        return WitchEntityRenderer.SKIN;
    }
    
    @Override
    protected void scale(final WitchEntity entity, final float tickDelta) {
        final float float3 = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
    
    static {
        SKIN = new Identifier("textures/entity/witch.png");
    }
}
