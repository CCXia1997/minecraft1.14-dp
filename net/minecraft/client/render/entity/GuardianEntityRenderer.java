package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.entity.mob.GuardianEntity;

@Environment(EnvType.CLIENT)
public class GuardianEntityRenderer extends MobEntityRenderer<GuardianEntity, GuardianEntityModel>
{
    private static final Identifier SKIN;
    private static final Identifier EXPLOSION_BEAM_TEX;
    
    public GuardianEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        this(entityRenderDispatcher, 0.5f);
    }
    
    protected GuardianEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final float float2) {
        super(entityRenderDispatcher, new GuardianEntityModel(), float2);
    }
    
    @Override
    public boolean isVisible(final GuardianEntity guardianEntity, final VisibleRegion visibleRegion, final double double3, final double double5, final double double7) {
        if (super.isVisible(guardianEntity, visibleRegion, double3, double5, double7)) {
            return true;
        }
        if (guardianEntity.hasBeamTarget()) {
            final LivingEntity livingEntity9 = guardianEntity.getBeamTarget();
            if (livingEntity9 != null) {
                final Vec3d vec3d10 = this.fromLerpedPosition(livingEntity9, livingEntity9.getHeight() * 0.5, 1.0f);
                final Vec3d vec3d11 = this.fromLerpedPosition(guardianEntity, guardianEntity.getStandingEyeHeight(), 1.0f);
                if (visibleRegion.intersects(new BoundingBox(vec3d11.x, vec3d11.y, vec3d11.z, vec3d10.x, vec3d10.y, vec3d10.z))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private Vec3d fromLerpedPosition(final LivingEntity entity, final double yOffset, final float delta) {
        final double double5 = MathHelper.lerp(delta, entity.prevRenderX, entity.x);
        final double double6 = MathHelper.lerp(delta, entity.prevRenderY, entity.y) + yOffset;
        final double double7 = MathHelper.lerp(delta, entity.prevRenderZ, entity.z);
        return new Vec3d(double5, double6, double7);
    }
    
    @Override
    public void render(final GuardianEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        super.render(entity, x, y, z, yaw, tickDelta);
        final LivingEntity livingEntity10 = entity.getBeamTarget();
        if (livingEntity10 != null) {
            final float float11 = entity.getBeamProgress(tickDelta);
            final Tessellator tessellator12 = Tessellator.getInstance();
            final BufferBuilder bufferBuilder13 = tessellator12.getBufferBuilder();
            this.bindTexture(GuardianEntityRenderer.EXPLOSION_BEAM_TEX);
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            final float float12 = 240.0f;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0f, 240.0f);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            final float float13 = entity.world.getTime() + tickDelta;
            final float float14 = float13 * 0.5f % 1.0f;
            final float float15 = entity.getStandingEyeHeight();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x, (float)y + float15, (float)z);
            final Vec3d vec3d18 = this.fromLerpedPosition(livingEntity10, livingEntity10.getHeight() * 0.5, tickDelta);
            final Vec3d vec3d19 = this.fromLerpedPosition(entity, float15, tickDelta);
            Vec3d vec3d20 = vec3d18.subtract(vec3d19);
            final double double21 = vec3d20.length() + 1.0;
            vec3d20 = vec3d20.normalize();
            final float float16 = (float)Math.acos(vec3d20.y);
            final float float17 = (float)Math.atan2(vec3d20.z, vec3d20.x);
            GlStateManager.rotatef((1.5707964f - float17) * 57.295776f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(float16 * 57.295776f, 1.0f, 0.0f, 0.0f);
            final int integer25 = 1;
            final double double22 = float13 * 0.05 * -1.5;
            bufferBuilder13.begin(7, VertexFormats.POSITION_UV_COLOR);
            final float float18 = float11 * float11;
            final int integer26 = 64 + (int)(float18 * 191.0f);
            final int integer27 = 32 + (int)(float18 * 191.0f);
            final int integer28 = 128 - (int)(float18 * 64.0f);
            final double double23 = 0.2;
            final double double24 = 0.282;
            final double double25 = 0.0 + Math.cos(double22 + 2.356194490192345) * 0.282;
            final double double26 = 0.0 + Math.sin(double22 + 2.356194490192345) * 0.282;
            final double double27 = 0.0 + Math.cos(double22 + 0.7853981633974483) * 0.282;
            final double double28 = 0.0 + Math.sin(double22 + 0.7853981633974483) * 0.282;
            final double double29 = 0.0 + Math.cos(double22 + 3.9269908169872414) * 0.282;
            final double double30 = 0.0 + Math.sin(double22 + 3.9269908169872414) * 0.282;
            final double double31 = 0.0 + Math.cos(double22 + 5.497787143782138) * 0.282;
            final double double32 = 0.0 + Math.sin(double22 + 5.497787143782138) * 0.282;
            final double double33 = 0.0 + Math.cos(double22 + 3.141592653589793) * 0.2;
            final double double34 = 0.0 + Math.sin(double22 + 3.141592653589793) * 0.2;
            final double double35 = 0.0 + Math.cos(double22 + 0.0) * 0.2;
            final double double36 = 0.0 + Math.sin(double22 + 0.0) * 0.2;
            final double double37 = 0.0 + Math.cos(double22 + 1.5707963267948966) * 0.2;
            final double double38 = 0.0 + Math.sin(double22 + 1.5707963267948966) * 0.2;
            final double double39 = 0.0 + Math.cos(double22 + 4.71238898038469) * 0.2;
            final double double40 = 0.0 + Math.sin(double22 + 4.71238898038469) * 0.2;
            final double double41 = double21;
            final double double42 = 0.0;
            final double double43 = 0.4999;
            final double double44 = -1.0f + float14;
            final double double45 = double21 * 2.5 + double44;
            bufferBuilder13.vertex(double33, double41, double34).texture(0.4999, double45).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double33, 0.0, double34).texture(0.4999, double44).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double35, 0.0, double36).texture(0.0, double44).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double35, double41, double36).texture(0.0, double45).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double37, double41, double38).texture(0.4999, double45).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double37, 0.0, double38).texture(0.4999, double44).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double39, 0.0, double40).texture(0.0, double44).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double39, double41, double40).texture(0.0, double45).color(integer26, integer27, integer28, 255).next();
            double double46 = 0.0;
            if (entity.age % 2 == 0) {
                double46 = 0.5;
            }
            bufferBuilder13.vertex(double25, double41, double26).texture(0.5, double46 + 0.5).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double27, double41, double28).texture(1.0, double46 + 0.5).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double31, double41, double32).texture(1.0, double46).color(integer26, integer27, integer28, 255).next();
            bufferBuilder13.vertex(double29, double41, double30).texture(0.5, double46).color(integer26, integer27, integer28, 255).next();
            tessellator12.draw();
            GlStateManager.popMatrix();
        }
    }
    
    protected Identifier getTexture(final GuardianEntity guardianEntity) {
        return GuardianEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/guardian.png");
        EXPLOSION_BEAM_TEX = new Identifier("textures/entity/guardian_beam.png");
    }
}
