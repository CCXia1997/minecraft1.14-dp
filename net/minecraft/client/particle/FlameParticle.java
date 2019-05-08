package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FlameParticle extends SpriteBillboardParticle
{
    private FlameParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        super(world, double2, double4, double6, double8, double10, double12);
        this.velocityX = this.velocityX * 0.009999999776482582 + double8;
        this.velocityY = this.velocityY * 0.009999999776482582 + double10;
        this.velocityZ = this.velocityZ * 0.009999999776482582 + double12;
        this.x += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        this.y += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        this.z += (this.random.nextFloat() - this.random.nextFloat()) * 0.05f;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }
    
    @Override
    public void move(final double dx, final double dy, final double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }
    
    @Override
    public float getSize(final float tickDelta) {
        final float float2 = (this.age + tickDelta) / this.maxAge;
        return this.scale * (1.0f - float2 * float2 * 0.5f);
    }
    
    public int getColorMultiplier(final float float1) {
        float float2 = (this.age + float1) / this.maxAge;
        float2 = MathHelper.clamp(float2, 0.0f, 1.0f);
        final int integer3 = super.getColorMultiplier(float1);
        int integer4 = integer3 & 0xFF;
        final int integer5 = integer3 >> 16 & 0xFF;
        integer4 += (int)(float2 * 15.0f * 16.0f);
        if (integer4 > 240) {
            integer4 = 240;
        }
        return integer4 | integer5 << 16;
    }
    
    @Override
    public void update() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.9599999785423279;
        this.velocityY *= 0.9599999785423279;
        this.velocityZ *= 0.9599999785423279;
        if (this.onGround) {
            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public Factory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final FlameParticle flameParticle15 = new FlameParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            flameParticle15.setSprite(this.a);
            return flameParticle15;
        }
    }
}
