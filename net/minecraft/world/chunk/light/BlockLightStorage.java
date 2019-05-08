package net.minecraft.world.chunk.light;

import net.minecraft.world.chunk.WorldNibbleStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkNibbleArray;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkProvider;

public class BlockLightStorage extends LightStorage<Data>
{
    protected BlockLightStorage(final ChunkProvider chunkProvider) {
        super(LightType.BLOCK, chunkProvider, new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)new Long2ObjectOpenHashMap()));
    }
    
    @Override
    protected int getLight(final long blockPos) {
        final long long3 = ChunkSectionPos.toChunkLong(blockPos);
        final ChunkNibbleArray chunkNibbleArray5 = this.getDataForChunk(long3, false);
        if (chunkNibbleArray5 == null) {
            return 0;
        }
        return chunkNibbleArray5.get(ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }
    
    public static final class Data extends WorldNibbleStorage<Data>
    {
        public Data(final Long2ObjectOpenHashMap<ChunkNibbleArray> map) {
            super(map);
        }
        
        @Override
        public Data copy() {
            return new Data((Long2ObjectOpenHashMap<ChunkNibbleArray>)this.arraysByChunk.clone());
        }
    }
}
