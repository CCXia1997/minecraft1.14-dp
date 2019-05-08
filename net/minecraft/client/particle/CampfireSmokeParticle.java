package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CampfireSmokeParticle extends SpriteBillboardParticle
{
    private CampfireSmokeParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final boolean boolean14) {
        super(world, double2, double4, double6);
        this.e(3.0f);
        this.setBoundingBoxSpacing(0.25f, 0.25f);
        if (boolean14) {
            this.maxAge = this.random.nextInt(50) + 280;
        }
        else {
            this.maxAge = this.random.nextInt(50) + 80;
        }
        this.gravityStrength = 3.0E-6f;
        this.velocityX = double8;
        this.velocityY = double10 + this.random.nextFloat() / 500.0f;
        this.velocityZ = double12;
    }
    
    @Override
    public void update() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge || this.colorAlpha <= 0.0f) {
            this.markDead();
            return;
        }
        this.velocityX += this.random.nextFloat() / 5000.0f * (this.random.nextBoolean() ? 1 : -1);
        this.velocityZ += this.random.nextFloat() / 5000.0f * (this.random.nextBoolean() ? 1 : -1);
        this.velocityY -= this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.age >= this.maxAge - 60 && this.colorAlpha > 0.01f) {
            this.colorAlpha -= 0.015f;
        }
    }
    
    @Override
    public ParticleTextureSheet getTextureSheet() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    @Environment(EnvType.CLIENT)
    public static class a implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public a(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final CampfireSmokeParticle campfireSmokeParticle15 = new CampfireSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ, false, null);
            campfireSmokeParticle15.setColorAlpha(0.9f);
            campfireSmokeParticle15.setSprite(this.a);
            return campfireSmokeParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class b implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public b(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final CampfireSmokeParticle campfireSmokeParticle15 = new CampfireSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ, true, null);
            campfireSmokeParticle15.setColorAlpha(0.95f);
            campfireSmokeParticle15.setSprite(this.a);
            return campfireSmokeParticle15;
        }
    }
}
