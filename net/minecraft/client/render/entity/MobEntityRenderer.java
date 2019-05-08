package net.minecraft.client.render.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.VisibleRegion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.MobEntity;

@Environment(EnvType.CLIENT)
public abstract class MobEntityRenderer<T extends MobEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M>
{
    public MobEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final M entityModel, final float float3) {
        super(entityRenderDispatcher, entityModel, float3);
    }
    
    @Override
    protected boolean hasLabel(final T entity) {
        return super.hasLabel(entity) && (entity.shouldRenderName() || (entity.hasCustomName() && entity == this.renderManager.targetedEntity));
    }
    
    public boolean isVisible(final T mobEntity, final VisibleRegion visibleRegion, final double double3, final double double5, final double double7) {
        if (super.isVisible((T)mobEntity, visibleRegion, double3, double5, double7)) {
            return true;
        }
        final Entity entity9 = mobEntity.getHoldingEntity();
        return entity9 != null && visibleRegion.intersects(entity9.getVisibilityBoundingBox());
    }
    
    @Override
    public void render(final T entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        super.render(entity, x, y, z, yaw, tickDelta);
        if (!this.e) {
            this.b(entity, x, y, z, yaw, tickDelta);
        }
    }
    
    protected void b(final T mobEntity, double double2, double double4, double double6, final float float8, final float float9) {
        final Entity entity10 = mobEntity.getHoldingEntity();
        if (entity10 == null) {
            return;
        }
        double4 -= (1.6 - mobEntity.getHeight()) * 0.5;
        final Tessellator tessellator11 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder12 = tessellator11.getBufferBuilder();
        final double double7 = MathHelper.lerp(float9 * 0.5f, entity10.yaw, entity10.prevYaw) * 0.017453292f;
        final double double8 = MathHelper.lerp(float9 * 0.5f, entity10.pitch, entity10.prevPitch) * 0.017453292f;
        double double9 = Math.cos(double7);
        double double10 = Math.sin(double7);
        double double11 = Math.sin(double8);
        if (entity10 instanceof AbstractDecorationEntity) {
            double9 = 0.0;
            double10 = 0.0;
            double11 = -1.0;
        }
        final double double12 = Math.cos(double8);
        final double double13 = MathHelper.lerp(float9, entity10.prevX, entity10.x) - double9 * 0.7 - double10 * 0.5 * double12;
        final double double14 = MathHelper.lerp(float9, entity10.prevY + entity10.getStandingEyeHeight() * 0.7, entity10.y + entity10.getStandingEyeHeight() * 0.7) - double11 * 0.5 - 0.25;
        final double double15 = MathHelper.lerp(float9, entity10.prevZ, entity10.z) - double10 * 0.7 + double9 * 0.5 * double12;
        final double double16 = MathHelper.lerp(float9, mobEntity.aK, mobEntity.aL) * 0.017453292f + 1.5707963267948966;
        double9 = Math.cos(double16) * mobEntity.getWidth() * 0.4;
        double10 = Math.sin(double16) * mobEntity.getWidth() * 0.4;
        final double double17 = MathHelper.lerp(float9, mobEntity.prevX, mobEntity.x) + double9;
        final double double18 = MathHelper.lerp(float9, mobEntity.prevY, mobEntity.y);
        final double double19 = MathHelper.lerp(float9, mobEntity.prevZ, mobEntity.z) + double10;
        double2 += double9;
        double6 += double10;
        final double double20 = (float)(double13 - double17);
        final double double21 = (float)(double14 - double18);
        final double double22 = (float)(double15 - double19);
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        final int integer45 = 24;
        final double double23 = 0.025;
        bufferBuilder12.begin(5, VertexFormats.POSITION_COLOR);
        for (int integer46 = 0; integer46 <= 24; ++integer46) {
            float float10 = 0.5f;
            float float11 = 0.4f;
            float float12 = 0.3f;
            if (integer46 % 2 == 0) {
                float10 *= 0.7f;
                float11 *= 0.7f;
                float12 *= 0.7f;
            }
            final float float13 = integer46 / 24.0f;
            bufferBuilder12.vertex(double2 + double20 * float13 + 0.0, double4 + double21 * (float13 * float13 + float13) * 0.5 + ((24.0f - integer46) / 18.0f + 0.125f), double6 + double22 * float13).color(float10, float11, float12, 1.0f).next();
            bufferBuilder12.vertex(double2 + double20 * float13 + 0.025, double4 + double21 * (float13 * float13 + float13) * 0.5 + ((24.0f - integer46) / 18.0f + 0.125f) + 0.025, double6 + double22 * float13).color(float10, float11, float12, 1.0f).next();
        }
        tessellator11.draw();
        bufferBuilder12.begin(5, VertexFormats.POSITION_COLOR);
        for (int integer46 = 0; integer46 <= 24; ++integer46) {
            float float10 = 0.5f;
            float float11 = 0.4f;
            float float12 = 0.3f;
            if (integer46 % 2 == 0) {
                float10 *= 0.7f;
                float11 *= 0.7f;
                float12 *= 0.7f;
            }
            final float float13 = integer46 / 24.0f;
            bufferBuilder12.vertex(double2 + double20 * float13 + 0.0, double4 + double21 * (float13 * float13 + float13) * 0.5 + ((24.0f - integer46) / 18.0f + 0.125f) + 0.025, double6 + double22 * float13).color(float10, float11, float12, 1.0f).next();
            bufferBuilder12.vertex(double2 + double20 * float13 + 0.025, double4 + double21 * (float13 * float13 + float13) * 0.5 + ((24.0f - integer46) / 18.0f + 0.125f), double6 + double22 * float13 + 0.025).color(float10, float11, float12, 1.0f).next();
        }
        tessellator11.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture();
        GlStateManager.enableCull();
    }
}
