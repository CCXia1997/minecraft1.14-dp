package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataOutputStream;
import java.io.DataOutput;
import javax.annotation.Nullable;
import java.io.DataInputStream;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.CompoundTag;
import java.io.IOException;
import net.minecraft.world.chunk.ChunkPos;
import java.io.File;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

public abstract class RegionBasedStorage implements AutoCloseable
{
    protected final Long2ObjectLinkedOpenHashMap<RegionFile> cachedRegionFiles;
    private final File directory;
    
    protected RegionBasedStorage(final File directory) {
        this.cachedRegionFiles = (Long2ObjectLinkedOpenHashMap<RegionFile>)new Long2ObjectLinkedOpenHashMap();
        this.directory = directory;
    }
    
    private RegionFile getRegionFile(final ChunkPos pos) throws IOException {
        final long long2 = ChunkPos.toLong(pos.getRegionX(), pos.getRegionZ());
        final RegionFile regionFile4 = (RegionFile)this.cachedRegionFiles.getAndMoveToFirst(long2);
        if (regionFile4 != null) {
            return regionFile4;
        }
        if (this.cachedRegionFiles.size() >= 256) {
            this.cachedRegionFiles.removeLast();
        }
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        final File file5 = new File(this.directory, "r." + pos.getRegionX() + "." + pos.getRegionZ() + ".mca");
        final RegionFile regionFile5 = new RegionFile(file5);
        this.cachedRegionFiles.putAndMoveToFirst(long2, regionFile5);
        return regionFile5;
    }
    
    @Nullable
    public CompoundTag getTagAt(final ChunkPos pos) throws IOException {
        final RegionFile regionFile2 = this.getRegionFile(pos);
        try (final DataInputStream dataInputStream3 = regionFile2.getChunkDataInputStream(pos)) {
            if (dataInputStream3 == null) {
                return null;
            }
            return NbtIo.read(dataInputStream3);
        }
    }
    
    protected void setTagAt(final ChunkPos pos, final CompoundTag tag) throws IOException {
        final RegionFile regionFile3 = this.getRegionFile(pos);
        try (final DataOutputStream dataOutputStream4 = regionFile3.getChunkDataOutputStream(pos)) {
            NbtIo.write(tag, dataOutputStream4);
        }
    }
    
    @Override
    public void close() throws IOException {
        for (final RegionFile regionFile2 : this.cachedRegionFiles.values()) {
            regionFile2.close();
        }
    }
}
