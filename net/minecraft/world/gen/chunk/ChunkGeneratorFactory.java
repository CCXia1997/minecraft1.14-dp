package net.minecraft.world.gen.chunk;

import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.World;

interface ChunkGeneratorFactory<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>>
{
    T create(final World arg1, final BiomeSource arg2, final C arg3);
}
