package net.minecraft.world.biome.layer;

import net.minecraft.util.math.noise.PerlinNoiseSampler;

public interface LayerRandomnessSource
{
    int nextInt(final int arg1);
    
    PerlinNoiseSampler getNoiseSampler();
}
