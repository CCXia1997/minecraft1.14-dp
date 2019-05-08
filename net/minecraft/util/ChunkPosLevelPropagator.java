package net.minecraft.util;

import net.minecraft.world.chunk.ChunkPos;

public abstract class ChunkPosLevelPropagator extends LevelPropagator
{
    protected ChunkPosLevelPropagator(final int levelCount, final int initLevelCapacity, final int initTotalCapacity) {
        super(levelCount, initLevelCapacity, initTotalCapacity);
    }
    
    @Override
    protected boolean isInvalid(final long id) {
        return id == ChunkPos.INVALID;
    }
    
    @Override
    protected void updateNeighborsRecursively(final long id, final int targetLevel, final boolean mergeAsMin) {
        final ChunkPos chunkPos5 = new ChunkPos(id);
        final int integer6 = chunkPos5.x;
        final int integer7 = chunkPos5.z;
        for (int integer8 = -1; integer8 <= 1; ++integer8) {
            for (int integer9 = -1; integer9 <= 1; ++integer9) {
                final long long10 = ChunkPos.toLong(integer6 + integer8, integer7 + integer9);
                if (long10 != id) {
                    this.updateRecursively(id, long10, targetLevel, mergeAsMin);
                }
            }
        }
    }
    
    @Override
    protected int getMergedLevel(final long id, final long sourceId, final int limitLevel) {
        int integer6 = limitLevel;
        final ChunkPos chunkPos7 = new ChunkPos(id);
        final int integer7 = chunkPos7.x;
        final int integer8 = chunkPos7.z;
        for (int integer9 = -1; integer9 <= 1; ++integer9) {
            for (int integer10 = -1; integer10 <= 1; ++integer10) {
                long long12 = ChunkPos.toLong(integer7 + integer9, integer8 + integer10);
                if (long12 == id) {
                    long12 = ChunkPos.INVALID;
                }
                if (long12 != sourceId) {
                    final int integer11 = this.getPropagatedLevel(long12, id, this.getLevel(long12));
                    if (integer6 > integer11) {
                        integer6 = integer11;
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
    protected int getPropagatedLevel(final long fromId, final long toId, final int currentLevel) {
        if (fromId == ChunkPos.INVALID) {
            return this.getInitialLevel(toId);
        }
        return currentLevel + 1;
    }
    
    protected abstract int getInitialLevel(final long arg1);
    
    public void update(final long chunkPos, final int distance, final boolean playerPresent) {
        this.update(ChunkPos.INVALID, chunkPos, distance, playerPresent);
    }
}
