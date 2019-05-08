package net.minecraft.client.render.entity;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.TridentEntity;

@Environment(EnvType.CLIENT)
public class TridentEntityRenderer extends EntityRenderer<TridentEntity>
{
    public static final Identifier SKIN;
    private final TridentEntityModel model;
    
    public TridentEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.model = new TridentEntityModel();
    }
    
    @Override
    public void render(final TridentEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        this.bindEntityTexture(entity);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.rotatef(MathHelper.lerp(tickDelta, entity.prevYaw, entity.yaw) - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch) + 90.0f, 0.0f, 0.0f, 1.0f);
        this.model.renderItem();
        GlStateManager.popMatrix();
        this.b(entity, x, y, z, yaw, tickDelta);
        super.render(entity, x, y, z, yaw, tickDelta);
        GlStateManager.enableLighting();
    }
    
    @Override
    protected Identifier getTexture(final TridentEntity tridentEntity) {
        return TridentEntityRenderer.SKIN;
    }
    
    protected void b(final TridentEntity tridentEntity, final double double2, final double double4, final double double6, final float float8, final float float9) {
        final Entity entity10 = tridentEntity.getOwner();
        if (entity10 == null || !tridentEntity.isNoClip()) {
            return;
        }
        final Tessellator tessellator11 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder12 = tessellator11.getBufferBuilder();
        final double double7 = MathHelper.lerp(float9 * 0.5f, entity10.yaw, entity10.prevYaw) * 0.017453292f;
        final double double8 = Math.cos(double7);
        final double double9 = Math.sin(double7);
        final double double10 = MathHelper.lerp(float9, entity10.prevX, entity10.x);
        final double double11 = MathHelper.lerp(float9, entity10.prevY + entity10.getStandingEyeHeight() * 0.8, entity10.y + entity10.getStandingEyeHeight() * 0.8);
        final double double12 = MathHelper.lerp(float9, entity10.prevZ, entity10.z);
        final double double13 = double8 - double9;
        final double double14 = double9 + double8;
        final double double15 = MathHelper.lerp(float9, tridentEntity.prevX, tridentEntity.x);
        final double double16 = MathHelper.lerp(float9, tridentEntity.prevY, tridentEntity.y);
        final double double17 = MathHelper.lerp(float9, tridentEntity.prevZ, tridentEntity.z);
        final double double18 = (float)(double10 - double15);
        final double double19 = (float)(double11 - double16);
        final double double20 = (float)(double12 - double17);
        final double double21 = Math.sqrt(double18 * double18 + double19 * double19 + double20 * double20);
        final int integer43 = tridentEntity.getEntityId() + tridentEntity.age;
        final double double22 = (integer43 + float9) * -0.1;
        final double double23 = Math.min(0.5, double21 / 30.0);
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 255.0f, 255.0f);
        bufferBuilder12.begin(5, VertexFormats.POSITION_COLOR);
        final int integer44 = 37;
        final int integer45 = 7 - integer43 % 7;
        final double double24 = 0.1;
        for (int integer46 = 0; integer46 <= 37; ++integer46) {
            final double double25 = integer46 / 37.0;
            final float float10 = 1.0f - (integer46 + integer45) % 7 / 7.0f;
            double double26 = double25 * 2.0 - 1.0;
            double26 = (1.0 - double26 * double26) * double23;
            final double double27 = double2 + double18 * double25 + Math.sin(double25 * 3.141592653589793 * 8.0 + double22) * double13 * double26;
            final double double28 = double4 + double19 * double25 + Math.cos(double25 * 3.141592653589793 * 8.0 + double22) * 0.02 + (0.1 + double26) * 1.0;
            final double double29 = double6 + double20 * double25 + Math.sin(double25 * 3.141592653589793 * 8.0 + double22) * double14 * double26;
            final float float11 = 0.87f * float10 + 0.3f * (1.0f - float10);
            final float float12 = 0.91f * float10 + 0.6f * (1.0f - float10);
            final float float13 = 0.85f * float10 + 0.5f * (1.0f - float10);
            bufferBuilder12.vertex(double27, double28, double29).color(float11, float12, float13, 1.0f).next();
            bufferBuilder12.vertex(double27 + 0.1 * double26, double28 + 0.1 * double26, double29).color(float11, float12, float13, 1.0f).next();
            if (integer46 > tridentEntity.ar * 2) {
                break;
            }
        }
        tessellator11.draw();
        bufferBuilder12.begin(5, VertexFormats.POSITION_COLOR);
        for (int integer46 = 0; integer46 <= 37; ++integer46) {
            final double double25 = integer46 / 37.0;
            final float float10 = 1.0f - (integer46 + integer45) % 7 / 7.0f;
            double double26 = double25 * 2.0 - 1.0;
            double26 = (1.0 - double26 * double26) * double23;
            final double double27 = double2 + double18 * double25 + Math.sin(double25 * 3.141592653589793 * 8.0 + double22) * double13 * double26;
            final double double28 = double4 + double19 * double25 + Math.cos(double25 * 3.141592653589793 * 8.0 + double22) * 0.01 + (0.1 + double26) * 1.0;
            final double double29 = double6 + double20 * double25 + Math.sin(double25 * 3.141592653589793 * 8.0 + double22) * double14 * double26;
            final float float11 = 0.87f * float10 + 0.3f * (1.0f - float10);
            final float float12 = 0.91f * float10 + 0.6f * (1.0f - float10);
            final float float13 = 0.85f * float10 + 0.5f * (1.0f - float10);
            bufferBuilder12.vertex(double27, double28, double29).color(float11, float12, float13, 1.0f).next();
            bufferBuilder12.vertex(double27 + 0.1 * double26, double28, double29 + 0.1 * double26).color(float11, float12, float13, 1.0f).next();
            if (integer46 > tridentEntity.ar * 2) {
                break;
            }
        }
        tessellator11.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableCull();
    }
    
    static {
        SKIN = new Identifier("textures/entity/trident.png");
    }
}
