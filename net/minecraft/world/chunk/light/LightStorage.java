package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.LightType;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.util.SectionRelativeLevelPropagator;
import net.minecraft.world.chunk.WorldNibbleStorage;

public abstract class LightStorage<M extends WorldNibbleStorage<M>> extends SectionRelativeLevelPropagator
{
    protected static final ChunkNibbleArray EMPTY;
    private static final Direction[] DIRECTIONS;
    private final LightType lightType;
    private final ChunkProvider chunkProvider;
    protected final LongSet b;
    protected final LongSet c;
    protected final LongSet d;
    protected volatile M dataStorageUncached;
    protected final M dataStorage;
    protected final LongSet g;
    protected final LongSet toNotify;
    protected final Long2ObjectMap<ChunkNibbleArray> toUpdate;
    private final LongSet toRemove;
    protected volatile boolean hasLightUpdates;
    
    protected LightStorage(final LightType lightType, final ChunkProvider chunkProvider, final M lightData) {
        super(3, 16, 256);
        this.b = (LongSet)new LongOpenHashSet();
        this.c = (LongSet)new LongOpenHashSet();
        this.d = (LongSet)new LongOpenHashSet();
        this.g = (LongSet)new LongOpenHashSet();
        this.toNotify = (LongSet)new LongOpenHashSet();
        this.toUpdate = (Long2ObjectMap<ChunkNibbleArray>)new Long2ObjectOpenHashMap();
        this.toRemove = (LongSet)new LongOpenHashSet();
        this.lightType = lightType;
        this.chunkProvider = chunkProvider;
        this.dataStorage = lightData;
        (this.dataStorageUncached = lightData.copy()).disableCache();
    }
    
    protected boolean hasChunk(final long chunkPos) {
        return this.getDataForChunk(chunkPos, true) != null;
    }
    
    @Nullable
    protected ChunkNibbleArray getDataForChunk(final long chunkPos, final boolean cached) {
        return this.getDataForChunk(cached ? this.dataStorage : this.dataStorageUncached, chunkPos);
    }
    
    @Nullable
    protected ChunkNibbleArray getDataForChunk(final M storage, final long chunkPos) {
        return storage.getDataForChunk(chunkPos);
    }
    
    protected abstract int getLight(final long arg1);
    
    protected int get(final long blockPos) {
        final long long3 = ChunkSectionPos.toChunkLong(blockPos);
        final ChunkNibbleArray chunkNibbleArray5 = this.getDataForChunk(long3, true);
        return chunkNibbleArray5.get(ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }
    
    protected void set(final long blockPos, final int value) {
        final long long4 = ChunkSectionPos.toChunkLong(blockPos);
        if (this.g.add(long4)) {
            this.dataStorage.cloneChunkData(long4);
        }
        final ChunkNibbleArray chunkNibbleArray6 = this.getDataForChunk(long4, true);
        chunkNibbleArray6.set(ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(blockPos)), value);
        for (int integer7 = -1; integer7 <= 1; ++integer7) {
            for (int integer8 = -1; integer8 <= 1; ++integer8) {
                for (int integer9 = -1; integer9 <= 1; ++integer9) {
                    this.toNotify.add(ChunkSectionPos.toChunkLong(BlockPos.add(blockPos, integer8, integer9, integer7)));
                }
            }
        }
    }
    
    @Override
    protected int getLevel(final long id) {
        if (id == Long.MAX_VALUE) {
            return 2;
        }
        if (this.b.contains(id)) {
            return 0;
        }
        if (!this.toRemove.contains(id) && this.dataStorage.hasChunk(id)) {
            return 1;
        }
        return 2;
    }
    
    @Override
    protected int getInitialLevel(final long id) {
        if (this.c.contains(id)) {
            return 2;
        }
        if (this.b.contains(id) || this.d.contains(id)) {
            return 0;
        }
        return 2;
    }
    
