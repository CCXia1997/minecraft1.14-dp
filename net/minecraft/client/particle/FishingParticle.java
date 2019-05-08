package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FishingParticle extends SpriteBillboardParticle
{
    private final SpriteProvider C;
    
    private FishingParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final SpriteProvider spriteProvider14) {
        super(world, double2, double4, double6, 0.0, 0.0, 0.0);
        this.C = spriteProvider14;
        this.velocityX *= 0.30000001192092896;
        this.velocityY = Math.random() * 0.20000000298023224 + 0.10000000149011612;
        this.velocityZ *= 0.30000001192092896;
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.setSpriteForAge(spriteProvider14);
        this.gravityStrength = 0.0f;
        this.velocityX = double8;
        this.velocityY = double10;
        this.velocityZ = double12;
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
        final int integer1 = 60 - this.maxAge;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.velocityY -= this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.9800000190734863;
        this.velocityY *= 0.9800000190734863;
        this.velocityZ *= 0.9800000190734863;
        final float float2 = integer1 * 0.001f;
        this.setBoundingBoxSpacing(float2, float2);
        this.setSprite(this.C.getSprite(integer1 % 4, 4));
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
            return new FishingParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
        }
    }
}
