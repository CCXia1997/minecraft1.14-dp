package net.minecraft.client.particle;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EmitterParticle extends NoRenderParticle
{
    private final Entity entity;
    private int emitterAge;
    private final int maxEmitterAge;
    private final ParticleParameters parameters;
    
    public EmitterParticle(final World world, final Entity entity, final ParticleParameters parameters) {
        this(world, entity, parameters, 3);
    }
    
    public EmitterParticle(final World world, final Entity entity, final ParticleParameters particleParameters, final int integer) {
        this(world, entity, particleParameters, integer, entity.getVelocity());
    }
    
    private EmitterParticle(final World world, final Entity entity, final ParticleParameters particleParameters, final int integer, final Vec3d vec3d) {
        super(world, entity.x, entity.getBoundingBox().minY + entity.getHeight() / 2.0f, entity.z, vec3d.x, vec3d.y, vec3d.z);
        this.entity = entity;
        this.maxEmitterAge = integer;
        this.parameters = particleParameters;
        this.update();
    }
    
    @Override
    public void update() {
        for (int integer1 = 0; integer1 < 16; ++integer1) {
            final double double2 = this.random.nextFloat() * 2.0f - 1.0f;
            final double double3 = this.random.nextFloat() * 2.0f - 1.0f;
            final double double4 = this.random.nextFloat() * 2.0f - 1.0f;
            if (double2 * double2 + double3 * double3 + double4 * double4 <= 1.0) {
                final double double5 = this.entity.x + double2 * this.entity.getWidth() / 4.0;
                final double double6 = this.entity.getBoundingBox().minY + this.entity.getHeight() / 2.0f + double3 * this.entity.getHeight() / 4.0;
                final double double7 = this.entity.z + double4 * this.entity.getWidth() / 4.0;
                this.world.addParticle(this.parameters, false, double5, double6, double7, double2, double3 + 0.2, double4);
            }
        }
        ++this.emitterAge;
        if (this.emitterAge >= this.maxEmitterAge) {
            this.markDead();
        }
    }
}
