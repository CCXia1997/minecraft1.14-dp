package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FireSmokeParticle extends SpriteBillboardParticle
{
    private final SpriteProvider C;
    
    protected FireSmokeParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final float float14, final SpriteProvider spriteProvider15) {
        super(world, double2, double4, double6, 0.0, 0.0, 0.0);
        this.C = spriteProvider15;
        this.velocityX *= 0.10000000149011612;
        this.velocityY *= 0.10000000149011612;
        this.velocityZ *= 0.10000000149011612;
        this.velocityX += double8;
        this.velocityY += double10;
        this.velocityZ += double12;
        final float float15 = (float)(Math.random() * 0.30000001192092896);
        this.colorRed = float15;
        this.colorGreen = float15;
        this.colorBlue = float15;
        this.scale *= 0.75f * float14;
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge *= (int)float14;
        this.maxAge = Math.max(this.maxAge, 1);
        this.setSpriteForAge(spriteProvider15);
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }
    
    @Override
    public float getSize(final float tickDelta) {
        return this.scale * MathHelper.clamp((this.age + tickDelta) / this.maxAge * 32.0f, 0.0f, 1.0f);
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
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
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
            return new FireSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ, 1.0f, this.a);
        }
    }
}