    @Override
    protected void setLevel(final long id, final int level) {
        final int integer4 = this.getLevel(id);
        if (integer4 != 0 && level == 0) {
            this.b.add(id);
            this.d.remove(id);
        }
        if (integer4 == 0 && level != 0) {
            this.b.remove(id);
            this.c.remove(id);
        }
        if (integer4 >= 2 && level != 2) {
            if (this.toRemove.contains(id)) {
                this.toRemove.remove(id);
            }
            else {
                this.dataStorage.addForChunk(id, this.getDataForChunk(id));
                this.g.add(id);
                this.j(id);
                for (int integer5 = -1; integer5 <= 1; ++integer5) {
                    for (int integer6 = -1; integer6 <= 1; ++integer6) {
                        for (int integer7 = -1; integer7 <= 1; ++integer7) {
                            this.toNotify.add(ChunkSectionPos.toChunkLong(BlockPos.add(id, integer6, integer7, integer5)));
                        }
                    }
                }
            }
        }
        if (integer4 != 2 && level >= 2) {
            this.toRemove.add(id);
        }
        this.hasLightUpdates = !this.toRemove.isEmpty();
    }
    
    protected ChunkNibbleArray getDataForChunk(final long long1) {
        final ChunkNibbleArray chunkNibbleArray3 = (ChunkNibbleArray)this.toUpdate.get(long1);
        if (chunkNibbleArray3 != null) {
            return chunkNibbleArray3;
        }
        return new ChunkNibbleArray();
    }
    
