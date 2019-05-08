package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.Items;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;

@Environment(EnvType.CLIENT)
public class CapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
{
    public CapeFeatureRenderer(final FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }
    
    @Override
    public void render(final AbstractClientPlayerEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (!entity.canRenderCapeTexture() || entity.isInvisible() || !entity.isSkinOverlayVisible(PlayerModelPart.CAPE) || entity.getCapeTexture() == null) {
            return;
        }
        final ItemStack itemStack9 = entity.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack9.getItem() == Items.oX) {
            return;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(entity.getCapeTexture());
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 0.125f);
        final double double10 = MathHelper.lerp(float4, entity.bG, entity.bJ) - MathHelper.lerp(float4, entity.prevX, entity.x);
        final double double11 = MathHelper.lerp(float4, entity.bH, entity.bK) - MathHelper.lerp(float4, entity.prevY, entity.y);
        final double double12 = MathHelper.lerp(float4, entity.bI, entity.bL) - MathHelper.lerp(float4, entity.prevZ, entity.z);
        final float float9 = entity.aL + (entity.aK - entity.aL);
        final double double13 = MathHelper.sin(float9 * 0.017453292f);
        final double double14 = -MathHelper.cos(float9 * 0.017453292f);
        float float10 = (float)double11 * 10.0f;
        float10 = MathHelper.clamp(float10, -6.0f, 32.0f);
        float float11 = (float)(double10 * double13 + double12 * double14) * 100.0f;
        float11 = MathHelper.clamp(float11, 0.0f, 150.0f);
        float float12 = (float)(double10 * double14 - double12 * double13) * 100.0f;
        float12 = MathHelper.clamp(float12, -20.0f, 20.0f);
        if (float11 < 0.0f) {
            float11 = 0.0f;
        }
        final float float13 = MathHelper.lerp(float4, entity.bD, entity.bE);
        float10 += MathHelper.sin(MathHelper.lerp(float4, entity.D, entity.E) * 6.0f) * 32.0f * float13;
        if (entity.isInSneakingPose()) {
            float10 += 25.0f;
        }
        GlStateManager.rotatef(6.0f + float11 / 2.0f + float10, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(float12 / 2.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(-float12 / 2.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        ((FeatureRenderer<T, PlayerEntityModel>)this).getModel().renderCape(0.0625f);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
