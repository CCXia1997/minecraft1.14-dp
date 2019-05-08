package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpitParticle extends ExplosionSmokeParticle
{
    private SpitParticle(final World world, final double x, final double y, final double z, final double double8, final double double10, final double double12, final SpriteProvider spriteProvider14) {
        super(world, x, y, z, double8, double10, double12, spriteProvider14);
        this.gravityStrength = 0.5f;
    }
    
    @Override
    public void update() {
        super.update();
        this.velocityY -= 0.004 + 0.04 * this.gravityStrength;
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
            return new SpitParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
        }
    }
}
