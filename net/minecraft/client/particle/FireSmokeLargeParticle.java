package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FireSmokeLargeParticle extends FireSmokeParticle
{
    protected FireSmokeLargeParticle(final World world, final double x, final double y, final double z, final double double8, final double double10, final double double12, final SpriteProvider spriteProvider14) {
        super(world, x, y, z, double8, double10, double12, 2.5f, spriteProvider14);
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
            return new FireSmokeLargeParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a);
        }
    }
}
