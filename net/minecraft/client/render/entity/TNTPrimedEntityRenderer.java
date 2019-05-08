package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.PrimedTntEntity;

@Environment(EnvType.CLIENT)
public class TNTPrimedEntityRenderer extends EntityRenderer<PrimedTntEntity>
{
    public TNTPrimedEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.c = 0.5f;
    }
    
    @Override
    public void render(final PrimedTntEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        final BlockRenderManager blockRenderManager10 = MinecraftClient.getInstance().getBlockRenderManager();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y + 0.5f, (float)z);
        if (entity.getFuseTimer() - tickDelta + 1.0f < 10.0f) {
            float float11 = 1.0f - (entity.getFuseTimer() - tickDelta + 1.0f) / 10.0f;
            float11 = MathHelper.clamp(float11, 0.0f, 1.0f);
            float11 *= float11;
            float11 *= float11;
            final float float12 = 1.0f + float11 * 0.3f;
            GlStateManager.scalef(float12, float12, float12);
        }
        float float11 = (1.0f - (entity.getFuseTimer() - tickDelta + 1.0f) / 100.0f) * 0.8f;
        this.bindEntityTexture(entity);
        GlStateManager.rotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(-0.5f, -0.5f, 0.5f);
        blockRenderManager10.renderDynamic(Blocks.bG.getDefaultState(), entity.getBrightnessAtEyes());
        GlStateManager.translatef(0.0f, 0.0f, 1.0f);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
            blockRenderManager10.renderDynamic(Blocks.bG.getDefaultState(), 1.0f);
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        else if (entity.getFuseTimer() / 5 % 2 == 0) {
            GlStateManager.disableTexture();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, float11);
            GlStateManager.polygonOffset(-3.0f, -3.0f);
            GlStateManager.enablePolygonOffset();
            blockRenderManager10.renderDynamic(Blocks.bG.getDefaultState(), 1.0f);
            GlStateManager.polygonOffset(0.0f, 0.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture();
        }
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final PrimedTntEntity primedTntEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}
