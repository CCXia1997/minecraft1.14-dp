package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ExplosionSmokeParticle extends SpriteBillboardParticle
{
    private final SpriteProvider C;
    
    protected ExplosionSmokeParticle(final World world, final double x, final double y, final double z, final double double8, final double double10, final double double12, final SpriteProvider spriteProvider14) {
        super(world, x, y, z);
        this.C = spriteProvider14;
        this.velocityX = double8 + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
        this.velocityY = double10 + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
        this.velocityZ = double12 + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
        final float float15 = this.random.nextFloat() * 0.3f + 0.7f;
        this.colorRed = float15;
        this.colorGreen = float15;
        this.colorBlue = float15;
        this.scale = 0.1f * (this.random.nextFloat() * this.random.nextFloat() * 6.0f + 1.0f);
        this.maxAge = (int)(16.0 / (this.random.nextFloat() * 0.8 + 0.2)) + 2;
        this.setSpriteForAge(spriteProvider14);
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
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
        this.setSpriteForAge(this.C);
        this.velocityY += 0.004;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.8999999761581421;
        this.velocityY *= 0.8999999761581421;
        this.velocityZ *= 0.8999999761581421;
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
            return new ExplosionSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a);
        }
    }
}
