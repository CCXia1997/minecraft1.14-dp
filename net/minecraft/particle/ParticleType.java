package net.minecraft.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ParticleType<T extends ParticleParameters>
{
    private final boolean shouldAlwaysSpawn;
    private final ParticleParameters.Factory<T> parametersFactory;
    
    protected ParticleType(final boolean shouldAlwaysShow, final ParticleParameters.Factory<T> parametersFactory) {
        this.shouldAlwaysSpawn = shouldAlwaysShow;
        this.parametersFactory = parametersFactory;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldAlwaysSpawn() {
        return this.shouldAlwaysSpawn;
    }
    
    public ParticleParameters.Factory<T> getParametersFactory() {
        return this.parametersFactory;
    }
}
