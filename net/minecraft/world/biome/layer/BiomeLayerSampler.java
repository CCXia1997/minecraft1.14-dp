package net.minecraft.world.biome.layer;

import org.apache.logging.log4j.LogManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.SharedConstants;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.Logger;

public class BiomeLayerSampler
{
    private static final Logger LOGGER;
    private final CachingLayerSampler sampler;
    
    public BiomeLayerSampler(final LayerFactory<CachingLayerSampler> layerFactory) {
        this.sampler = layerFactory.make();
    }
    
    public Biome[] sample(final int x, final int y, final int width, final int height) {
        final Biome[] arr5 = new Biome[width * height];
        for (int integer6 = 0; integer6 < height; ++integer6) {
            for (int integer7 = 0; integer7 < width; ++integer7) {
                final int integer8 = this.sampler.sample(x + integer7, y + integer6);
                final Biome biome9 = this.getBiome(integer8);
                arr5[integer7 + integer6 * width] = biome9;
            }
        }
        return arr5;
    }
    
    private Biome getBiome(final int id) {
        final Biome biome2 = Registry.BIOME.get(id);
        if (biome2 != null) {
            return biome2;
        }
        if (SharedConstants.isDevelopment) {
            throw new IllegalStateException("Unknown biome id: " + id);
        }
        BiomeLayerSampler.LOGGER.warn("Unknown biome id: ", id);
        return Biomes.DEFAULT;
    }
    
    public Biome sample(final int x, final int y) {
        return this.getBiome(this.sampler.sample(x, y));
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
