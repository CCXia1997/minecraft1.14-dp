package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.MooshroomEntity;

@Environment(EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity> extends FeatureRenderer<T, CowEntityModel<T>>
{
    public MooshroomMushroomFeatureRenderer(final FeatureRendererContext<T, CowEntityModel<T>> context) {
        super(context);
    }
    
    @Override
    public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (entity.isChild() || entity.isInvisible()) {
            return;
        }
        final BlockState blockState9 = entity.getMooshroomType().getMushroomState();
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.FaceSides.a);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(1.0f, -1.0f, 1.0f);
        GlStateManager.translatef(0.2f, 0.35f, 0.5f);
        GlStateManager.rotatef(42.0f, 0.0f, 1.0f, 0.0f);
        final BlockRenderManager blockRenderManager10 = MinecraftClient.getInstance().getBlockRenderManager();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager10.renderDynamic(blockState9, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.1f, 0.0f, -0.6f);
        GlStateManager.rotatef(42.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager10.renderDynamic(blockState9, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.getModel().a().applyTransform(0.0625f);
        GlStateManager.scalef(1.0f, -1.0f, 1.0f);
        GlStateManager.translatef(0.0f, 0.7f, -0.2f);
        GlStateManager.rotatef(12.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager10.renderDynamic(blockState9, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.cullFace(GlStateManager.FaceSides.b);
        GlStateManager.disableCull();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}
