package net.minecraft.util;

import net.minecraft.util.math.ChunkSectionPos;

public abstract class SectionRelativeLevelPropagator extends LevelPropagator
{
    protected SectionRelativeLevelPropagator(final int levelCount, final int initLevelCapacity, final int initTotalCapacity) {
        super(levelCount, initLevelCapacity, initTotalCapacity);
    }
    
    @Override
    protected boolean isInvalid(final long id) {
        return id == Long.MAX_VALUE;
    }
    
    @Override
    protected void updateNeighborsRecursively(final long id, final int targetLevel, final boolean mergeAsMin) {
        for (int integer5 = -1; integer5 <= 1; ++integer5) {
            for (int integer6 = -1; integer6 <= 1; ++integer6) {
                for (int integer7 = -1; integer7 <= 1; ++integer7) {
                    final long long8 = ChunkSectionPos.offsetPacked(id, integer5, integer6, integer7);
                    if (long8 != id) {
                        this.updateRecursively(id, long8, targetLevel, mergeAsMin);
                    }
                }
            }
        }
    }
    
    @Override
    protected int getMergedLevel(final long id, final long sourceId, final int limitLevel) {
        int integer6 = limitLevel;
        for (int integer7 = -1; integer7 <= 1; ++integer7) {
            for (int integer8 = -1; integer8 <= 1; ++integer8) {
                for (int integer9 = -1; integer9 <= 1; ++integer9) {
                    long long10 = ChunkSectionPos.offsetPacked(id, integer7, integer8, integer9);
                    if (long10 == id) {
                        long10 = Long.MAX_VALUE;
                    }
                    if (long10 != sourceId) {
                        final int integer10 = this.getPropagatedLevel(long10, id, this.getLevel(long10));
                        if (integer6 > integer10) {
                            integer6 = integer10;
                        }
                        if (integer6 == 0) {
                            return integer6;
                        }
                    }
                }
            }
        }
        return integer6;
    }
    
    @Override
    protected int getPropagatedLevel(final long fromId, final long toId, final int currentLevel) {
        if (fromId == Long.MAX_VALUE) {
            return this.getInitialLevel(toId);
        }
        return currentLevel + 1;
    }
    
    protected abstract int getInitialLevel(final long arg1);
    
    public void update(final long id, final int limitLevel, final boolean mergeAsMin) {
        this.update(Long.MAX_VALUE, id, limitLevel, mergeAsMin);
    }
}
