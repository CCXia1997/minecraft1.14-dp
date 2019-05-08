package net.minecraft.world.gen;

import java.util.Random;

public class ChunkRandom extends Random
{
    private int sampleCount;
    
    public ChunkRandom() {
    }
    
    public ChunkRandom(final long seed) {
        super(seed);
    }
    
    public void consume(final int count) {
        for (int integer2 = 0; integer2 < count; ++integer2) {
            this.next(1);
        }
    }
    
    @Override
    protected int next(final int bound) {
        ++this.sampleCount;
        return super.next(bound);
    }
    
    public long setSeed(final int x, final int z) {
        final long long3 = x * 341873128712L + z * 132897987541L;
        this.setSeed(long3);
        return long3;
    }
    
    public long setSeed(final long worldSeed, final int x, final int z) {
        this.setSeed(worldSeed);
        final long long5 = this.nextLong() | 0x1L;
        final long long6 = this.nextLong() | 0x1L;
        final long long7 = x * long5 + z * long6 ^ worldSeed;
        this.setSeed(long7);
        return long7;
    }
    
    public long setFeatureSeed(final long worldSeed, final int index, final int step) {
        final long long5 = worldSeed + index + 10000 * step;
        this.setSeed(long5);
        return long5;
    }
    
    public long setStructureSeed(final long worldSeed, final int x, final int z) {
        this.setSeed(worldSeed);
        final long long5 = this.nextLong();
        final long long6 = this.nextLong();
        final long long7 = x * long5 ^ z * long6 ^ worldSeed;
        this.setSeed(long7);
        return long7;
    }
    
    public long setStructureSeed(final long worldSeed, final int x, final int z, final int seedModifier) {
        final long long6 = x * 341873128712L + z * 132897987541L + worldSeed + seedModifier;
        this.setSeed(long6);
        return long6;
    }
    
    public static Random create(final int x, final int z, final long worldSeed, final long localSeed) {
        return new Random(worldSeed + x * x * 4987142 + x * 5947611 + z * z * 4392871L + z * 389711 ^ localSeed);
    }
}
