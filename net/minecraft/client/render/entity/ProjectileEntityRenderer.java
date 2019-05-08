package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.ProjectileEntity;

@Environment(EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends ProjectileEntity> extends EntityRenderer<T>
{
    public ProjectileEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    public void render(final T entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        this.bindEntityTexture(entity);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.rotatef(MathHelper.lerp(tickDelta, entity.prevYaw, entity.yaw) - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch), 0.0f, 0.0f, 1.0f);
        final Tessellator tessellator10 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder11 = tessellator10.getBufferBuilder();
        final int integer12 = 0;
        final float float13 = 0.0f;
        final float float14 = 0.5f;
        final float float15 = 0.0f;
        final float float16 = 0.15625f;
        final float float17 = 0.0f;
        final float float18 = 0.15625f;
        final float float19 = 0.15625f;
        final float float20 = 0.3125f;
        final float float21 = 0.05625f;
        GlStateManager.enableRescaleNormal();
        final float float22 = entity.shake - tickDelta;
        if (float22 > 0.0f) {
            final float float23 = -MathHelper.sin(float22 * 3.0f) * float22;
            GlStateManager.rotatef(float23, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.rotatef(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scalef(0.05625f, 0.05625f, 0.05625f);
        GlStateManager.translatef(-4.0f, 0.0f, 0.0f);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        GlStateManager.normal3f(0.05625f, 0.0f, 0.0f);
        bufferBuilder11.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder11.vertex(-7.0, -2.0, -2.0).texture(0.0, 0.15625).next();
        bufferBuilder11.vertex(-7.0, -2.0, 2.0).texture(0.15625, 0.15625).next();
        bufferBuilder11.vertex(-7.0, 2.0, 2.0).texture(0.15625, 0.3125).next();
        bufferBuilder11.vertex(-7.0, 2.0, -2.0).texture(0.0, 0.3125).next();
        tessellator10.draw();
        GlStateManager.normal3f(-0.05625f, 0.0f, 0.0f);
        bufferBuilder11.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder11.vertex(-7.0, 2.0, -2.0).texture(0.0, 0.15625).next();
        bufferBuilder11.vertex(-7.0, 2.0, 2.0).texture(0.15625, 0.15625).next();
        bufferBuilder11.vertex(-7.0, -2.0, 2.0).texture(0.15625, 0.3125).next();
        bufferBuilder11.vertex(-7.0, -2.0, -2.0).texture(0.0, 0.3125).next();
        tessellator10.draw();
        for (int integer13 = 0; integer13 < 4; ++integer13) {
            GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.normal3f(0.0f, 0.0f, 0.05625f);
            bufferBuilder11.begin(7, VertexFormats.POSITION_UV);
            bufferBuilder11.vertex(-8.0, -2.0, 0.0).texture(0.0, 0.0).next();
            bufferBuilder11.vertex(8.0, -2.0, 0.0).texture(0.5, 0.0).next();
            bufferBuilder11.vertex(8.0, 2.0, 0.0).texture(0.5, 0.15625).next();
            bufferBuilder11.vertex(-8.0, 2.0, 0.0).texture(0.0, 0.15625).next();
            tessellator10.draw();
        }
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
}
