package net.minecraft.stat;

import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.util.registry.Registry;

public class StatType<T> implements Iterable<Stat<T>>
{
    private final Registry<T> registry;
    private final Map<T, Stat<T>> stats;
    
    public StatType(final Registry<T> registry) {
        this.stats = new IdentityHashMap<T, Stat<T>>();
        this.registry = registry;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasStat(final T key) {
        return this.stats.containsKey(key);
    }
    
    public Stat<T> getOrCreateStat(final T key, final StatFormatter formatter) {
        return this.stats.computeIfAbsent(key, object -> new Stat((StatType<Object>)this, object, formatter));
    }
    
    public Registry<T> getRegistry() {
        return this.registry;
    }
    
    @Override
    public Iterator<Stat<T>> iterator() {
        return this.stats.values().iterator();
    }
    
    public Stat<T> getOrCreateStat(final T key) {
        return this.getOrCreateStat(key, StatFormatter.DEFAULT);
    }
    
    @Environment(EnvType.CLIENT)
    public String getTranslationKey() {
        return "stat_type." + Registry.STAT_TYPE.getId(this).toString().replace(':', '.');
    }
}
