package net.minecraft.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.Validate;
import java.util.List;
import java.util.AbstractList;

public class DefaultedList<E> extends AbstractList<E>
{
    private final List<E> underlying;
    private final E defaultValue;
    
    public static <E> DefaultedList<E> create() {
        return new DefaultedList<E>();
    }
    
    public static <E> DefaultedList<E> create(final int size, final E defaultValue) {
        Validate.notNull(defaultValue);
        final Object[] arr3 = new Object[size];
        Arrays.fill(arr3, defaultValue);
        return new DefaultedList<E>(Arrays.<E>asList((E[])arr3), defaultValue);
    }
    
    @SafeVarargs
    public static <E> DefaultedList<E> create(final E defaultValue, final E... values) {
        return new DefaultedList<E>(Arrays.<E>asList(values), defaultValue);
    }
    
    protected DefaultedList() {
        this(new ArrayList<>(), null);
    }
    
    protected DefaultedList(final List<E> underlying, @Nullable final E object) {
        this.underlying = underlying;
        this.defaultValue = object;
    }
    
    @Nonnull
    @Override
    public E get(final int integer) {
        return this.underlying.get(integer);
    }
    
    @Override
    public E set(final int index, final E object) {
        Validate.notNull(object);
        return this.underlying.set(index, object);
    }
    
    @Override
    public void add(final int value, final E object) {
        Validate.notNull(object);
        this.underlying.add(value, object);
    }
    
    @Override
    public E remove(final int integer) {
        return this.underlying.remove(integer);
    }
    
    @Override
    public int size() {
        return this.underlying.size();
    }
    
    @Override
    public void clear() {
        if (this.defaultValue == null) {
            super.clear();
        }
        else {
            for (int integer1 = 0; integer1 < this.size(); ++integer1) {
                this.set(integer1, this.defaultValue);
            }
        }
    }
}
