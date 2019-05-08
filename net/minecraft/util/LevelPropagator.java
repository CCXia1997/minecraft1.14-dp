package net.minecraft.util;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;

public abstract class LevelPropagator
{
    private final int levelCount;
    private final LongLinkedOpenHashSet[] levelToIds;
    private final Long2ByteFunction idToLevel;
    private int minLevel;
    private volatile boolean hasUpdates;
    
    protected LevelPropagator(final int levelCount, final int initLevelCapacity, final int initTotalCapacity) {
        if (levelCount >= 254) {
            throw new IllegalArgumentException("Level count must be < 254.");
        }
        this.levelCount = levelCount;
        this.levelToIds = new LongLinkedOpenHashSet[levelCount];
        for (int integer4 = 0; integer4 < levelCount; ++integer4) {
            this.levelToIds[integer4] = new LongLinkedOpenHashSet(initLevelCapacity, 0.5f) {
                protected void rehash(final int newN) {
                    if (newN > initLevelCapacity) {
                        super.rehash(newN);
                    }
                }
            };
        }
        (this.idToLevel = (Long2ByteFunction)new Long2ByteOpenHashMap(initTotalCapacity, 0.5f) {
            protected void rehash(final int newN) {
                if (newN > initTotalCapacity) {
                    super.rehash(newN);
                }
            }
        }).defaultReturnValue((byte)(-1));
        this.minLevel = levelCount;
    }
    
    private int min(final int a, final int b) {
        int integer3 = a;
        if (integer3 > b) {
            integer3 = b;
        }
        if (integer3 > this.levelCount - 1) {
            integer3 = this.levelCount - 1;
        }
        return integer3;
    }
    
    private void updateMinLevel(final int limitLevel) {
        final int integer2 = this.minLevel;
        this.minLevel = limitLevel;
        for (int integer3 = integer2 + 1; integer3 < limitLevel; ++integer3) {
            if (!this.levelToIds[integer3].isEmpty()) {
                this.minLevel = integer3;
                break;
            }
        }
    }
    
    protected void remove(final long id) {
        final int integer3 = this.idToLevel.get(id) & 0xFF;
        if (integer3 == 255) {
            return;
        }
        final int integer4 = this.getLevel(id);
        final int integer5 = this.min(integer4, integer3);
        this.removeFromLevel(id, integer5, this.levelCount, true);
        this.hasUpdates = (this.minLevel < this.levelCount);
    }
    
    private void removeFromLevel(final long id, final int level, final int maxLevel, final boolean removeFromLevelMap) {
        if (removeFromLevelMap) {
            this.idToLevel.remove(id);
        }
        this.levelToIds[level].remove(id);
        if (this.levelToIds[level].isEmpty() && this.minLevel == level) {
            this.updateMinLevel(maxLevel);
        }
    }
    
    private void add(final long id, int level, final int targetLevel) {
        if (level > this.levelCount - 1) {
            return;
        }
        level = Math.min(this.levelCount - 1, level);
        this.idToLevel.put(id, (byte)level);
        this.levelToIds[targetLevel].add(id);
        if (this.minLevel > targetLevel) {
            this.minLevel = targetLevel;
        }
    }
    
    protected void fullyUpdate(final long id) {
        this.update(id, id, this.levelCount - 1, false);
    }
    
    protected void update(final long causingId, final long id, final int level, final boolean mergeAsMin) {
        this.update(causingId, id, level, this.getLevel(id), this.idToLevel.get(id) & 0xFF, mergeAsMin);
        this.hasUpdates = (this.minLevel < this.levelCount);
    }
    
    private void update(final long causingId, final long id, int level, final int currentLevel, int previousLevel, final boolean mergeAsMin) {
        if (this.isInvalid(id)) {
            return;
        }
        if (level >= this.levelCount) {
            level = this.levelCount;
        }
        boolean boolean9;
        if (previousLevel == 255) {
            boolean9 = true;
            previousLevel = currentLevel;
        }
        else {
            boolean9 = false;
        }
        int integer10;
        if (mergeAsMin) {
            integer10 = Math.min(previousLevel, level);
        }
        else {
            integer10 = this.getMergedLevel(id, causingId, level);
        }
        final int integer11 = this.min(currentLevel, previousLevel);
        if (currentLevel != integer10) {
            final int integer12 = this.min(currentLevel, integer10);
            if (integer11 != integer12 && !boolean9) {
                this.removeFromLevel(id, integer11, integer12, false);
            }
            this.add(id, integer10, integer12);
        }
        else if (!boolean9) {
            this.removeFromLevel(id, integer11, this.levelCount, true);
        }
    }
    
    protected final void updateRecursively(final long sourceId, final long id, final int level, final boolean mergeAsMin) {
        final int integer7 = this.idToLevel.get(id) & 0xFF;
        final int integer8 = this.getPropagatedLevel(sourceId, id, level);
        if (mergeAsMin) {
            this.update(sourceId, id, integer8, this.getLevel(id), integer7, true);
        }
        else {
            boolean boolean10;
            int integer9;
            if (integer7 == 255) {
                boolean10 = true;
                integer9 = this.getLevel(id);
            }
            else {
                integer9 = integer7;
                boolean10 = false;
            }
            if (integer8 == integer9) {
                this.update(sourceId, id, this.levelCount - 1, boolean10 ? integer9 : this.getLevel(id), integer7, false);
            }
        }
    }
    
    protected final boolean hasLevelUpdates() {
        return this.hasUpdates;
    }
    
    protected final int updateAllRecursively(int maxSteps) {
        if (this.minLevel >= this.levelCount) {
            return maxSteps;
        }
        while (this.minLevel < this.levelCount && maxSteps > 0) {
            --maxSteps;
            final LongLinkedOpenHashSet longLinkedOpenHashSet2 = this.levelToIds[this.minLevel];
            final long long3 = longLinkedOpenHashSet2.removeFirstLong();
            final int integer5 = this.getLevel(long3);
            if (longLinkedOpenHashSet2.isEmpty()) {
                this.updateMinLevel(this.levelCount);
            }
            final int integer6 = this.idToLevel.remove(long3) & 0xFF;
            if (integer6 < integer5) {
                this.setLevel(long3, integer6);
                this.updateNeighborsRecursively(long3, integer6, true);
            }
            else {
                if (integer6 <= integer5) {
                    continue;
                }
                this.add(long3, integer6, this.min(this.levelCount - 1, integer6));
                this.setLevel(long3, this.levelCount - 1);
                this.updateNeighborsRecursively(long3, integer5, false);
            }
        }
        this.hasUpdates = (this.minLevel < this.levelCount);
        return maxSteps;
    }
    
    protected abstract boolean isInvalid(final long arg1);
    
    protected abstract int getMergedLevel(final long arg1, final long arg2, final int arg3);
    
    protected abstract void updateNeighborsRecursively(final long arg1, final int arg2, final boolean arg3);
    
    protected abstract int getLevel(final long arg1);
    
    protected abstract void setLevel(final long arg1, final int arg2);
    
    protected abstract int getPropagatedLevel(final long arg1, final long arg2, final int arg3);
}
