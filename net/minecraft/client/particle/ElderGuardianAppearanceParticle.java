package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ElderGuardianAppearanceParticle extends Particle
{
    private LivingEntity guardian;
    
    private ElderGuardianAppearanceParticle(final World world, final double double2, final double double4, final double double6) {
        super(world, double2, double4, double6);
        this.gravityStrength = 0.0f;
        this.maxAge = 30;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.CUSTOM;
    }
    
    @Override
    public void update() {
        super.update();
        if (this.guardian == null) {
            final ElderGuardianEntity elderGuardianEntity1 = EntityType.ELDER_GUARDIAN.create(this.world);
            elderGuardianEntity1.straightenTail();
            this.guardian = elderGuardianEntity1;
        }
    }
    
    @Override
    public void buildGeometry(final BufferBuilder bufferBuilder, final Camera camera, final float tickDelta, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (this.guardian == null) {
            return;
        }
        final EntityRenderDispatcher entityRenderDispatcher9 = MinecraftClient.getInstance().getEntityRenderManager();
        entityRenderDispatcher9.setRenderPosition(Particle.cameraX, Particle.cameraY, Particle.cameraZ);
        final float float9 = 1.0f / ElderGuardianEntity.b;
        final float float10 = (this.age + tickDelta) / this.maxAge;
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();
        GlStateManager.enableDepthTest();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        final float float11 = 240.0f;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0f, 240.0f);
        GlStateManager.pushMatrix();
        final float float12 = 0.05f + 0.5f * MathHelper.sin(float10 * 3.1415927f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, float12);
        GlStateManager.translatef(0.0f, 1.8f, 0.0f);
        GlStateManager.rotatef(180.0f - camera.getYaw(), 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(60.0f - 150.0f * float10 - camera.getPitch(), 1.0f, 0.0f, 0.0f);
        GlStateManager.translatef(0.0f, -0.4f, -1.5f);
        GlStateManager.scalef(float9, float9, float9);
        this.guardian.yaw = 0.0f;
        this.guardian.headYaw = 0.0f;
        this.guardian.prevYaw = 0.0f;
        this.guardian.prevHeadYaw = 0.0f;
        entityRenderDispatcher9.render(this.guardian, 0.0, 0.0, 0.0, 0.0f, tickDelta, false);
        GlStateManager.popMatrix();
        GlStateManager.enableDepthTest();
    }
    
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            return new ElderGuardianAppearanceParticle(world, x, y, z, null);
        }
    }
}
