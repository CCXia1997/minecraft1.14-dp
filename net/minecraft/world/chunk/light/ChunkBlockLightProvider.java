package net.minecraft.world.chunk.light;

import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class ChunkBlockLightProvider extends ChunkLightProvider<BlockLightStorage.Data, BlockLightStorage>
{
    private static final Direction[] DIRECTIONS_BLOCKLIGHT;
    private final BlockPos.Mutable mutablePos;
    
    public ChunkBlockLightProvider(final ChunkProvider chunkProvider) {
        super(chunkProvider, LightType.BLOCK, new BlockLightStorage(chunkProvider));
        this.mutablePos = new BlockPos.Mutable();
    }
    
    private int getLightSourceLuminance(final long blockPos) {
        final int integer3 = BlockPos.unpackLongX(blockPos);
        final int integer4 = BlockPos.unpackLongY(blockPos);
        final int integer5 = BlockPos.unpackLongZ(blockPos);
        final BlockView blockView6 = this.chunkProvider.getChunk(integer3 >> 4, integer5 >> 4);
        if (blockView6 != null) {
            return blockView6.getLuminance(this.mutablePos.set(integer3, integer4, integer5));
        }
        return 0;
    }
    
    @Override
    protected int getPropagatedLevel(final long fromId, final long toId, final int currentLevel) {
        if (toId == Long.MAX_VALUE) {
            return 15;
        }
        if (fromId == Long.MAX_VALUE) {
            return currentLevel + 15 - this.getLightSourceLuminance(toId);
        }
        if (currentLevel >= 15) {
            return currentLevel;
        }
        final int integer6 = Integer.signum(BlockPos.unpackLongX(toId) - BlockPos.unpackLongX(fromId));
        final int integer7 = Integer.signum(BlockPos.unpackLongY(toId) - BlockPos.unpackLongY(fromId));
        final int integer8 = Integer.signum(BlockPos.unpackLongZ(toId) - BlockPos.unpackLongZ(fromId));
        final Direction direction9 = Direction.fromVector(integer6, integer7, integer8);
        if (direction9 == null) {
            return 15;
        }
        final AtomicInteger atomicInteger10 = new AtomicInteger();
        final VoxelShape voxelShape11 = this.a(toId, atomicInteger10);
        if (atomicInteger10.get() >= 15) {
            return 15;
        }
        final VoxelShape voxelShape12 = this.a(fromId, null);
        if (VoxelShapes.b(voxelShape12, voxelShape11, direction9)) {
            return 15;
        }
        return currentLevel + Math.max(1, atomicInteger10.get());
    }
    
    @Override
    protected void updateNeighborsRecursively(final long id, final int targetLevel, final boolean mergeAsMin) {
        final long long5 = ChunkSectionPos.toChunkLong(id);
        for (final Direction direction10 : ChunkBlockLightProvider.DIRECTIONS_BLOCKLIGHT) {
            final long long6 = BlockPos.offset(id, direction10);
            final long long7 = ChunkSectionPos.toChunkLong(long6);
            if (long5 == long7 || ((BlockLightStorage)this.lightStorage).hasChunk(long7)) {
                this.updateRecursively(id, long6, targetLevel, mergeAsMin);
            }
        }
    }
    
    @Override
    protected int getMergedLevel(final long id, final long sourceId, final int limitLevel) {
        int integer6 = limitLevel;
        if (Long.MAX_VALUE != sourceId) {
            final int integer7 = this.getPropagatedLevel(Long.MAX_VALUE, id, 0);
            if (integer6 > integer7) {
                integer6 = integer7;
            }
            if (integer6 == 0) {
                return integer6;
            }
        }
        final long long7 = ChunkSectionPos.toChunkLong(id);
        final ChunkNibbleArray chunkNibbleArray9 = ((BlockLightStorage)this.lightStorage).getDataForChunk(long7, true);
        for (final Direction direction13 : ChunkBlockLightProvider.DIRECTIONS_BLOCKLIGHT) {
            final long long8 = BlockPos.offset(id, direction13);
            if (long8 != sourceId) {
                final long long9 = ChunkSectionPos.toChunkLong(long8);
                ChunkNibbleArray chunkNibbleArray10;
                if (long7 == long9) {
                    chunkNibbleArray10 = chunkNibbleArray9;
                }
                else {
                    chunkNibbleArray10 = ((BlockLightStorage)this.lightStorage).getDataForChunk(long9, true);
                }
                if (chunkNibbleArray10 != null) {
                    final int integer8 = this.getPropagatedLevel(long8, id, this.getCurrentLevelFromArray(chunkNibbleArray10, long8));
                    if (integer6 > integer8) {
                        integer6 = integer8;
                    }
                    if (integer6 == 0) {
                        return integer6;
                    }
                }
            }
        }
        return integer6;
    }
    
    @Override
    public void a(final BlockPos blockPos, final int integer) {
        ((BlockLightStorage)this.lightStorage).updateAll();
        this.update(Long.MAX_VALUE, blockPos.asLong(), 15 - integer, true);
    }
    
    static {
        DIRECTIONS_BLOCKLIGHT = Direction.values();
    }
}
