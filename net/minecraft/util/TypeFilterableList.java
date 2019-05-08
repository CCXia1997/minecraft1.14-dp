package net.minecraft.util;

import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.Iterator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.AbstractCollection;

public class TypeFilterableList<T> extends AbstractCollection<T>
{
    private final Map<Class<?>, List<T>> elementsByType;
    private final Class<T> elementType;
    private final List<T> allElements;
    
    public TypeFilterableList(final Class<T> elementType) {
        this.elementsByType = Maps.newHashMap();
        this.allElements = Lists.newArrayList();
        this.elementType = elementType;
        this.elementsByType.put(elementType, this.allElements);
    }
    
    @Override
    public boolean add(final T e) {
        boolean boolean2 = false;
        for (final Map.Entry<Class<?>, List<T>> entry4 : this.elementsByType.entrySet()) {
            if (entry4.getKey().isInstance(e)) {
                boolean2 |= entry4.getValue().add(e);
            }
        }
        return boolean2;
    }
    
    @Override
    public boolean remove(final Object o) {
        boolean boolean2 = false;
        for (final Map.Entry<Class<?>, List<T>> entry4 : this.elementsByType.entrySet()) {
            if (entry4.getKey().isInstance(o)) {
                final List<T> list5 = entry4.getValue();
                boolean2 |= list5.remove(o);
            }
        }
        return boolean2;
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.getAllOfType(o.getClass()).contains(o);
    }
    
    public <S> Collection<S> getAllOfType(final Class<S> type) {
        if (!this.elementType.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Don't know how to search for " + type);
        }
        final List<T> list2 = this.elementsByType.computeIfAbsent(type, class1 -> this.allElements.stream().filter(class1::isInstance).collect(Collectors.toList()));
        return Collections.<S>unmodifiableCollection(list2);
    }
    
    @Override
    public Iterator<T> iterator() {
        if (this.allElements.isEmpty()) {
            return Collections.<T>emptyIterator();
        }
        return Iterators.unmodifiableIterator(this.allElements.iterator());
    }
    
    @Override
    public int size() {
        return this.allElements.size();
    }
}
