package net.minecraft.world.chunk;

import java.util.Spliterator;
import java.util.stream.StreamSupport;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import java.util.Spliterators;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class ChunkPos
{
    public static final long INVALID;
    public final int x;
    public final int z;
    
    public ChunkPos(final int x, final int z) {
        this.x = x;
        this.z = z;
    }
    
    public ChunkPos(final BlockPos pos) {
        this.x = pos.getX() >> 4;
        this.z = pos.getZ() >> 4;
    }
    
    public ChunkPos(final long pos) {
        this.x = (int)pos;
        this.z = (int)(pos >> 32);
    }
    
    public long toLong() {
        return toLong(this.x, this.z);
    }
    
    public static long toLong(final int chunkX, final int chunkZ) {
        return ((long)chunkX & 0xFFFFFFFFL) | ((long)chunkZ & 0xFFFFFFFFL) << 32;
    }
    
    public static int getPackedX(final long pos) {
        return (int)(pos & 0xFFFFFFFFL);
    }
    
    public static int getPackedZ(final long pos) {
        return (int)(pos >>> 32 & 0xFFFFFFFFL);
    }
    
    @Override
    public int hashCode() {
        final int integer1 = 1664525 * this.x + 1013904223;
        final int integer2 = 1664525 * (this.z ^ 0xDEADBEEF) + 1013904223;
        return integer1 ^ integer2;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ChunkPos) {
            final ChunkPos chunkPos2 = (ChunkPos)o;
            return this.x == chunkPos2.x && this.z == chunkPos2.z;
        }
        return false;
    }
    
    public int getStartX() {
        return this.x << 4;
    }
    
    public int getStartZ() {
        return this.z << 4;
    }
    
    public int getEndX() {
        return (this.x << 4) + 15;
    }
    
    public int getEndZ() {
        return (this.z << 4) + 15;
    }
    
    public int getRegionX() {
        return this.x >> 5;
    }
    
    public int getRegionZ() {
        return this.z >> 5;
    }
    
    public int getRegionRelativeX() {
        return this.x & 0x1F;
    }
    
    public int getRegionRelativeZ() {
        return this.z & 0x1F;
    }
    
    public BlockPos toBlockPos(final int chunkRelativeX, final int chunkRelativeY, final int chunkRelativeZ) {
        return new BlockPos((this.x << 4) + chunkRelativeX, chunkRelativeY, (this.z << 4) + chunkRelativeZ);
    }
    
    @Override
    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }
    
    public BlockPos getCenterBlockPos() {
        return new BlockPos(this.x << 4, 0, this.z << 4);
    }
    
    public static Stream<ChunkPos> stream(final ChunkPos center, final int radius) {
        return stream(new ChunkPos(center.x - radius, center.z - radius), new ChunkPos(center.x + radius, center.z + radius));
    }
    
    public static Stream<ChunkPos> stream(final ChunkPos pos1, final ChunkPos pos2) {
        final int integer3 = Math.abs(pos1.x - pos2.x) + 1;
        final int integer4 = Math.abs(pos1.z - pos2.z) + 1;
        final int integer5 = (pos1.x < pos2.x) ? 1 : -1;
        final int integer6 = (pos1.z < pos2.z) ? 1 : -1;
        return StreamSupport.<ChunkPos>stream((Spliterator<ChunkPos>)new Spliterators.AbstractSpliterator<ChunkPos>((long)(integer3 * integer4), 64) {
            @Nullable
            private ChunkPos e;
            
            @Override
            public boolean tryAdvance(final Consumer<? super ChunkPos> consumer) {
                if (this.e == null) {
                    this.e = pos1;
                }
                else {
                    final int integer2 = this.e.x;
                    final int integer3 = this.e.z;
                    if (integer2 == pos2.x) {
                        if (integer3 == pos2.z) {
                            return false;
                        }
                        this.e = new ChunkPos(pos1.x, integer3 + integer6);
                    }
                    else {
                        this.e = new ChunkPos(integer2 + integer5, integer3);
                    }
                }
                consumer.accept(this.e);
                return true;
            }
        }, false);
    }
    
    static {
        INVALID = toLong(1875016, 1875016);
    }
}
