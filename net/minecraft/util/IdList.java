package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.base.Predicates;
import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.IdentityHashMap;

public class IdList<T> implements IndexedIterable<T>
{
    private int nextId;
    private final IdentityHashMap<T, Integer> idMap;
    private final List<T> list;
    
    public IdList() {
        this(512);
    }
    
    public IdList(final int integer) {
        this.list = Lists.newArrayListWithExpectedSize(integer);
        this.idMap = new IdentityHashMap<T, Integer>(integer);
    }
    
    public void set(final T value, final int integer) {
        this.idMap.put(value, integer);
        while (this.list.size() <= integer) {
            this.list.add(null);
        }
        this.list.set(integer, value);
        if (this.nextId <= integer) {
            this.nextId = integer + 1;
        }
    }
    
    public void add(final T object) {
        this.set(object, this.nextId);
    }
    
    public int getId(final T object) {
        final Integer integer2 = this.idMap.get(object);
        return (integer2 == null) ? -1 : integer2;
    }
    
    @Nullable
    @Override
    public final T get(final int index) {
        if (index >= 0 && index < this.list.size()) {
            return this.list.get(index);
        }
        return null;
    }
    
    @Override
    public Iterator<T> iterator() {
        return Iterators.<T>filter(this.list.iterator(), Predicates.notNull());
    }
    
    public int size() {
        return this.idMap.size();
    }
}
