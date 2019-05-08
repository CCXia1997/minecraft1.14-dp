package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(EnvType.CLIENT)
public class IronGolemFlowerFeatureRenderer extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>>
{
    public IronGolemFlowerFeatureRenderer(final FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> context) {
        super(context);
    }
    
    @Override
    public void render(final IronGolemEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (entity.dV() == 0) {
            return;
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.rotatef(5.0f + 180.0f * ((FeatureRenderer<T, IronGolemEntityModel>)this).getModel().a().pitch / 3.1415927f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translatef(-0.9375f, -0.625f, -0.9375f);
        final float float9 = 0.5f;
        GlStateManager.scalef(0.5f, -0.5f, 0.5f);
        final int integer10 = entity.getLightmapCoordinates();
        final int integer11 = integer10 % 65536;
        final int integer12 = integer10 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer11, (float)integer12);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(Blocks.bp.getDefaultState(), 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
