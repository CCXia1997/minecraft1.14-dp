package net.minecraft.client.particle;

import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TotemParticle extends AnimatedParticle
{
    private TotemParticle(final World world, final double double2, final double double4, final double double6, final double double8, final double double10, final double double12, final SpriteProvider spriteProvider14) {
        super(world, double2, double4, double6, spriteProvider14, -0.05f);
        this.velocityX = double8;
        this.velocityY = double10;
        this.velocityZ = double12;
        this.scale *= 0.75f;
        this.maxAge = 60 + this.random.nextInt(12);
        this.setSpriteForAge(spriteProvider14);
        if (this.random.nextInt(4) == 0) {
            this.setColor(0.6f + this.random.nextFloat() * 0.2f, 0.6f + this.random.nextFloat() * 0.3f, this.random.nextFloat() * 0.2f);
        }
        else {
            this.setColor(0.1f + this.random.nextFloat() * 0.2f, 0.4f + this.random.nextFloat() * 0.3f, this.random.nextFloat() * 0.2f);
        }
        this.setResistance(0.6f);
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
            return new TotemParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.a, null);
        }
    }
}
