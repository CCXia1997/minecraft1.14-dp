package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.mob.EndermanEntity;

@Environment(EnvType.CLIENT)
public class EndermanBlockFeatureRenderer extends FeatureRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>>
{
    public EndermanBlockFeatureRenderer(final FeatureRendererContext<EndermanEntity, EndermanEntityModel<EndermanEntity>> context) {
        super(context);
    }
    
    @Override
    public void render(final EndermanEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final BlockState blockState9 = entity.getCarriedBlock();
        if (blockState9 == null) {
            return;
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.6875f, -0.75f);
        GlStateManager.rotatef(20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(45.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.25f, 0.1875f, 0.25f);
        final float float9 = 0.5f;
        GlStateManager.scalef(-0.5f, -0.5f, 0.5f);
        final int integer11 = entity.getLightmapCoordinates();
        final int integer12 = integer11 % 65536;
        final int integer13 = integer11 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer12, (float)integer13);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState9, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}
