package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public abstract class WorldNibbleStorage<M extends WorldNibbleStorage<M>>
{
    private final long[] cachedCoords;
    private final ChunkNibbleArray[] cachedData;
    private boolean hasCache;
    protected final Long2ObjectOpenHashMap<ChunkNibbleArray> arraysByChunk;
    
    protected WorldNibbleStorage(final Long2ObjectOpenHashMap<ChunkNibbleArray> map) {
        this.cachedCoords = new long[2];
        this.cachedData = new ChunkNibbleArray[2];
        this.arraysByChunk = map;
        this.clearCache();
        this.hasCache = true;
    }
    
    public abstract M copy();
    
    public void cloneChunkData(final long chunkPos) {
        this.arraysByChunk.put(chunkPos, ((ChunkNibbleArray)this.arraysByChunk.get(chunkPos)).copy());
        this.clearCache();
    }
    
    public boolean hasChunk(final long chunkPos) {
        return this.arraysByChunk.containsKey(chunkPos);
    }
    
    @Nullable
    public ChunkNibbleArray getDataForChunk(final long chunkPos) {
        if (this.hasCache) {
            for (int integer3 = 0; integer3 < 2; ++integer3) {
                if (chunkPos == this.cachedCoords[integer3]) {
                    return this.cachedData[integer3];
                }
            }
        }
        final ChunkNibbleArray chunkNibbleArray3 = (ChunkNibbleArray)this.arraysByChunk.get(chunkPos);
        if (chunkNibbleArray3 != null) {
            if (this.hasCache) {
                for (int integer4 = 1; integer4 > 0; --integer4) {
                    this.cachedCoords[integer4] = this.cachedCoords[integer4 - 1];
                    this.cachedData[integer4] = this.cachedData[integer4 - 1];
                }
                this.cachedCoords[0] = chunkPos;
                this.cachedData[0] = chunkNibbleArray3;
            }
            return chunkNibbleArray3;
        }
        return null;
    }
    
    public void removeChunk(final long chunkPos) {
        this.arraysByChunk.remove(chunkPos);
    }
    
    public void addForChunk(final long chunkPos, final ChunkNibbleArray data) {
        this.arraysByChunk.put(chunkPos, data);
    }
    
    public void clearCache() {
        for (int integer1 = 0; integer1 < 2; ++integer1) {
            this.cachedCoords[integer1] = Long.MAX_VALUE;
            this.cachedData[integer1] = null;
        }
    }
    
    public void disableCache() {
        this.hasCache = false;
    }
}
