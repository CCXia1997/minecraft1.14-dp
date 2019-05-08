package net.minecraft.world.biome.layer;

import java.util.Random;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;

public class CachingLayerContext implements LayerSampleContext<CachingLayerSampler>
{
    private final Long2IntLinkedOpenHashMap cache;
    private final int d;
    protected long a;
    protected PerlinNoiseSampler noiseSampler;
    private long worldSeed;
    private long localSeed;
    
    public CachingLayerContext(final int integer, final long seed, final long long4) {
        this.a = long4;
        this.a *= this.a * 6364136223846793005L + 1442695040888963407L;
        this.a += long4;
        this.a *= this.a * 6364136223846793005L + 1442695040888963407L;
        this.a += long4;
        this.a *= this.a * 6364136223846793005L + 1442695040888963407L;
        this.a += long4;
        (this.cache = new Long2IntLinkedOpenHashMap(16, 0.25f)).defaultReturnValue(Integer.MIN_VALUE);
        this.d = integer;
        this.initWorldSeed(seed);
    }
    
    @Override
    public CachingLayerSampler createSampler(final LayerOperator operator) {
        return new CachingLayerSampler(this.cache, this.d, operator);
    }
    
    @Override
    public CachingLayerSampler createSampler(final LayerOperator operator, final CachingLayerSampler parent) {
        return new CachingLayerSampler(this.cache, Math.min(1024, parent.getCapacity() * 4), operator);
    }
    
    @Override
    public CachingLayerSampler createSampler(final LayerOperator operator, final CachingLayerSampler cachingLayerSampler2, final CachingLayerSampler cachingLayerSampler3) {
        return new CachingLayerSampler(this.cache, Math.min(1024, Math.max(cachingLayerSampler2.getCapacity(), cachingLayerSampler3.getCapacity()) * 4), operator);
    }
    
    public void initWorldSeed(final long long1) {
        this.worldSeed = long1;
        this.worldSeed *= this.worldSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldSeed += this.a;
        this.worldSeed *= this.worldSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldSeed += this.a;
        this.worldSeed *= this.worldSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldSeed += this.a;
        this.noiseSampler = new PerlinNoiseSampler(new Random(long1));
    }
    
    @Override
    public void initSeed(final long x, final long y) {
        this.localSeed = this.worldSeed;
        this.localSeed *= this.localSeed * 6364136223846793005L + 1442695040888963407L;
        this.localSeed += x;
        this.localSeed *= this.localSeed * 6364136223846793005L + 1442695040888963407L;
        this.localSeed += y;
        this.localSeed *= this.localSeed * 6364136223846793005L + 1442695040888963407L;
        this.localSeed += x;
        this.localSeed *= this.localSeed * 6364136223846793005L + 1442695040888963407L;
        this.localSeed += y;
    }
    
    @Override
    public int nextInt(final int bound) {
        int integer2 = (int)((this.localSeed >> 24) % bound);
        if (integer2 < 0) {
            integer2 += bound;
        }
        this.localSeed *= this.localSeed * 6364136223846793005L + 1442695040888963407L;
        this.localSeed += this.worldSeed;
        return integer2;
    }
    
    @Override
    public PerlinNoiseSampler getNoiseSampler() {
        return this.noiseSampler;
    }
}
