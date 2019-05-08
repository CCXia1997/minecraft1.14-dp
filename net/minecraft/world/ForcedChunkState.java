package net.minecraft.world;

import net.minecraft.nbt.CompoundTag;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public class ForcedChunkState extends PersistentState
{
    private LongSet chunks;
    
    public ForcedChunkState() {
        super("chunks");
        this.chunks = (LongSet)new LongOpenHashSet();
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        this.chunks = (LongSet)new LongOpenHashSet(compoundTag.getLongArray("Forced"));
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        compoundTag.putLongArray("Forced", this.chunks.toLongArray());
        return compoundTag;
    }
    
    public LongSet getChunks() {
        return this.chunks;
    }
}
