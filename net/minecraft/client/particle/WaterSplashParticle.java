package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WaterSplashParticle extends RainSplashParticle
{
    private WaterSplashParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12) {
        super(world, double2, double4, double6);
        this.gravityStrength = 0.04f;
        if (double10 == 0.0 && (double8 != 0.0 || double12 != 0.0)) {
            this.velocityX = double8;
            this.velocityY = 0.1;
            this.velocityZ = double12;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class SplashFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider a;
        
        public SplashFactory(final SpriteProvider spriteProvider) {
            this.a = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final WaterSplashParticle waterSplashParticle15 = new WaterSplashParticle(world, x, y, z, velocityX, velocityY, velocityZ, null);
            waterSplashParticle15.setSprite(this.a);
            return waterSplashParticle15;
        }
    }
}
