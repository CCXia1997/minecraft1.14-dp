package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EmotionParticle extends SpriteBillboardParticle
{
    private EmotionParticle(final World world, final double double2, final double double4, final double double6) {
        super(world, double2, double4, double6, 0.0, 0.0, 0.0);
        this.velocityX *= 0.009999999776482582;
        this.velocityY *= 0.009999999776482582;
        this.velocityZ *= 0.009999999776482582;
        this.velocityY += 0.1;
        this.scale *= 1.5f;
        this.maxAge = 16;
        this.collidesWithWorld = false;
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
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= 0.8600000143051147;
        this.velocityY *= 0.8600000143051147;
        this.velocityZ *= 0.8600000143051147;
        if (this.onGround) {
            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class HeartFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public HeartFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final EmotionParticle emotionParticle15 = new EmotionParticle(world, x, y, z, null);
            emotionParticle15.setSprite(this.a);
            return emotionParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class AngryVillagerFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public AngryVillagerFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final EmotionParticle emotionParticle15 = new EmotionParticle(world, x, y + 0.5, z, null);
            emotionParticle15.setSprite(this.a);
            emotionParticle15.setColor(1.0f, 1.0f, 1.0f);
            return emotionParticle15;
        }
    }
}
