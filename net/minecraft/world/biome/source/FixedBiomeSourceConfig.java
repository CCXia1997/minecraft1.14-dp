package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome;

public class FixedBiomeSourceConfig implements BiomeSourceConfig
{
    private Biome biome;
    
    public FixedBiomeSourceConfig() {
        this.biome = Biomes.c;
    }
    
    public FixedBiomeSourceConfig setBiome(final Biome biome) {
        this.biome = biome;
        return this;
    }
    
    public Biome getBiome() {
        return this.biome;
    }
}
