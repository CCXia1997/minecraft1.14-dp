package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SweepAttackParticle extends SpriteBillboardParticle
{
    private final SpriteProvider C;
    
    private SweepAttackParticle(final World world, final double double2, final double double4, final double double6, final double double8, final SpriteProvider spriteProvider10) {
        super(world, double2, double4, double6, 0.0, 0.0, 0.0);
        this.C = spriteProvider10;
        this.maxAge = 4;
        final float float11 = this.random.nextFloat() * 0.6f + 0.4f;
        this.colorRed = float11;
        this.colorGreen = float11;
        this.colorBlue = float11;
        this.scale = 1.0f - (float)double8 * 0.5f;
        this.setSpriteForAge(spriteProvider10);
    }
    
    public int getColorMultiplier(final float float1) {
        return 15728880;
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
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
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
            return new SweepAttackParticle(world, x, y, z, velocityX, this.a, null);
        }
    }
}
