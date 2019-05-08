package net.minecraft.world.biome.layer;

import net.minecraft.util.math.noise.PerlinNoiseSampler;

public enum OceanTemperatureLayer implements InitLayer
{
    a;
    
    @Override
    public int sample(final LayerRandomnessSource context, final int x, final int y) {
        final PerlinNoiseSampler perlinNoiseSampler4 = context.getNoiseSampler();
        final double double5 = perlinNoiseSampler4.sample(x / 8.0, y / 8.0, 0.0, 0.0, 0.0);
        if (double5 > 0.4) {
            return BiomeLayers.WARM_OCEAN_ID;
        }
        if (double5 > 0.2) {
            return BiomeLayers.LUKEWARM_OCEAN_ID;
        }
        if (double5 < -0.4) {
            return BiomeLayers.FROZEN_OCEAN_ID;
        }
        if (double5 < -0.2) {
            return BiomeLayers.COLD_OCEAN_ID;
        }
        return BiomeLayers.OCEAN_ID;
    }
}
