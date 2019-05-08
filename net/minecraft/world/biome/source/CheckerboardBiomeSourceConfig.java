package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeSourceConfig implements BiomeSourceConfig
{
    private Biome[] biomes;
    private int size;
    
    public CheckerboardBiomeSourceConfig() {
        this.biomes = new Biome[] { Biomes.c };
        this.size = 1;
    }
    
    public CheckerboardBiomeSourceConfig a(final Biome[] arr) {
        this.biomes = arr;
        return this;
    }
    
    public CheckerboardBiomeSourceConfig a(final int integer) {
        this.size = integer;
        return this;
    }
    
    public Biome[] getBiomes() {
        return this.biomes;
    }
    
    public int getSize() {
        return this.size;
    }
}
