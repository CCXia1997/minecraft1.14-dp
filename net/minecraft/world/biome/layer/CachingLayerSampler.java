package net.minecraft.world.biome.layer;

import net.minecraft.world.chunk.ChunkPos;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;

public final class CachingLayerSampler implements LayerSampler
{
    private final LayerOperator operator;
    private final Long2IntLinkedOpenHashMap cache;
    private final int cacheCapacity;
    
    public CachingLayerSampler(final Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap, final int integer, final LayerOperator layerOperator) {
        this.cache = long2IntLinkedOpenHashMap;
        this.cacheCapacity = integer;
        this.operator = layerOperator;
    }
    
    @Override
    public int sample(final int x, final int z) {
        final long long3 = ChunkPos.toLong(x, z);
        synchronized (this.cache) {
            final int integer6 = this.cache.get(long3);
            if (integer6 != Integer.MIN_VALUE) {
                return integer6;
            }
            final int integer7 = this.operator.apply(x, z);
            this.cache.put(long3, integer7);
            if (this.cache.size() > this.cacheCapacity) {
                for (int integer8 = 0; integer8 < this.cacheCapacity / 16; ++integer8) {
                    this.cache.removeFirstInt();
                }
            }
            return integer7;
        }
    }
    
    public int getCapacity() {
        return this.cacheCapacity;
    }
}
