package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.BlockRenderLayer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.entity.passive.DolphinEntity;

@Environment(EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer extends FeatureRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>>
{
    private final ItemRenderer a;
    
    public DolphinHeldItemFeatureRenderer(final FeatureRendererContext<DolphinEntity, DolphinEntityModel<DolphinEntity>> context) {
        super(context);
        this.a = MinecraftClient.getInstance().getItemRenderer();
    }
    
    @Override
    public void render(final DolphinEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final boolean boolean9 = entity.getMainHand() == AbsoluteHand.b;
        final ItemStack itemStack10 = boolean9 ? entity.getOffHandStack() : entity.getMainHandStack();
        final ItemStack itemStack11 = boolean9 ? entity.getMainHandStack() : entity.getOffHandStack();
        if (itemStack10.isEmpty() && itemStack11.isEmpty()) {
            return;
        }
        this.a(entity, itemStack11);
    }
    
    private void a(final LivingEntity livingEntity, final ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        final Item item3 = itemStack.getItem();
        final Block block4 = Block.getBlockFromItem(item3);
        GlStateManager.pushMatrix();
        final boolean boolean5 = this.a.hasDepthInGui(itemStack) && block4.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
        if (boolean5) {
            GlStateManager.depthMask(false);
        }
        final float float6 = 1.0f;
        final float float7 = -1.0f;
        final float float8 = MathHelper.abs(livingEntity.pitch) / 60.0f;
        if (livingEntity.pitch < 0.0f) {
            GlStateManager.translatef(0.0f, 1.0f - float8 * 0.5f, -1.0f + float8 * 0.5f);
        }
        else {
            GlStateManager.translatef(0.0f, 1.0f + float8 * 0.8f, -1.0f + float8 * 0.2f);
        }
        this.a.renderHeldItem(itemStack, livingEntity, ModelTransformation.Type.h, false);
        if (boolean5) {
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
