package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.util.math.MathHelper;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RedDustParticle extends SpriteBillboardParticle
{
    private final SpriteProvider C;
    
    private RedDustParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final DustParticleParameters dustParticleParameters14, final SpriteProvider spriteProvider15) {
        super(world, double2, double4, double6, double8, double10, double12);
        this.C = spriteProvider15;
        this.velocityX *= 0.10000000149011612;
        this.velocityY *= 0.10000000149011612;
        this.velocityZ *= 0.10000000149011612;
        final float float16 = (float)Math.random() * 0.4f + 0.6f;
        this.colorRed = ((float)(Math.random() * 0.20000000298023224) + 0.8f) * dustParticleParameters14.getRed() * float16;
        this.colorGreen = ((float)(Math.random() * 0.20000000298023224) + 0.8f) * dustParticleParameters14.getGreen() * float16;
        this.colorBlue = ((float)(Math.random() * 0.20000000298023224) + 0.8f) * dustParticleParameters14.getBlue() * float16;
        this.scale *= 0.75f * dustParticleParameters14.getAlpha();
        final int integer17 = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.maxAge = (int)Math.max(integer17 * dustParticleParameters14.getAlpha(), 1.0f);
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
    public static class Factory implements ParticleFactory<DustParticleParameters>
    {
        private final SpriteProvider a;
        
        public Factory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DustParticleParameters parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            return new RedDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, parameters, this.a, null);
        }
    }
}
