package net.minecraft.util;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.Locale;
import com.google.common.collect.Maps;
import java.util.Map;

public class LowercaseMap<V> implements Map<String, V>
{
    private final Map<String, V> delegate;
    
    public LowercaseMap() {
        this.delegate = Maps.newLinkedHashMap();
    }
    
    @Override
    public int size() {
        return this.delegate.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object object) {
        return this.delegate.containsKey(object.toString().toLowerCase(Locale.ROOT));
    }
    
    @Override
    public boolean containsValue(final Object object) {
        return this.delegate.containsValue(object);
    }
    
    @Override
    public V get(final Object object) {
        return this.delegate.get(object.toString().toLowerCase(Locale.ROOT));
    }
    
    public V a(final String string, final V object) {
        return this.delegate.put(string.toLowerCase(Locale.ROOT), object);
    }
    
    @Override
    public V remove(final Object object) {
        return this.delegate.remove(object.toString().toLowerCase(Locale.ROOT));
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends V> map) {
        for (final Entry<? extends String, ? extends V> entry3 : map.entrySet()) {
            this.a((String)entry3.getKey(), entry3.getValue());
        }
    }
    
    @Override
    public void clear() {
        this.delegate.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return this.delegate.keySet();
    }
    
    @Override
    public Collection<V> values() {
        return this.delegate.values();
    }
    
    @Override
    public Set<Entry<String, V>> entrySet() {
        return this.delegate.entrySet();
    }
}
