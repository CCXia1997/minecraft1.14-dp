package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class IdCountsState extends PersistentState
{
    private final Object2IntMap<String> idCounts;
    
    public IdCountsState() {
        super("idcounts");
        (this.idCounts = (Object2IntMap<String>)new Object2IntOpenHashMap()).defaultReturnValue(-1);
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        this.idCounts.clear();
        for (final String string3 : compoundTag.getKeys()) {
            if (compoundTag.containsKey(string3, 99)) {
                this.idCounts.put(string3, compoundTag.getInt(string3));
            }
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        for (final Object2IntMap.Entry<String> entry3 : this.idCounts.object2IntEntrySet()) {
            compoundTag.putInt((String)entry3.getKey(), entry3.getIntValue());
        }
        return compoundTag;
    }
    
    public int getNextMapId() {
        final int integer1 = this.idCounts.getInt("map") + 1;
        this.idCounts.put("map", integer1);
        this.markDirty();
        return integer1;
    }
}
