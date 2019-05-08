package net.minecraft.client.particle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ExplosionEmitterParticle extends NoRenderParticle
{
    private int age_;
    private final int maxAge_;
    
    private ExplosionEmitterParticle(final World world, final double double2, final double double4, final double double6) {
        super(world, double2, double4, double6, 0.0, 0.0, 0.0);
        this.maxAge_ = 8;
    }
    
    @Override
    public void update() {
        for (int integer1 = 0; integer1 < 6; ++integer1) {
            final double double2 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            final double double3 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            final double double4 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            this.world.addParticle(ParticleTypes.w, double2, double3, double4, this.age_ / (float)this.maxAge_, 0.0, 0.0);
        }
        ++this.age_;
        if (this.age_ == this.maxAge_) {
            this.markDead();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType>
    {
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            return new ExplosionEmitterParticle(world, x, y, z, null);
        }
    }
}
