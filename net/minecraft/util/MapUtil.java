package net.minecraft.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import com.google.common.collect.Maps;
import java.util.Map;

public class MapUtil
{
    public static <K, V> Map<K, V> createMap(final Iterable<K> keys, final Iterable<V> values) {
        return MapUtil.<K, V>createMap(keys, values, Maps.newLinkedHashMap());
    }
    
    public static <K, V> Map<K, V> createMap(final Iterable<K> keys, final Iterable<V> values, final Map<K, V> result) {
        final Iterator<V> iterator4 = values.iterator();
        for (final K object6 : keys) {
            result.put(object6, iterator4.next());
        }
        if (iterator4.hasNext()) {
            throw new NoSuchElementException();
        }
        return result;
    }
}
