package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.block.BlockRenderLayer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, VillagerResemblingModel<T>>
{
    private final ItemRenderer itemRenderer;
    
    public VillagerHeldItemFeatureRenderer(final FeatureRendererContext<T, VillagerResemblingModel<T>> context) {
        super(context);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final ItemStack itemStack9 = entity.getEquippedStack(EquipmentSlot.HAND_MAIN);
        if (itemStack9.isEmpty()) {
            return;
        }
        final Item item10 = itemStack9.getItem();
        final Block block11 = Block.getBlockFromItem(item10);
        GlStateManager.pushMatrix();
        final boolean boolean12 = this.itemRenderer.hasDepthInGui(itemStack9) && block11.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
        if (boolean12) {
            GlStateManager.depthMask(false);
        }
        GlStateManager.translatef(0.0f, 0.4f, -0.4f);
        GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        this.itemRenderer.renderHeldItem(itemStack9, entity, ModelTransformation.Type.h, false);
        if (boolean12) {
            GlStateManager.depthMask(true);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
