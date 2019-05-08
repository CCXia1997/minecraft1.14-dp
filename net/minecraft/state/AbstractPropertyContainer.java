package net.minecraft.state;

import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableMap;
import net.minecraft.state.property.Property;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractPropertyContainer<O, S> implements PropertyContainer<S>
{
    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER;
    protected final O owner;
    private final ImmutableMap<Property<?>, Comparable<?>> entries;
    private final int hashCode;
    private Table<Property<?>, Comparable<?>, S> withTable;
    
    protected AbstractPropertyContainer(final O owner, final ImmutableMap<Property<?>, Comparable<?>> entries) {
        this.owner = owner;
        this.entries = entries;
        this.hashCode = entries.hashCode();
    }
    
    public <T extends Comparable<T>> S cycle(final Property<T> property) {
        return this.<T, Comparable>with(property, (Comparable)AbstractPropertyContainer.<V>getNext((Collection<V>)property.getValues(), (V)this.<T>get((Property<T>)property)));
    }
    
    protected static <T> T getNext(final Collection<T> values, final T value) {
        final Iterator<T> iterator3 = values.iterator();
        while (iterator3.hasNext()) {
            if (iterator3.next().equals(value)) {
                if (iterator3.hasNext()) {
                    return iterator3.next();
                }
                return values.iterator().next();
            }
        }
        return iterator3.next();
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(this.owner);
        if (!this.getEntries().isEmpty()) {
            stringBuilder1.append('[');
            stringBuilder1.append(this.getEntries().entrySet().stream().map(AbstractPropertyContainer.PROPERTY_MAP_PRINTER).collect(Collectors.joining(",")));
            stringBuilder1.append(']');
        }
        return stringBuilder1.toString();
    }
    
    public Collection<Property<?>> getProperties() {
        return Collections.<Property<?>>unmodifiableCollection(this.entries.keySet());
    }
    
    public <T extends Comparable<T>> boolean contains(final Property<T> property) {
        return this.entries.containsKey(property);
    }
    
    @Override
    public <T extends Comparable<T>> T get(final Property<T> property) {
        final Comparable<?> comparable2 = this.entries.get(property);
        if (comparable2 == null) {
            throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.owner);
        }
        return property.getValueClass().cast(comparable2);
    }
    
    @Override
    public <T extends Comparable<T>, V extends T> S with(final Property<T> property, final V comparable) {
        final Comparable<?> comparable2 = this.entries.get(property);
        if (comparable2 == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.owner);
        }
        if (comparable2 == comparable) {
            return (S)this;
        }
        final S object4 = this.withTable.get(property, comparable);
        if (object4 == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " to " + comparable + " on " + this.owner + ", it is not an allowed value");
        }
        return object4;
    }
    
    public void createWithTable(final Map<Map<Property<?>, Comparable<?>>, S> states) {
        if (this.withTable != null) {
            throw new IllegalStateException();
        }
        final Table<Property<?>, Comparable<?>, S> table2 = HashBasedTable.create();
        for (final Map.Entry<Property<?>, Comparable<?>> entry4 : this.entries.entrySet()) {
            final Property<?> property5 = entry4.getKey();
            for (final Comparable<?> comparable7 : property5.getValues()) {
                if (comparable7 != entry4.getValue()) {
                    table2.put(property5, comparable7, states.get(this.toMapWith(property5, comparable7)));
                }
            }
        }
        this.withTable = (table2.isEmpty() ? table2 : ArrayTable.<Property<?>, Comparable<?>, S>create(table2));
    }
    
    private Map<Property<?>, Comparable<?>> toMapWith(final Property<?> property, final Comparable<?> value) {
        final Map<Property<?>, Comparable<?>> map3 = Maps.newHashMap(this.entries);
        map3.put(property, value);
        return map3;
    }
    
    @Override
    public ImmutableMap<Property<?>, Comparable<?>> getEntries() {
        return this.entries;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    static {
        PROPERTY_MAP_PRINTER = new Function<Map.Entry<Property<?>, Comparable<?>>, String>() {
            public String a(@Nullable final Map.Entry<Property<?>, Comparable<?>> entry) {
                if (entry == null) {
                    return "<NULL>";
                }
                final Property<?> property2 = entry.getKey();
                return property2.getName() + "=" + this.valueToString(property2, entry.getValue());
            }
            
            private <T extends Comparable<T>> String valueToString(final Property<T> property, final Comparable<?> value) {
                return property.getValueAsString((T)value);
            }
        };
    }
}
