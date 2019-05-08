package net.minecraft.util.math;

import java.util.Spliterator;
import java.util.stream.StreamSupport;
import java.util.function.Consumer;
import net.minecraft.util.CuboidBlockIterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.ChunkPos;

public class ChunkSectionPos extends Vec3i
{
    private ChunkSectionPos(final int x, final int y, final int z) {
        super(x, y, z);
    }
    
    public static ChunkSectionPos from(final int x, final int y, final int z) {
        return new ChunkSectionPos(x, y, z);
    }
    
    public static ChunkSectionPos from(final BlockPos pos) {
        return new ChunkSectionPos(toChunkCoord(pos.getX()), toChunkCoord(pos.getY()), toChunkCoord(pos.getZ()));
    }
    
    public static ChunkSectionPos from(final ChunkPos chunkPos, final int y) {
        return new ChunkSectionPos(chunkPos.x, y, chunkPos.z);
    }
    
    public static ChunkSectionPos from(final Entity entity) {
        return new ChunkSectionPos(toChunkCoord(MathHelper.floor(entity.x)), toChunkCoord(MathHelper.floor(entity.y)), toChunkCoord(MathHelper.floor(entity.z)));
    }
    
    public static ChunkSectionPos from(final long packed) {
        return new ChunkSectionPos(unpackLongX(packed), unpackLongY(packed), unpackLongZ(packed));
    }
    
    public static long offsetPacked(final long packed, final Direction direction) {
        return offsetPacked(packed, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }
    
    public static long offsetPacked(final long packed, final int x, final int y, final int z) {
        return asLong(unpackLongX(packed) + x, unpackLongY(packed) + y, unpackLongZ(packed) + z);
    }
    
    public static int toChunkCoord(final int coord) {
        return coord >> 4;
    }
    
    public static int toLocalCoord(final int coord) {
        return coord & 0xF;
    }
    
    public static short packToShort(final BlockPos blockPos) {
        final int integer2 = toLocalCoord(blockPos.getX());
        final int integer3 = toLocalCoord(blockPos.getY());
        final int integer4 = toLocalCoord(blockPos.getZ());
        return (short)(integer2 << 8 | integer4 << 4 | integer3);
    }
    
    public static int fromChunkCoord(final int coord) {
        return coord << 4;
    }
    
    public static int unpackLongX(final long packed) {
        return (int)(packed << 0 >> 42);
    }
    
    public static int unpackLongY(final long packed) {
        return (int)(packed << 44 >> 44);
    }
    
    public static int unpackLongZ(final long packed) {
        return (int)(packed << 22 >> 42);
    }
    
    public int getChunkX() {
        return this.getX();
    }
    
    public int getChunkY() {
        return this.getY();
    }
    
    public int getChunkZ() {
        return this.getZ();
    }
    
    public int getMinX() {
        return this.getChunkX() << 4;
    }
    
    public int getMinY() {
        return this.getChunkY() << 4;
    }
    
    public int getMinZ() {
        return this.getChunkZ() << 4;
    }
    
    public int getMaxX() {
        return (this.getChunkX() << 4) + 15;
    }
    
    public int getMaxY() {
        return (this.getChunkY() << 4) + 15;
    }
    
    public int getMaxZ() {
        return (this.getChunkZ() << 4) + 15;
    }
    
    public static long toChunkLong(final long globalLong) {
        return asLong(toChunkCoord(BlockPos.unpackLongX(globalLong)), toChunkCoord(BlockPos.unpackLongY(globalLong)), toChunkCoord(BlockPos.unpackLongZ(globalLong)));
    }
    
    public static long toLightStorageIndex(final long pos) {
        return pos & 0xFFFFFFFFFFF00000L;
    }
    
    public BlockPos getMinPos() {
        return new BlockPos(fromChunkCoord(this.getChunkX()), fromChunkCoord(this.getChunkY()), fromChunkCoord(this.getChunkZ()));
    }
    
    public BlockPos getCenterPos() {
        final int integer1 = 8;
        return this.getMinPos().add(8, 8, 8);
    }
    
    public ChunkPos toChunkPos() {
        return new ChunkPos(this.getChunkX(), this.getChunkZ());
    }
    
    public static long asLong(final int x, final int y, final int z) {
        long long4 = 0L;
        long4 |= ((long)x & 0x3FFFFFL) << 42;
        long4 |= ((long)y & 0xFFFFFL) << 0;
        long4 |= ((long)z & 0x3FFFFFL) << 20;
        return long4;
    }
    
    public long asLong() {
        return asLong(this.getChunkX(), this.getChunkY(), this.getChunkZ());
    }
    
    public Stream<BlockPos> streamBlocks() {
        return BlockPos.stream(this.getMinX(), this.getMinY(), this.getMinZ(), this.getMaxX(), this.getMaxY(), this.getMaxZ());
    }
    
    public static Stream<ChunkSectionPos> stream(final ChunkSectionPos center, final int radius) {
        final int integer3 = center.getChunkX();
        final int integer4 = center.getChunkY();
        final int integer5 = center.getChunkZ();
        return stream(integer3 - radius, integer4 - radius, integer5 - radius, integer3 + radius, integer4 + radius, integer5 + radius);
    }
    
    public static Stream<ChunkSectionPos> stream(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        return StreamSupport.<ChunkSectionPos>stream((Spliterator<ChunkSectionPos>)new Spliterators.AbstractSpliterator<ChunkSectionPos>((long)((maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)), 64) {
            final CuboidBlockIterator iterator = new CuboidBlockIterator(minX, minY, minZ, maxX, maxY, maxZ);
            
            @Override
            public boolean tryAdvance(final Consumer<? super ChunkSectionPos> consumer) {
                if (this.iterator.step()) {
                    consumer.accept(new ChunkSectionPos(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ(), null));
                    return true;
                }
                return false;
            }
        }, false);
    }
}
