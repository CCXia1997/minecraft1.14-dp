package net.minecraft.client.particle;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class BillboardParticle extends Particle
{
    protected float scale;
    
    protected BillboardParticle(final World world, final double x, final double y, final double z) {
        super(world, x, y, z);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }
    
    protected BillboardParticle(final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }
    
    @Override
    public void buildGeometry(final BufferBuilder bufferBuilder, final Camera camera, final float tickDelta, final float float4, final float float5, final float float6, final float float7, final float float8) {
        final float float9 = this.getSize(tickDelta);
        final float float10 = this.getMinU();
        final float float11 = this.getMaxU();
        final float float12 = this.getMinV();
        final float float13 = this.getMaxV();
        final float float14 = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - BillboardParticle.cameraX);
        final float float15 = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - BillboardParticle.cameraY);
        final float float16 = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - BillboardParticle.cameraZ);
        final int integer17 = this.getColorMultiplier(tickDelta);
        final int integer18 = integer17 >> 16 & 0xFFFF;
        final int integer19 = integer17 & 0xFFFF;
        final Vec3d[] arr20 = { new Vec3d(-float4 * float9 - float7 * float9, -float5 * float9, -float6 * float9 - float8 * float9), new Vec3d(-float4 * float9 + float7 * float9, float5 * float9, -float6 * float9 + float8 * float9), new Vec3d(float4 * float9 + float7 * float9, float5 * float9, float6 * float9 + float8 * float9), new Vec3d(float4 * float9 - float7 * float9, -float5 * float9, float6 * float9 - float8 * float9) };
        if (this.angle != 0.0f) {
            final float float17 = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
            final float float18 = MathHelper.cos(float17 * 0.5f);
            final float float19 = (float)(MathHelper.sin(float17 * 0.5f) * camera.l().x);
            final float float20 = (float)(MathHelper.sin(float17 * 0.5f) * camera.l().y);
            final float float21 = (float)(MathHelper.sin(float17 * 0.5f) * camera.l().z);
            final Vec3d vec3d26 = new Vec3d(float19, float20, float21);
            for (int integer20 = 0; integer20 < 4; ++integer20) {
                arr20[integer20] = vec3d26.multiply(2.0 * arr20[integer20].dotProduct(vec3d26)).add(arr20[integer20].multiply(float18 * float18 - vec3d26.dotProduct(vec3d26))).add(vec3d26.crossProduct(arr20[integer20]).multiply(2.0f * float18));
            }
        }
        bufferBuilder.vertex(float14 + arr20[0].x, float15 + arr20[0].y, float16 + arr20[0].z).texture(float11, float13).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).texture(integer18, integer19).next();
        bufferBuilder.vertex(float14 + arr20[1].x, float15 + arr20[1].y, float16 + arr20[1].z).texture(float11, float12).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).texture(integer18, integer19).next();
        bufferBuilder.vertex(float14 + arr20[2].x, float15 + arr20[2].y, float16 + arr20[2].z).texture(float10, float12).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).texture(integer18, integer19).next();
        bufferBuilder.vertex(float14 + arr20[3].x, float15 + arr20[3].y, float16 + arr20[3].z).texture(float10, float13).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).texture(integer18, integer19).next();
    }
    
    public float getSize(final float tickDelta) {
        return this.scale;
    }
    
    @Override
    public Particle e(final float float1) {
        this.scale *= float1;
        return super.e(float1);
    }
    
    protected abstract float getMinU();
    
    protected abstract float getMaxU();
    
    protected abstract float getMinV();
    
    protected abstract float getMaxV();
}
