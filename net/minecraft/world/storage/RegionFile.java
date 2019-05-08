package net.minecraft.world.storage;

import java.io.ByteArrayOutputStream;
import net.minecraft.util.SystemUtil;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.DataOutputStream;
import javax.annotation.Nullable;
import java.util.zip.InflaterInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import net.minecraft.world.chunk.ChunkPos;
import java.io.IOException;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.io.RandomAccessFile;

public class RegionFile implements AutoCloseable
{
    private static final byte[] EMPTY_SECTOR;
    private final RandomAccessFile file;
    private final int[] offsets;
    private final int[] chunkTimestamps;
    private final List<Boolean> sectorFree;
    
    public RegionFile(final File file) throws IOException {
        this.offsets = new int[1024];
        this.chunkTimestamps = new int[1024];
        this.file = new RandomAccessFile(file, "rw");
        if (this.file.length() < 4096L) {
            this.file.write(RegionFile.EMPTY_SECTOR);
            this.file.write(RegionFile.EMPTY_SECTOR);
        }
        if ((this.file.length() & 0xFFFL) != 0x0L) {
            for (int integer2 = 0; integer2 < (this.file.length() & 0xFFFL); ++integer2) {
                this.file.write(0);
            }
        }
        int integer2 = (int)this.file.length() / 4096;
        this.sectorFree = Lists.newArrayListWithCapacity(integer2);
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            this.sectorFree.add(true);
        }
        this.sectorFree.set(0, false);
        this.sectorFree.set(1, false);
        this.file.seek(0L);
        for (int integer3 = 0; integer3 < 1024; ++integer3) {
            final int integer4 = this.file.readInt();
            this.offsets[integer3] = integer4;
            if (integer4 != 0 && (integer4 >> 8) + (integer4 & 0xFF) <= this.sectorFree.size()) {
                for (int integer5 = 0; integer5 < (integer4 & 0xFF); ++integer5) {
                    this.sectorFree.set((integer4 >> 8) + integer5, false);
                }
            }
        }
        for (int integer3 = 0; integer3 < 1024; ++integer3) {
            final int integer4 = this.file.readInt();
            this.chunkTimestamps[integer3] = integer4;
        }
    }
    
    @Nullable
    public synchronized DataInputStream getChunkDataInputStream(final ChunkPos pos) {
        try {
            final int integer2 = this.getOffset(pos);
            if (integer2 == 0) {
                return null;
            }
            final int integer3 = integer2 >> 8;
            final int integer4 = integer2 & 0xFF;
            if (integer3 + integer4 > this.sectorFree.size()) {
                return null;
            }
            this.file.seek(integer3 * 4096);
            final int integer5 = this.file.readInt();
            if (integer5 > 4096 * integer4) {
                return null;
            }
            if (integer5 <= 0) {
                return null;
            }
            final byte byte6 = this.file.readByte();
            if (byte6 == 1) {
                final byte[] arr7 = new byte[integer5 - 1];
                this.file.read(arr7);
                return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(arr7))));
            }
            if (byte6 == 2) {
                final byte[] arr7 = new byte[integer5 - 1];
                this.file.read(arr7);
                return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(arr7))));
            }
            return null;
        }
        catch (IOException iOException2) {
            return null;
        }
    }
    
    public boolean isChunkPresent(final ChunkPos pos) {
        final int integer2 = this.getOffset(pos);
        if (integer2 == 0) {
            return false;
        }
        final int integer3 = integer2 >> 8;
        final int integer4 = integer2 & 0xFF;
        if (integer3 + integer4 > this.sectorFree.size()) {
            return false;
        }
        try {
            this.file.seek(integer3 * 4096);
            final int integer5 = this.file.readInt();
            if (integer5 > 4096 * integer4) {
                return false;
            }
            if (integer5 <= 0) {
                return false;
            }
        }
        catch (IOException iOException5) {
            return false;
        }
        return true;
    }
    
    public DataOutputStream getChunkDataOutputStream(final ChunkPos chunkPos) {
        return new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new ChunkBuffer(chunkPos))));
    }
    
    protected synchronized void write(final ChunkPos chunkPos, final byte[] arr, final int integer) {
        try {
            final int integer2 = this.getOffset(chunkPos);
            int integer3 = integer2 >> 8;
            final int integer4 = integer2 & 0xFF;
            final int integer5 = (integer + 5) / 4096 + 1;
            if (integer5 >= 256) {
                throw new RuntimeException(String.format("Too big to save, %d > 1048576", integer));
            }
            if (integer3 != 0 && integer4 == integer5) {
                this.write(integer3, arr, integer);
            }
            else {
                for (int integer6 = 0; integer6 < integer4; ++integer6) {
                    this.sectorFree.set(integer3 + integer6, true);
                }
                int integer6 = this.sectorFree.indexOf(true);
                int integer7 = 0;
                if (integer6 != -1) {
                    for (int integer8 = integer6; integer8 < this.sectorFree.size(); ++integer8) {
                        if (integer7 != 0) {
                            if (this.sectorFree.get(integer8)) {
                                ++integer7;
                            }
                            else {
                                integer7 = 0;
                            }
                        }
                        else if (this.sectorFree.get(integer8)) {
                            integer6 = integer8;
                            integer7 = 1;
                        }
                        if (integer7 >= integer5) {
                            break;
                        }
                    }
                }
                if (integer7 >= integer5) {
                    integer3 = integer6;
                    this.setOffset(chunkPos, integer3 << 8 | integer5);
                    for (int integer8 = 0; integer8 < integer5; ++integer8) {
                        this.sectorFree.set(integer3 + integer8, false);
                    }
                    this.write(integer3, arr, integer);
                }
                else {
                    this.file.seek(this.file.length());
                    integer3 = this.sectorFree.size();
                    for (int integer8 = 0; integer8 < integer5; ++integer8) {
                        this.file.write(RegionFile.EMPTY_SECTOR);
                        this.sectorFree.add(false);
                    }
                    this.write(integer3, arr, integer);
                    this.setOffset(chunkPos, integer3 << 8 | integer5);
                }
            }
            this.setTimestamp(chunkPos, (int)(SystemUtil.getEpochTimeMs() / 1000L));
        }
        catch (IOException iOException4) {
            iOException4.printStackTrace();
        }
    }
    
    private void write(final int sectorNumber, final byte[] data, final int integer3) throws IOException {
        this.file.seek(sectorNumber * 4096);
        this.file.writeInt(integer3 + 1);
        this.file.writeByte(2);
        this.file.write(data, 0, integer3);
    }
    
    private int getOffset(final ChunkPos pos) {
        return this.offsets[this.getPackedRegionRelativePosition(pos)];
    }
    
    public boolean hasChunk(final ChunkPos pos) {
        return this.getOffset(pos) != 0;
    }
    
    private void setOffset(final ChunkPos chunkPos, final int integer) throws IOException {
        final int integer2 = this.getPackedRegionRelativePosition(chunkPos);
        this.offsets[integer2] = integer;
        this.file.seek(integer2 * 4);
        this.file.writeInt(integer);
    }
    
    private int getPackedRegionRelativePosition(final ChunkPos pos) {
        return pos.getRegionRelativeX() + pos.getRegionRelativeZ() * 32;
    }
    
    private void setTimestamp(final ChunkPos chunkPos, final int integer) throws IOException {
        final int integer2 = this.getPackedRegionRelativePosition(chunkPos);
        this.chunkTimestamps[integer2] = integer;
        this.file.seek(4096 + integer2 * 4);
        this.file.writeInt(integer);
    }
    
    @Override
    public void close() throws IOException {
        this.file.close();
    }
    
    static {
        EMPTY_SECTOR = new byte[4096];
    }
    
    class ChunkBuffer extends ByteArrayOutputStream
    {
        private final ChunkPos pos;
        
        public ChunkBuffer(final ChunkPos chunkPos) {
            super(8096);
            this.pos = chunkPos;
        }
        
        @Override
        public void close() {
            RegionFile.this.write(this.pos, this.buf, this.count);
        }
    }
}
