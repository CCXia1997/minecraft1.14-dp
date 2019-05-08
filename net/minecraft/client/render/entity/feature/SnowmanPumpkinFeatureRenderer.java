package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.entity.passive.SnowmanEntity;

@Environment(EnvType.CLIENT)
public class SnowmanPumpkinFeatureRenderer extends FeatureRenderer<SnowmanEntity, SnowmanEntityModel<SnowmanEntity>>
{
    public SnowmanPumpkinFeatureRenderer(final FeatureRendererContext<SnowmanEntity, SnowmanEntityModel<SnowmanEntity>> context) {
        super(context);
    }
    
    @Override
    public void render(final SnowmanEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (entity.isInvisible() || !entity.hasPumpkin()) {
            return;
        }
        GlStateManager.pushMatrix();
        ((FeatureRenderer<T, SnowmanEntityModel>)this).getModel().a().applyTransform(0.0625f);
        final float float9 = 0.625f;
        GlStateManager.translatef(0.0f, -0.34375f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scalef(0.625f, -0.625f, -0.625f);
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(entity, new ItemStack(Blocks.cN), ModelTransformation.Type.f);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}
