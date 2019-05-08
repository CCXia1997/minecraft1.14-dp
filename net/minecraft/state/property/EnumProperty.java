package net.minecraft.state.property;

import com.google.common.collect.Lists;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.function.Predicate;
import com.google.common.base.Predicates;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.util.StringRepresentable;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import com.google.common.collect.ImmutableSet;

public class EnumProperty<T extends Enum> extends AbstractProperty<T>
{
    private final ImmutableSet<T> values;
    private final Map<String, T> valuesByName;
    
    protected EnumProperty(final String string, final Class<T> valueClass, final Collection<T> values) {
        super(string, valueClass);
        this.valuesByName = Maps.newHashMap();
        this.values = ImmutableSet.<T>copyOf(values);
        for (final T enum5 : values) {
            final String string2 = ((StringRepresentable)enum5).asString();
            if (this.valuesByName.containsKey(string2)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + string2 + "'");
            }
            this.valuesByName.put(string2, enum5);
        }
    }
    
    @Override
    public Collection<T> getValues() {
        return this.values;
    }
    
    @Override
    public Optional<T> getValue(final String string) {
        return Optional.<T>ofNullable(this.valuesByName.get(string));
    }
    
    @Override
    public String getValueAsString(final T enum1) {
        return ((StringRepresentable)enum1).asString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EnumProperty && super.equals(o)) {
            final EnumProperty<?> enumProperty2 = o;
            return this.values.equals(enumProperty2.values) && this.valuesByName.equals(enumProperty2.valuesByName);
        }
        return false;
    }
    
    @Override
    public int computeHashCode() {
        int integer1 = super.computeHashCode();
        integer1 = 31 * integer1 + this.values.hashCode();
        integer1 = 31 * integer1 + this.valuesByName.hashCode();
        return integer1;
    }
    
    public static <T extends java.lang.Enum> EnumProperty<T> create(final String name, final Class<T> valueClass) {
        return EnumProperty.<T>create(name, valueClass, Predicates.alwaysTrue());
    }
    
    public static <T extends java.lang.Enum> EnumProperty<T> create(final String name, final Class<T> valueClass, final Predicate<T> predicate) {
        return EnumProperty.<T>create(name, valueClass, Arrays.<T>stream(valueClass.getEnumConstants()).filter(predicate).collect(Collectors.toList()));
    }
    
    public static <T extends java.lang.Enum> EnumProperty<T> create(final String name, final Class<T> valueClass, final T... values) {
        return EnumProperty.<T>create(name, valueClass, Lists.<T>newArrayList(values));
    }
    
    public static <T extends java.lang.Enum> EnumProperty<T> create(final String name, final Class<T> valueClass, final Collection<T> validValues) {
        return new EnumProperty<T>(name, valueClass, validValues);
    }
}
