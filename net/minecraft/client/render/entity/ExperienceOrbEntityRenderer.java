package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ExperienceOrbEntity;

@Environment(EnvType.CLIENT)
public class ExperienceOrbEntityRenderer extends EntityRenderer<ExperienceOrbEntity>
{
    private static final Identifier SKIN;
    
    public ExperienceOrbEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.c = 0.15f;
        this.d = 0.75f;
    }
    
    @Override
    public void render(final ExperienceOrbEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        if (this.e) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        this.bindEntityTexture(entity);
        GuiLighting.enable();
        final int integer10 = entity.getOrbSize();
        final float float11 = (integer10 % 4 * 16 + 0) / 64.0f;
        final float float12 = (integer10 % 4 * 16 + 16) / 64.0f;
        final float float13 = (integer10 / 4 * 16 + 0) / 64.0f;
        final float float14 = (integer10 / 4 * 16 + 16) / 64.0f;
        final float float15 = 1.0f;
        final float float16 = 0.5f;
        final float float17 = 0.25f;
        final int integer11 = entity.getLightmapCoordinates();
        final int integer12 = integer11 % 65536;
        final int integer13 = integer11 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer12, (float)integer13);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final float float18 = 255.0f;
        final float float19 = (entity.renderTicks + tickDelta) / 2.0f;
        final int integer14 = (int)((MathHelper.sin(float19 + 0.0f) + 1.0f) * 0.5f * 255.0f);
        final int integer15 = 255;
        final int integer16 = (int)((MathHelper.sin(float19 + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        GlStateManager.translatef(0.0f, 0.1f, 0.0f);
        GlStateManager.rotatef(180.0f - this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.renderManager.gameOptions.perspective == 2) ? -1 : 1) * -this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        final float float20 = 0.3f;
        GlStateManager.scalef(0.3f, 0.3f, 0.3f);
        final Tessellator tessellator27 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder28 = tessellator27.getBufferBuilder();
        bufferBuilder28.begin(7, VertexFormats.POSITION_UV_COLOR_NORMAL);
        bufferBuilder28.vertex(-0.5, -0.25, 0.0).texture(float11, float14).color(integer14, 255, integer16, 128).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder28.vertex(0.5, -0.25, 0.0).texture(float12, float14).color(integer14, 255, integer16, 128).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder28.vertex(0.5, 0.75, 0.0).texture(float12, float13).color(integer14, 255, integer16, 128).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder28.vertex(-0.5, 0.75, 0.0).texture(float11, float13).color(integer14, 255, integer16, 128).normal(0.0f, 1.0f, 0.0f).next();
        tessellator27.draw();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final ExperienceOrbEntity experienceOrbEntity) {
        return ExperienceOrbEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/experience_orb.png");
    }
}
