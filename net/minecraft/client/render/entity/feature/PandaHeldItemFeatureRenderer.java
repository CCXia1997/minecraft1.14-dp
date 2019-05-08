package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EquipmentSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.entity.passive.PandaEntity;

@Environment(EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer extends FeatureRenderer<PandaEntity, PandaEntityModel<PandaEntity>>
{
    public PandaHeldItemFeatureRenderer(final FeatureRendererContext<PandaEntity, PandaEntityModel<PandaEntity>> context) {
        super(context);
    }
    
    @Override
    public void render(final PandaEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack itemStack9 = entity.getEquippedStack(EquipmentSlot.HAND_MAIN);
        if (!entity.isScared() || itemStack9.isEmpty() || entity.eo()) {
            return;
        }
        float float9 = -0.6f;
        float float10 = 1.4f;
        if (entity.isEating()) {
            float9 -= 0.2f * MathHelper.sin(float5 * 0.6f) + 0.2f;
            float10 -= 0.09f * MathHelper.sin(float5 * 0.6f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.1f, float10, float9);
        MinecraftClient.getInstance().getItemRenderer().renderHeldItem(itemStack9, entity, ModelTransformation.Type.h, false);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
