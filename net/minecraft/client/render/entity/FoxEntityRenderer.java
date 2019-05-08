package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.FoxModel;
import net.minecraft.entity.passive.FoxEntity;

@Environment(EnvType.CLIENT)
public class FoxEntityRenderer extends MobEntityRenderer<FoxEntity, FoxModel<FoxEntity>>
{
    private static final Identifier SKIN;
    private static final Identifier SKIN_SLEEP;
    private static final Identifier SKIN_SNOW;
    private static final Identifier SKIN_SNOW_SLEEp;
    
    public FoxEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new FoxModel(), 0.4f);
        this.addFeature(new FoxHeldItemFeatureRenderer(this));
    }
    
    @Override
    protected void setupTransforms(final FoxEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
        if (entity.isChasing() || entity.isWalking()) {
            GlStateManager.rotatef(-MathHelper.lerp(delta, entity.prevPitch, entity.pitch), 1.0f, 0.0f, 0.0f);
        }
    }
    
    @Nullable
    protected Identifier getTexture(final FoxEntity foxEntity) {
        if (foxEntity.getFoxType() == FoxEntity.Type.a) {
            return foxEntity.isSleeping() ? FoxEntityRenderer.SKIN_SLEEP : FoxEntityRenderer.SKIN;
        }
        return foxEntity.isSleeping() ? FoxEntityRenderer.SKIN_SNOW_SLEEp : FoxEntityRenderer.SKIN_SNOW;
    }
    
    static {
        SKIN = new Identifier("textures/entity/fox/fox.png");
        SKIN_SLEEP = new Identifier("textures/entity/fox/fox_sleep.png");
        SKIN_SNOW = new Identifier("textures/entity/fox/snow_fox.png");
        SKIN_SNOW_SLEEp = new Identifier("textures/entity/fox/snow_fox_sleep.png");
    }
}
