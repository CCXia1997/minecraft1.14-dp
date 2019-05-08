package net.minecraft.world;

import net.minecraft.nbt.CompoundTag;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public class ChunkUpdateState extends PersistentState
{
    private LongSet all;
    private LongSet remaining;
    
    public ChunkUpdateState(final String key) {
        super(key);
        this.all = (LongSet)new LongOpenHashSet();
        this.remaining = (LongSet)new LongOpenHashSet();
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        this.all = (LongSet)new LongOpenHashSet(compoundTag.getLongArray("All"));
        this.remaining = (LongSet)new LongOpenHashSet(compoundTag.getLongArray("Remaining"));
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        compoundTag.putLongArray("All", this.all.toLongArray());
        compoundTag.putLongArray("Remaining", this.remaining.toLongArray());
        return compoundTag;
    }
    
    public void add(final long long1) {
        this.all.add(long1);
        this.remaining.add(long1);
    }
    
    public boolean contains(final long long1) {
        return this.all.contains(long1);
    }
    
    public boolean isRemaining(final long long1) {
        return this.remaining.contains(long1);
    }
    
    public void markResolved(final long long1) {
        this.remaining.remove(long1);
    }
    
    public LongSet getAll() {
        return this.all;
    }
}
