package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemPickupParticle extends Particle
{
    private final Entity C;
    private final Entity D;
    private int E;
    private final int F;
    private final float G;
    private final EntityRenderDispatcher H;
    
    public ItemPickupParticle(final World world, final Entity entity2, final Entity entity3, final float float4) {
        this(world, entity2, entity3, float4, entity2.getVelocity());
    }
    
    private ItemPickupParticle(final World world, final Entity entity2, final Entity entity3, final float float4, final Vec3d vec3d) {
        super(world, entity2.x, entity2.y, entity2.z, vec3d.x, vec3d.y, vec3d.z);
        this.H = MinecraftClient.getInstance().getEntityRenderManager();
        this.C = entity2;
        this.D = entity3;
        this.F = 3;
        this.G = float4;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.CUSTOM;
    }
    
    @Override
    public void buildGeometry(final BufferBuilder bufferBuilder, final Camera camera, final float tickDelta, final float float4, final float float5, final float float6, final float float7, final float float8) {
        float float9 = (this.E + tickDelta) / this.F;
        float9 *= float9;
        final double double10 = this.C.x;
        final double double11 = this.C.y;
        final double double12 = this.C.z;
        final double double13 = MathHelper.lerp(tickDelta, this.D.prevRenderX, this.D.x);
        final double double14 = MathHelper.lerp(tickDelta, this.D.prevRenderY, this.D.y) + this.G;
        final double double15 = MathHelper.lerp(tickDelta, this.D.prevRenderZ, this.D.z);
        double double16 = MathHelper.lerp(float9, double10, double13);
        double double17 = MathHelper.lerp(float9, double11, double14);
        double double18 = MathHelper.lerp(float9, double12, double15);
        final int integer28 = this.getColorMultiplier(tickDelta);
        final int integer29 = integer28 % 65536;
        final int integer30 = integer28 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer29, (float)integer30);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        double16 -= ItemPickupParticle.cameraX;
        double17 -= ItemPickupParticle.cameraY;
        double18 -= ItemPickupParticle.cameraZ;
        GlStateManager.enableLighting();
        this.H.render(this.C, double16, double17, double18, this.C.yaw, tickDelta, false);
    }
    
    @Override
    public void update() {
        ++this.E;
        if (this.E == this.F) {
            this.markDead();
        }
    }
}
