package net.minecraft.state;

import java.util.ArrayList;
import javax.annotation.Nullable;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import com.google.common.base.MoreObjects;
import java.util.Collection;
import java.util.Iterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.MapUtil;
import java.util.List;
import java.util.stream.Stream;
import java.util.Collections;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import net.minecraft.state.property.Property;
import com.google.common.collect.ImmutableSortedMap;
import java.util.regex.Pattern;

public class StateFactory<O, S extends PropertyContainer<S>>
{
    private static final Pattern NAME_MATCHER;
    private final O baseObject;
    private final ImmutableSortedMap<String, Property<?>> propertyMap;
    private final ImmutableList<S> states;
    
    protected <A extends AbstractPropertyContainer<O, S>> StateFactory(final O baseObject, final Factory<O, S, A> factory, final Map<String, Property<?>> map) {
        this.baseObject = baseObject;
        this.propertyMap = ImmutableSortedMap.<String, Property<?>>copyOf(map);
        final Map<Map<Property<?>, Comparable<?>>, A> map2 = Maps.newLinkedHashMap();
        final List<A> list6 = Lists.newArrayList();
        Stream<List<Comparable<?>>> stream6 = Stream.<List<Comparable<?>>>of(Collections.<Comparable<?>>emptyList());
        for (final Property<?> property8 : this.propertyMap.values()) {
            final List<Comparable<?>> list7;
            stream6 = stream6.<List<Comparable<?>>>flatMap(list -> property8.getValues().stream().map(comparable -> {
                list7 = Lists.newArrayList(list);
                list7.add(comparable);
                return list7;
            }));
        }
        final Map<Property<?>, Comparable<?>> map3;
        final AbstractPropertyContainer abstractPropertyContainer7;
        final Map<Map<Property<?>, Comparable<?>>, AbstractPropertyContainer> map4;
        final List<AbstractPropertyContainer> list8;
        stream6.forEach(list5 -> {
            map3 = MapUtil.<Property<?>, Comparable<?>>createMap(this.propertyMap.values(), list5);
            abstractPropertyContainer7 = factory.create(baseObject, ImmutableMap.<Property<?>, Comparable<?>>copyOf(map3));
            map4.put(map3, abstractPropertyContainer7);
            list8.add(abstractPropertyContainer7);
            return;
        });
        for (final A abstractPropertyContainer8 : list6) {
            ((AbstractPropertyContainer<O, S>)abstractPropertyContainer8).createWithTable((Map<Map<Property<?>, Comparable<?>>, S>)map2);
        }
        this.states = ImmutableList.<S>copyOf(list6);
    }
    
    public ImmutableList<S> getStates() {
        return this.states;
    }
    
    public S getDefaultState() {
        return this.states.get(0);
    }
    
    public O getBaseObject() {
        return this.baseObject;
    }
    
    public Collection<Property<?>> getProperties() {
        return this.propertyMap.values();
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("block", this.baseObject).add("properties", this.propertyMap.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }
    
    @Nullable
    public Property<?> getProperty(final String string) {
        return this.propertyMap.get(string);
    }
    
    static {
        NAME_MATCHER = Pattern.compile("^[a-z0-9_]+$");
    }
    
    public static class Builder<O, S extends PropertyContainer<S>>
    {
        private final O baseObject;
        private final Map<String, Property<?>> propertyMap;
        
        public Builder(final O object) {
            this.propertyMap = Maps.newHashMap();
            this.baseObject = object;
        }
        
        public Builder<O, S> add(final Property<?>... arr) {
            for (final Property<?> property5 : arr) {
                this.validate(property5);
                this.propertyMap.put(property5.getName(), property5);
            }
            return this;
        }
        
        private <T extends Comparable<T>> void validate(final Property<T> property) {
            final String string2 = property.getName();
            if (!StateFactory.NAME_MATCHER.matcher(string2).matches()) {
                throw new IllegalArgumentException(this.baseObject + " has invalidly named property: " + string2);
            }
            final Collection<T> collection3 = property.getValues();
            if (collection3.size() <= 1) {
                throw new IllegalArgumentException(this.baseObject + " attempted use property " + string2 + " with <= 1 possible values");
            }
            for (final T comparable5 : collection3) {
                final String string3 = property.getValueAsString(comparable5);
                if (!StateFactory.NAME_MATCHER.matcher(string3).matches()) {
                    throw new IllegalArgumentException(this.baseObject + " has property: " + string2 + " with invalidly named value: " + string3);
                }
            }
            if (this.propertyMap.containsKey(string2)) {
                throw new IllegalArgumentException(this.baseObject + " has duplicate property: " + string2);
            }
        }
        
        public <A extends AbstractPropertyContainer<O, S>> StateFactory<O, S> build(final Factory<O, S, A> factory) {
            return new StateFactory<O, S>(this.baseObject, (Factory<O, S, A>)factory, this.propertyMap);
        }
    }
    
    public interface Factory<O, S extends PropertyContainer<S>, A extends AbstractPropertyContainer<O, S>>
    {
        A create(final O arg1, final ImmutableMap<Property<?>, Comparable<?>> arg2);
    }
}
