package net.minecraft.world.chunk.light;

import net.minecraft.world.chunk.WorldNibbleStorage;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Arrays;
import net.minecraft.world.chunk.ColumnChunkNibbleArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.world.chunk.ChunkNibbleArray;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkProvider;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.Direction;

public class SkyLightStorage extends LightStorage<Data>
{
    private static final Direction[] DIRECTIONS_SKYLIGHT;
    private final LongSet l;
    private final LongSet m;
    private final LongSet n;
    private final LongSet o;
    private volatile boolean hasSkyLightUpdates;
    
    protected SkyLightStorage(final ChunkProvider chunkProvider) {
        super(LightType.SKY, chunkProvider, new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
        this.l = (LongSet)new LongOpenHashSet();
        this.m = (LongSet)new LongOpenHashSet();
        this.n = (LongSet)new LongOpenHashSet();
        this.o = (LongSet)new LongOpenHashSet();
    }
    
    @Override
    protected int getLight(long blockPos) {
        long long3 = ChunkSectionPos.toChunkLong(blockPos);
        int integer5 = ChunkSectionPos.unpackLongY(long3);
        final Data data6 = (Data)this.dataStorageUncached;
        final int integer6 = data6.heightMap.get(ChunkSectionPos.toLightStorageIndex(long3));
        if (integer6 == data6.defaultHeight || integer5 >= integer6) {
            return 15;
        }
        ChunkNibbleArray chunkNibbleArray8 = this.getDataForChunk(data6, long3);
        if (chunkNibbleArray8 == null) {
            blockPos = BlockPos.removeChunkSectionLocalY(blockPos);
            while (chunkNibbleArray8 == null) {
                long3 = ChunkSectionPos.offsetPacked(long3, Direction.UP);
                if (++integer5 >= integer6) {
                    return 15;
                }
                blockPos = BlockPos.add(blockPos, 0, 16, 0);
                chunkNibbleArray8 = this.getDataForChunk(data6, long3);
            }
        }
        return chunkNibbleArray8.get(ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }
    
    @Override
    protected void j(final long blockPos) {
        final int integer3 = ChunkSectionPos.unpackLongY(blockPos);
        if (((Data)this.dataStorage).defaultHeight > integer3) {
            ((Data)this.dataStorage).defaultHeight = integer3;
            ((Data)this.dataStorage).heightMap.defaultReturnValue(((Data)this.dataStorage).defaultHeight);
        }
        final long long4 = ChunkSectionPos.toLightStorageIndex(blockPos);
        final int integer4 = ((Data)this.dataStorage).heightMap.get(long4);
        if (integer4 < integer3 + 1) {
            ((Data)this.dataStorage).heightMap.put(long4, integer3 + 1);
            if (this.o.contains(long4)) {
                this.m.add(blockPos);
                this.n.remove(blockPos);
                if (integer4 > ((Data)this.dataStorage).defaultHeight) {
                    final long long5 = ChunkSectionPos.asLong(ChunkSectionPos.unpackLongX(blockPos), integer4 - 1, ChunkSectionPos.unpackLongZ(blockPos));
                    this.m.remove(long5);
                    this.n.add(long5);
                }
                this.checkForUpdates();
            }
        }
    }
    
    private void checkForUpdates() {
        this.hasSkyLightUpdates = (!this.m.isEmpty() || !this.n.isEmpty());
    }
    
    @Override
    protected void onChunkRemoved(final long long1) {
        final long long2 = ChunkSectionPos.toLightStorageIndex(long1);
        final boolean boolean5 = this.o.contains(long2);
        if (boolean5) {
            this.n.add(long1);
            this.m.remove(long1);
        }
        int integer6 = ChunkSectionPos.unpackLongY(long1);
        if (((Data)this.dataStorage).heightMap.get(long2) == integer6 + 1) {
            long long3;
            for (long3 = long1; !this.hasChunk(long3) && this.isAboveMinimumHeight(integer6); --integer6, long3 = ChunkSectionPos.offsetPacked(long3, Direction.DOWN)) {}
            if (this.hasChunk(long3)) {
                ((Data)this.dataStorage).heightMap.put(long2, integer6 + 1);
                if (boolean5) {
                    this.m.add(long3);
                    this.n.remove(long3);
                }
            }
            else {
                ((Data)this.dataStorage).heightMap.remove(long2);
            }
        }
        if (boolean5) {
            this.checkForUpdates();
        }
    }
    
    @Override
    protected void b(final long long1, final boolean boolean3) {
        if (boolean3 && this.o.add(long1)) {
            final int integer4 = ((Data)this.dataStorage).heightMap.get(long1);
            if (integer4 != ((Data)this.dataStorage).defaultHeight) {
                final long long2 = ChunkSectionPos.asLong(ChunkSectionPos.unpackLongX(long1), integer4 - 1, ChunkSectionPos.unpackLongZ(long1));
                this.m.add(long2);
                this.n.remove(long2);
                this.checkForUpdates();
            }
        }
        else if (!boolean3 && this.o.remove(long1)) {
            final int integer4 = ((Data)this.dataStorage).heightMap.get(long1);
            if (integer4 != ((Data)this.dataStorage).defaultHeight) {
                final long long2 = ChunkSectionPos.asLong(ChunkSectionPos.unpackLongX(long1), integer4 - 1, ChunkSectionPos.unpackLongZ(long1));
                this.n.add(long2);
                this.m.remove(long2);
                this.checkForUpdates();
            }
        }
    }
    
    @Override
    protected boolean hasLightUpdates() {
        return super.hasLightUpdates() || this.hasSkyLightUpdates;
    }
    
    @Override
    protected ChunkNibbleArray getDataForChunk(final long long1) {
        final ChunkNibbleArray chunkNibbleArray3 = (ChunkNibbleArray)this.toUpdate.get(long1);
        if (chunkNibbleArray3 != null) {
            return chunkNibbleArray3;
        }
        if (!this.n(long1)) {
            return new ChunkNibbleArray();
        }
        long long2;
        int integer6;
        for (long2 = ChunkSectionPos.offsetPacked(long1, Direction.UP), integer6 = ((Data)this.dataStorage).heightMap.get(ChunkSectionPos.toLightStorageIndex(long1)); integer6 != ((Data)this.dataStorage).defaultHeight && ChunkSectionPos.unpackLongY(long2) < integer6 && !this.hasChunk(long2); long2 = ChunkSectionPos.offsetPacked(long2, Direction.UP)) {}
        final ChunkNibbleArray chunkNibbleArray4 = ((Data)this.dataStorage).getDataForChunk(long2);
        if (chunkNibbleArray4 != null) {
            return new ChunkNibbleArray(new ColumnChunkNibbleArray(chunkNibbleArray4, 0).asByteArray());
        }
        if (this.m.contains(ChunkSectionPos.asLong(ChunkSectionPos.unpackLongX(long1), integer6 - 1, ChunkSectionPos.unpackLongZ(long1)))) {
            return new ChunkNibbleArray();
        }
        final ChunkNibbleArray chunkNibbleArray5 = new ChunkNibbleArray();
        Arrays.fill(chunkNibbleArray5.asByteArray(), (byte)(-1));
        return chunkNibbleArray5;
    }
    
    @Override
    protected void processUpdates(final ChunkLightProvider<Data, ?> lightProvider, final boolean doSkylight, final boolean doEdgeLightPropagation) {
        super.processUpdates(lightProvider, doSkylight, doEdgeLightPropagation);
        if (!doSkylight) {
            return;
        }
        if (!this.m.isEmpty()) {
            for (final long long5 : this.m) {
                final int integer7 = this.getLevel(long5);
                if (integer7 == 2) {
                    continue;
                }
                if (this.n.contains(long5) || !this.l.add(long5)) {
                    continue;
                }
                if (integer7 == 1) {
                    this.removeChunkData(lightProvider, long5);
                    if (this.g.add(long5)) {
                        ((Data)this.dataStorage).cloneChunkData(long5);
                    }
                    Arrays.fill(this.getDataForChunk(long5, true).asByteArray(), (byte)(-1));
                    final int integer8 = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(long5));
                    final int integer9 = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(long5));
                    final int integer10 = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(long5));
                    for (final Direction direction14 : SkyLightStorage.DIRECTIONS_SKYLIGHT) {
                        final long long6 = ChunkSectionPos.offsetPacked(long5, direction14);
                        if (this.n.contains(long6) || (!this.l.contains(long6) && !this.m.contains(long6))) {
                            if (this.hasChunk(long6)) {
                                for (int integer11 = 0; integer11 < 16; ++integer11) {
                                    for (int integer12 = 0; integer12 < 16; ++integer12) {
                                        long long7 = 0L;
                                        long long8 = 0L;
                                        switch (direction14) {
                                            case NORTH: {
                                                long7 = BlockPos.asLong(integer8 + integer11, integer9 + integer12, integer10);
                                                long8 = BlockPos.asLong(integer8 + integer11, integer9 + integer12, integer10 - 1);
                                                break;
                                            }
                                            case SOUTH: {
                                                long7 = BlockPos.asLong(integer8 + integer11, integer9 + integer12, integer10 + 16 - 1);
                                                long8 = BlockPos.asLong(integer8 + integer11, integer9 + integer12, integer10 + 16);
                                                break;
                                            }
                                            case WEST: {
                                                long7 = BlockPos.asLong(integer8, integer9 + integer11, integer10 + integer12);
                                                long8 = BlockPos.asLong(integer8 - 1, integer9 + integer11, integer10 + integer12);
                                                break;
                                            }
                                            default: {
                                                long7 = BlockPos.asLong(integer8 + 16 - 1, integer9 + integer11, integer10 + integer12);
                                                long8 = BlockPos.asLong(integer8 + 16, integer9 + integer11, integer10 + integer12);
                                                break;
                                            }
                                        }
                                        lightProvider.update(long7, long8, lightProvider.getPropagatedLevel(long7, long8, 0), true);
                                    }
                                }
                            }
                        }
                    }
                    for (int integer13 = 0; integer13 < 16; ++integer13) {
                        for (int integer14 = 0; integer14 < 16; ++integer14) {
                            final long long9 = BlockPos.asLong(ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(long5)) + integer13, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(long5)), ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(long5)) + integer14);
                            final long long6 = BlockPos.asLong(ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(long5)) + integer13, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(long5)) - 1, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(long5)) + integer14);
                            lightProvider.update(long9, long6, lightProvider.getPropagatedLevel(long9, long6, 0), true);
                        }
                    }
                }
                else {
                    for (int integer8 = 0; integer8 < 16; ++integer8) {
                        for (int integer9 = 0; integer9 < 16; ++integer9) {
                            final long long10 = BlockPos.asLong(ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(long5)) + integer8, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(long5)) + 16 - 1, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(long5)) + integer9);
                            lightProvider.update(Long.MAX_VALUE, long10, 0, true);
                        }
                    }
                }
            }
        }
        this.m.clear();
        if (!this.n.isEmpty()) {
            for (final long long5 : this.n) {
                if (this.l.remove(long5)) {
                    if (!this.hasChunk(long5)) {
                        continue;
                    }
                    for (int integer7 = 0; integer7 < 16; ++integer7) {
                        for (int integer8 = 0; integer8 < 16; ++integer8) {
                            final long long11 = BlockPos.asLong(ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(long5)) + integer7, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(long5)) + 16 - 1, ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(long5)) + integer8);
                            lightProvider.update(Long.MAX_VALUE, long11, 15, false);
                        }
                    }
                }
            }
        }
        this.n.clear();
        this.hasSkyLightUpdates = false;
    }
    
    protected boolean isAboveMinimumHeight(final int blockY) {
        return blockY >= ((Data)this.dataStorage).defaultHeight;
    }
    
    protected boolean l(final long long1) {
        final int integer3 = BlockPos.unpackLongY(long1);
        if ((integer3 & 0xF) != 0xF) {
            return false;
        }
        final long long2 = ChunkSectionPos.toChunkLong(long1);
        final long long3 = ChunkSectionPos.toLightStorageIndex(long2);
        if (!this.o.contains(long3)) {
            return false;
        }
        final int integer4 = ((Data)this.dataStorage).heightMap.get(long3);
        return ChunkSectionPos.fromChunkCoord(integer4) == integer3 + 16;
    }
    
    protected boolean m(final long long1) {
        final long long2 = ChunkSectionPos.toLightStorageIndex(long1);
        final int integer5 = ((Data)this.dataStorage).heightMap.get(long2);
        return integer5 == ((Data)this.dataStorage).defaultHeight || ChunkSectionPos.unpackLongY(long1) >= integer5;
    }
    
    protected boolean n(final long long1) {
        final long long2 = ChunkSectionPos.toLightStorageIndex(long1);
        return this.o.contains(long2);
    }
    
    static {
        DIRECTIONS_SKYLIGHT = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    }
    
    public static final class Data extends WorldNibbleStorage<Data>
    {
        private int defaultHeight;
        private final Long2IntOpenHashMap heightMap;
        
        public Data(final Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap, final Long2IntOpenHashMap long2IntOpenHashMap, final int integer) {
            super(long2ObjectOpenHashMap);
            (this.heightMap = long2IntOpenHashMap).defaultReturnValue(integer);
            this.defaultHeight = integer;
        }
        
        @Override
        public Data copy() {
            return new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)this.arraysByChunk.clone(), this.heightMap.clone(), this.defaultHeight);
        }
    }
}
