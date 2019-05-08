package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;
import com.mojang.datafixers.Dynamic;
import java.util.function.Function;

public class ShatteredSavannaSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig>
{
    public ShatteredSavannaSurfaceBuilder(final Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
        super(function);
    }
    
    @Override
    public void generate(final Random random, final Chunk chunk, final Biome biome, final int x, final int z, final int worldHeight, final double noise, final BlockState defaultBlock, final BlockState defaultFluid, final int seaLevel, final long seed, final TernarySurfaceConfig surfaceBlocks) {
        if (noise > 1.75) {
            SurfaceBuilder.DEFAULT.generate(random, chunk, biome, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.STONE_CONFIG);
        }
        else if (noise > -0.5) {
            SurfaceBuilder.DEFAULT.generate(random, chunk, biome, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.COARSE_DIRT_CONFIG);
        }
        else {
            SurfaceBuilder.DEFAULT.generate(random, chunk, biome, x, z, worldHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.GRASS_CONFIG);
        }
    }
}
