package net.minecraft.client.render.entity;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.TNTMinecartEntity;

@Environment(EnvType.CLIENT)
public class MinecartTNTEntityRenderer extends MinecartEntityRenderer<TNTMinecartEntity>
{
    public MinecartTNTEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    protected void renderBlock(final TNTMinecartEntity entity, final float delta, final BlockState state) {
        final int integer4 = entity.getFuseTicks();
        if (integer4 > -1 && integer4 - delta + 1.0f < 10.0f) {
            float float5 = 1.0f - (integer4 - delta + 1.0f) / 10.0f;
            float5 = MathHelper.clamp(float5, 0.0f, 1.0f);
            float5 *= float5;
            float5 *= float5;
            final float float6 = 1.0f + float5 * 0.3f;
            GlStateManager.scalef(float6, float6, float6);
        }
        super.renderBlock(entity, delta, state);
        if (integer4 > -1 && integer4 / 5 % 2 == 0) {
            final BlockRenderManager blockRenderManager5 = MinecraftClient.getInstance().getBlockRenderManager();
            GlStateManager.disableTexture();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, (1.0f - (integer4 - delta + 1.0f) / 100.0f) * 0.8f);
            GlStateManager.pushMatrix();
            blockRenderManager5.renderDynamic(Blocks.bG.getDefaultState(), 1.0f);
            GlStateManager.popMatrix();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture();
        }
    }
}
