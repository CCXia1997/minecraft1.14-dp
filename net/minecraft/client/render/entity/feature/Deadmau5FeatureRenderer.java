package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;

@Environment(EnvType.CLIENT)
public class Deadmau5FeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
{
    public Deadmau5FeatureRenderer(final FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }
    
    @Override
    public void render(final AbstractClientPlayerEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!"deadmau5".equals(entity.getName().getString()) || !entity.hasSkinTexture() || entity.isInvisible()) {
            return;
        }
        this.bindTexture(entity.getSkinTexture());
        for (int integer9 = 0; integer9 < 2; ++integer9) {
            final float float9 = MathHelper.lerp(float4, entity.prevYaw, entity.yaw) - MathHelper.lerp(float4, entity.aL, entity.aK);
            final float float10 = MathHelper.lerp(float4, entity.prevPitch, entity.pitch);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(float9, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(float10, 1.0f, 0.0f, 0.0f);
            GlStateManager.translatef(0.375f * (integer9 * 2 - 1), 0.0f, 0.0f);
            GlStateManager.translatef(0.0f, -0.375f, 0.0f);
            GlStateManager.rotatef(-float10, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(-float9, 0.0f, 1.0f, 0.0f);
            final float float11 = 1.3333334f;
            GlStateManager.scalef(1.3333334f, 1.3333334f, 1.3333334f);
            ((FeatureRenderer<T, PlayerEntityModel>)this).getModel().renderEars(0.0625f);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}
