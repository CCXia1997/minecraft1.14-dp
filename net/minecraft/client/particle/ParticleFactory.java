package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleParameters;

@Environment(EnvType.CLIENT)
public interface ParticleFactory<T extends ParticleParameters>
{
    @Nullable
    Particle createParticle(final T arg1, final World arg2, final double arg3, final double arg4, final double arg5, final double arg6, final double arg7, final double arg8);
}
