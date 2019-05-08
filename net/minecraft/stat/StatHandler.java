package net.minecraft.stat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class StatHandler
{
    protected final Object2IntMap<Stat<?>> statMap;
    
    public StatHandler() {
        (this.statMap = (Object2IntMap<Stat<?>>)Object2IntMaps.synchronize((Object2IntMap)new Object2IntOpenHashMap())).defaultReturnValue(0);
    }
    
    public void increaseStat(final PlayerEntity player, final Stat<?> stat, final int value) {
        this.setStat(player, stat, this.getStat(stat) + value);
    }
    
    public void setStat(final PlayerEntity player, final Stat<?> stat, final int value) {
        this.statMap.put(stat, value);
    }
    
    @Environment(EnvType.CLIENT)
    public <T> int getStat(final StatType<T> type, final T stat) {
        return type.hasStat(stat) ? this.getStat(type.getOrCreateStat(stat)) : 0;
    }
    
    public int getStat(final Stat<?> stat) {
        return this.statMap.getInt(stat);
    }
}
