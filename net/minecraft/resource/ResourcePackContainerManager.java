package net.minecraft.resource;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Iterator;
import com.google.common.base.Functions;
import java.util.function.Predicate;
import java.util.Objects;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;
import java.util.function.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourcePackContainerManager<T extends ResourcePackContainer> implements AutoCloseable
{
    private final Set<ResourcePackCreator> creators;
    private final Map<String, T> nameToContainer;
    private final List<T> enabledContainers;
    private final ResourcePackContainer.Factory<T> factory;
    
    public ResourcePackContainerManager(final ResourcePackContainer.Factory<T> factory) {
        this.creators = Sets.newHashSet();
        this.nameToContainer = Maps.newLinkedHashMap();
        this.enabledContainers = Lists.newLinkedList();
        this.factory = factory;
    }
    
    public void callCreators() {
        this.close();
        final Set<String> set1 = this.enabledContainers.stream().map(ResourcePackContainer::getName).collect(Collectors.toCollection((Supplier<R>)LinkedHashSet::new));
        this.nameToContainer.clear();
        this.enabledContainers.clear();
        for (final ResourcePackCreator resourcePackCreator3 : this.creators) {
            resourcePackCreator3.<T>registerContainer(this.nameToContainer, this.factory);
        }
        this.sortMapByName();
        this.enabledContainers.addAll(set1.stream().map(this.nameToContainer::get).filter(Objects::nonNull).collect(Collectors.toCollection((Supplier<R>)LinkedHashSet::new)));
        for (final T resourcePackContainer3 : this.nameToContainer.values()) {
            if (resourcePackContainer3.canBeSorted() && !this.enabledContainers.contains(resourcePackContainer3)) {
                resourcePackContainer3.getSortingDirection().locate((List<Object>)this.enabledContainers, resourcePackContainer3, Functions.identity(), false);
            }
        }
    }
    
    private void sortMapByName() {
        final List<Map.Entry<String, T>> list1 = Lists.newArrayList(this.nameToContainer.entrySet());
        this.nameToContainer.clear();
        final ResourcePackContainer resourcePackContainer;
        list1.stream().sorted(Map.Entry.<Comparable, Object>comparingByKey()).forEachOrdered(entry -> resourcePackContainer = this.nameToContainer.put(entry.getKey(), (T)entry.getValue()));
    }
    
    public void setEnabled(final Collection<T> enabled) {
        this.enabledContainers.clear();
        this.enabledContainers.addAll(enabled);
        for (final T resourcePackContainer3 : this.nameToContainer.values()) {
            if (resourcePackContainer3.canBeSorted() && !this.enabledContainers.contains(resourcePackContainer3)) {
                resourcePackContainer3.getSortingDirection().locate((List<Object>)this.enabledContainers, resourcePackContainer3, Functions.identity(), false);
            }
        }
    }
    
    public Collection<T> getAlphabeticallyOrderedContainers() {
        return this.nameToContainer.values();
    }
    
    public Collection<T> getDisabledContainers() {
        final Collection<T> collection1 = Lists.newArrayList(this.nameToContainer.values());
        collection1.removeAll(this.enabledContainers);
        return collection1;
    }
    
    public Collection<T> getEnabledContainers() {
        return this.enabledContainers;
    }
    
    @Nullable
    public T getContainer(final String name) {
        return this.nameToContainer.get(name);
    }
    
    public void addCreator(final ResourcePackCreator creator) {
        this.creators.add(creator);
    }
    
    @Override
    public void close() {
        this.nameToContainer.values().forEach(ResourcePackContainer::close);
    }
}
