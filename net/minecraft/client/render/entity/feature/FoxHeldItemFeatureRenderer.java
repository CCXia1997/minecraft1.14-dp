package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.EquipmentSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.FoxModel;
import net.minecraft.entity.passive.FoxEntity;

@Environment(EnvType.CLIENT)
public class FoxHeldItemFeatureRenderer extends FeatureRenderer<FoxEntity, FoxModel<FoxEntity>>
{
    public FoxHeldItemFeatureRenderer(final FeatureRendererContext<FoxEntity, FoxModel<FoxEntity>> context) {
        super(context);
    }
    
    @Override
    public void render(final FoxEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack itemStack9 = entity.getEquippedStack(EquipmentSlot.HAND_MAIN);
        if (itemStack9.isEmpty()) {
            return;
        }
        final boolean boolean10 = entity.isSleeping();
        final boolean boolean11 = entity.isChild();
        GlStateManager.pushMatrix();
        if (boolean11) {
            final float float9 = 0.75f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 8.0f * float8, 3.35f * float8);
        }
        GlStateManager.translatef(((FeatureRenderer<T, FoxModel>)this).getModel().head.rotationPointX / 16.0f, ((FeatureRenderer<T, FoxModel>)this).getModel().head.rotationPointY / 16.0f, ((FeatureRenderer<T, FoxModel>)this).getModel().head.rotationPointZ / 16.0f);
        final float float9 = entity.getHeadRoll(float4) * 57.295776f;
        GlStateManager.rotatef(float9, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotatef(float6, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(float7, 1.0f, 0.0f, 0.0f);
        if (entity.isChild()) {
            if (boolean10) {
                GlStateManager.translatef(0.4f, 0.26f, 0.15f);
            }
            else {
                GlStateManager.translatef(0.06f, 0.26f, -0.5f);
            }
        }
        else if (boolean10) {
            GlStateManager.translatef(0.46f, 0.26f, 0.22f);
        }
        else {
            GlStateManager.translatef(0.06f, 0.27f, -0.5f);
        }
        GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        if (boolean10) {
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
        MinecraftClient.getInstance().getItemRenderer().renderHeldItem(itemStack9, entity, ModelTransformation.Type.h, false);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
