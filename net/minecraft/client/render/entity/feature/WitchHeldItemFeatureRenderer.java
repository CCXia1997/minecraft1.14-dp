package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.Block;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, WitchEntityModel<T>>
{
    public WitchHeldItemFeatureRenderer(final FeatureRendererContext<T, WitchEntityModel<T>> context) {
        super(context);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack itemStack9 = entity.getMainHandStack();
        if (itemStack9.isEmpty()) {
            return;
        }
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        if (this.getModel().isChild) {
            GlStateManager.translatef(0.0f, 0.625f, 0.0f);
            GlStateManager.rotatef(-20.0f, -1.0f, 0.0f, 0.0f);
            final float float9 = 0.5f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
        }
        this.getModel().b().applyTransform(0.0625f);
        GlStateManager.translatef(-0.0625f, 0.53125f, 0.21875f);
        final Item item10 = itemStack9.getItem();
        if (Block.getBlockFromItem(item10).getDefaultState().getRenderType() == BlockRenderType.b) {
            GlStateManager.translatef(0.0f, 0.0625f, -0.25f);
            GlStateManager.rotatef(30.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(-5.0f, 0.0f, 1.0f, 0.0f);
            final float float10 = 0.375f;
            GlStateManager.scalef(0.375f, -0.375f, 0.375f);
        }
        else if (item10 == Items.jf) {
            GlStateManager.translatef(0.0f, 0.125f, -0.125f);
            GlStateManager.rotatef(-45.0f, 0.0f, 1.0f, 0.0f);
            final float float10 = 0.625f;
            GlStateManager.scalef(0.625f, -0.625f, 0.625f);
            GlStateManager.rotatef(-100.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(-20.0f, 0.0f, 1.0f, 0.0f);
        }
        else {
            GlStateManager.translatef(0.1875f, 0.1875f, 0.0f);
            final float float10 = 0.875f;
            GlStateManager.scalef(0.875f, 0.875f, 0.875f);
            GlStateManager.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(-60.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(-30.0f, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.rotatef(-15.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(40.0f, 0.0f, 0.0f, 1.0f);
        MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(entity, itemStack9, ModelTransformation.Type.c);
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
