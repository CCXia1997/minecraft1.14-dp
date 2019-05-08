package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;

public class ConfiguredSurfaceBuilder<SC extends SurfaceConfig>
{
    public final SurfaceBuilder<SC> surfaceBuilder;
    public final SC config;
    
    public ConfiguredSurfaceBuilder(final SurfaceBuilder<SC> surfaceBuilder, final SC surfaceConfig) {
        this.surfaceBuilder = surfaceBuilder;
        this.config = surfaceConfig;
    }
    
    public void generate(final Random random, final Chunk chunk, final Biome biome, final int integer4, final int integer5, final int integer6, final double double7, final BlockState defaultBlock, final BlockState defaultFluid, final int integer11, final long seed) {
        this.surfaceBuilder.generate(random, chunk, biome, integer4, integer5, integer6, double7, defaultBlock, defaultFluid, integer11, seed, this.config);
    }
    
    public void initSeed(final long seed) {
        this.surfaceBuilder.initSeed(seed);
    }
    
    public SC getConfig() {
        return this.config;
    }
}
