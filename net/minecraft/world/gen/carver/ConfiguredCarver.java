package net.minecraft.world.gen.carver;

import java.util.BitSet;
import net.minecraft.world.chunk.Chunk;
import java.util.Random;

public class ConfiguredCarver<WC extends CarverConfig>
{
    public final Carver<WC> carver;
    public final WC config;
    
    public ConfiguredCarver(final Carver<WC> carver, final WC config) {
        this.carver = carver;
        this.config = config;
    }
    
    public boolean shouldCarve(final Random random, final int chunkX, final int chunkZ) {
        return this.carver.shouldCarve(random, chunkX, chunkZ, this.config);
    }
    
    public boolean carve(final Chunk chunk, final Random random, final int seaLevel, final int chunkX, final int chunkZ, final int mainChunkX, final int mainChunkY, final BitSet mask) {
        return this.carver.carve(chunk, random, seaLevel, chunkX, chunkZ, mainChunkX, mainChunkY, mask, this.config);
    }
}