    protected void removeChunkData(final ChunkLightProvider<?, ?> storage, final long blockChunkPos) {
        final int integer4 = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(blockChunkPos));
        final int integer5 = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(blockChunkPos));
        final int integer6 = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(blockChunkPos));
        for (int integer7 = 0; integer7 < 16; ++integer7) {
            for (int integer8 = 0; integer8 < 16; ++integer8) {
                for (int integer9 = 0; integer9 < 16; ++integer9) {
                    final long long10 = BlockPos.asLong(integer4 + integer7, integer5 + integer8, integer6 + integer9);
                    storage.remove(long10);
                }
            }
        }
    }
    
    protected boolean hasLightUpdates() {
        return this.hasLightUpdates;
    }
    
    protected void processUpdates(final ChunkLightProvider<M, ?> lightProvider, final boolean doSkylight, final boolean doEdgeLightPropagation) {
        if (!this.hasLightUpdates() && this.toUpdate.isEmpty()) {
            return;
        }
        for (final long long5 : this.toRemove) {
            this.toUpdate.remove(long5);
            this.removeChunkData(lightProvider, long5);
            this.dataStorage.removeChunk(long5);
        }
        this.dataStorage.clearCache();
        for (final long long5 : this.toRemove) {
            this.onChunkRemoved(long5);
        }
        this.toRemove.clear();
        this.hasLightUpdates = false;
        for (final Long2ObjectMap.Entry<ChunkNibbleArray> entry5 : this.toUpdate.long2ObjectEntrySet()) {
            final long long6 = entry5.getLongKey();
            if (!this.hasChunk(long6)) {
                continue;
            }
            final ChunkNibbleArray chunkNibbleArray8 = (ChunkNibbleArray)entry5.getValue();
            if (this.dataStorage.getDataForChunk(long6) == chunkNibbleArray8) {
                continue;
            }
            this.removeChunkData(lightProvider, long6);
            this.dataStorage.addForChunk(long6, chunkNibbleArray8);
            this.g.add(long6);
        }
        this.dataStorage.clearCache();
        if (!doEdgeLightPropagation) {
            for (final long long5 : this.toUpdate.keySet()) {
                if (!this.hasChunk(long5)) {
                    continue;
                }
                final int integer7 = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(long5));
                final int integer8 = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(long5));
                final int integer9 = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(long5));
                for (final Direction direction13 : LightStorage.DIRECTIONS) {
                    final long long7 = ChunkSectionPos.offsetPacked(long5, direction13);
                    if (!this.toUpdate.containsKey(long7)) {
                        if (this.hasChunk(long7)) {
                            for (int integer10 = 0; integer10 < 16; ++integer10) {
                                for (int integer11 = 0; integer11 < 16; ++integer11) {
                                    long long8 = 0L;
                                    long long9 = 0L;
                                    switch (direction13) {
                                        case DOWN: {
                                            long8 = BlockPos.asLong(integer7 + integer11, integer8, integer9 + integer10);
                                            long9 = BlockPos.asLong(integer7 + integer11, integer8 - 1, integer9 + integer10);
                                            break;
                                        }
                                        case UP: {
                                            long8 = BlockPos.asLong(integer7 + integer11, integer8 + 16 - 1, integer9 + integer10);
                                            long9 = BlockPos.asLong(integer7 + integer11, integer8 + 16, integer9 + integer10);
                                            break;
                                        }
                                        case NORTH: {
                                            long8 = BlockPos.asLong(integer7 + integer10, integer8 + integer11, integer9);
                                            long9 = BlockPos.asLong(integer7 + integer10, integer8 + integer11, integer9 - 1);
                                            break;
                                        }
                                        case SOUTH: {
                                            long8 = BlockPos.asLong(integer7 + integer10, integer8 + integer11, integer9 + 16 - 1);
                                            long9 = BlockPos.asLong(integer7 + integer10, integer8 + integer11, integer9 + 16);
                                            break;
                                        }
                                        case WEST: {
                                            long8 = BlockPos.asLong(integer7, integer8 + integer10, integer9 + integer11);
                                            long9 = BlockPos.asLong(integer7 - 1, integer8 + integer10, integer9 + integer11);
                                            break;
                                        }
                                        default: {
                                            long8 = BlockPos.asLong(integer7 + 16 - 1, integer8 + integer10, integer9 + integer11);
                                            long9 = BlockPos.asLong(integer7 + 16, integer8 + integer10, integer9 + integer11);
                                            break;
                                        }
                                    }
                                    lightProvider.update(long8, long9, lightProvider.getPropagatedLevel(long8, long9, lightProvider.getLevel(long8)), false);
                                    lightProvider.update(long9, long8, lightProvider.getPropagatedLevel(long9, long8, lightProvider.getLevel(long9)), false);
                                }
                            }
                        }
                    }
                }
            }
        }
        final ObjectIterator<Long2ObjectMap.Entry<ChunkNibbleArray>> objectIterator4 = (ObjectIterator<Long2ObjectMap.Entry<ChunkNibbleArray>>)this.toUpdate.long2ObjectEntrySet().iterator();
        while (objectIterator4.hasNext()) {
            final Long2ObjectMap.Entry<ChunkNibbleArray> entry5 = (Long2ObjectMap.Entry<ChunkNibbleArray>)objectIterator4.next();
            final long long6 = entry5.getLongKey();
            if (this.hasChunk(long6)) {
                objectIterator4.remove();
            }
        }
    }
    
    protected void j(final long blockPos) {
    }
    
    protected void onChunkRemoved(final long long1) {
    }
    
    protected void b(final long long1, final boolean boolean3) {
    }
    
    protected void scheduleToUpdate(final long blockChunkPos, final ChunkNibbleArray array) {
        this.toUpdate.put(blockChunkPos, array);
    }
    
    protected void scheduleChunkLightUpdate(final long chunkBlockPos, final boolean isEmpty) {
        final boolean boolean4 = this.b.contains(chunkBlockPos);
        if (!boolean4 && !isEmpty) {
            this.d.add(chunkBlockPos);
            this.update(Long.MAX_VALUE, chunkBlockPos, 0, true);
        }
        if (boolean4 && isEmpty) {
            this.c.add(chunkBlockPos);
            this.update(Long.MAX_VALUE, chunkBlockPos, 2, false);
        }
    }
    
    protected void updateAll() {
        if (this.hasLevelUpdates()) {
            this.updateAllRecursively(Integer.MAX_VALUE);
        }
    }
    
    protected void notifyChunkProvider() {
        if (!this.g.isEmpty()) {
            final M worldNibbleStorage1 = this.dataStorage.copy();
            worldNibbleStorage1.disableCache();
            this.dataStorageUncached = worldNibbleStorage1;
            this.g.clear();
        }
        if (!this.toNotify.isEmpty()) {
            final LongIterator longIterator1 = this.toNotify.iterator();
            while (longIterator1.hasNext()) {
                final long long2 = longIterator1.nextLong();
                this.chunkProvider.onLightUpdate(this.lightType, ChunkSectionPos.from(long2));
            }
            this.toNotify.clear();
        }
    }
    
    static {
        EMPTY = new ChunkNibbleArray();
        DIRECTIONS = Direction.values();
    }
}
