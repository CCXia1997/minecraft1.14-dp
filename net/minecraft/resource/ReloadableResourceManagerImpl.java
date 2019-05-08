package net.minecraft.resource;

import org.apache.logging.log4j.LogManager;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.ArrayList;
import net.minecraft.util.Void;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.Collections;
import java.util.Collection;
import java.util.function.Predicate;
import java.io.IOException;
import java.io.FileNotFoundException;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Set;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

public class ReloadableResourceManagerImpl implements ReloadableResourceManager
{
    private static final Logger LOGGER;
    private final Map<String, NamespaceResourceManager> namespaceManagers;
    private final List<ResourceReloadListener> listeners;
    private final List<ResourceReloadListener> initialListeners;
    private final Set<String> namespaces;
    private final ResourceType type;
    private final Thread g;
    
    public ReloadableResourceManagerImpl(final ResourceType resourceType, final Thread thread) {
        this.namespaceManagers = Maps.newHashMap();
        this.listeners = Lists.newArrayList();
        this.initialListeners = Lists.newArrayList();
        this.namespaces = Sets.newLinkedHashSet();
        this.type = resourceType;
        this.g = thread;
    }
    
    @Override
    public void addPack(final ResourcePack resourcePack) {
        for (final String string3 : resourcePack.getNamespaces(this.type)) {
            this.namespaces.add(string3);
            NamespaceResourceManager namespaceResourceManager4 = this.namespaceManagers.get(string3);
            if (namespaceResourceManager4 == null) {
                namespaceResourceManager4 = new NamespaceResourceManager(this.type);
                this.namespaceManagers.put(string3, namespaceResourceManager4);
            }
            namespaceResourceManager4.addPack(resourcePack);
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public Set<String> getAllNamespaces() {
        return this.namespaces;
    }
    
    @Override
    public Resource getResource(final Identifier identifier) throws IOException {
        final ResourceManager resourceManager2 = this.namespaceManagers.get(identifier.getNamespace());
        if (resourceManager2 != null) {
            return resourceManager2.getResource(identifier);
        }
        throw new FileNotFoundException(identifier.toString());
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean containsResource(final Identifier identifier) {
        final ResourceManager resourceManager2 = this.namespaceManagers.get(identifier.getNamespace());
        return resourceManager2 != null && resourceManager2.containsResource(identifier);
    }
    
    @Override
    public List<Resource> getAllResources(final Identifier identifier) throws IOException {
        final ResourceManager resourceManager2 = this.namespaceManagers.get(identifier.getNamespace());
        if (resourceManager2 != null) {
            return resourceManager2.getAllResources(identifier);
        }
        throw new FileNotFoundException(identifier.toString());
    }
    
    @Override
    public Collection<Identifier> findResources(final String namespace, final Predicate<String> predicate) {
        final Set<Identifier> set3 = Sets.newHashSet();
        for (final NamespaceResourceManager namespaceResourceManager5 : this.namespaceManagers.values()) {
            set3.addAll(namespaceResourceManager5.findResources(namespace, predicate));
        }
        final List<Identifier> list4 = Lists.newArrayList(set3);
        Collections.<Identifier>sort(list4);
        return list4;
    }
    
    private void clear() {
        this.namespaceManagers.clear();
        this.namespaces.clear();
    }
    
    @Override
    public CompletableFuture<Void> beginReload(final Executor executor1, final Executor executor2, final List<ResourcePack> list, final CompletableFuture<Void> completableFuture) {
        final ResourceReloadMonitor resourceReloadMonitor5 = this.beginMonitoredReload(executor1, executor2, completableFuture, list);
        return resourceReloadMonitor5.whenComplete();
    }
    
    @Override
    public void registerListener(final ResourceReloadListener resourceReloadListener) {
        this.listeners.add(resourceReloadListener);
        this.initialListeners.add(resourceReloadListener);
    }
    
    protected ResourceReloadMonitor beginReloadInner(final Executor executor1, final Executor executor2, final List<ResourceReloadListener> list, final CompletableFuture<Void> completableFuture) {
        ResourceReloadMonitor resourceReloadMonitor5;
        if (ReloadableResourceManagerImpl.LOGGER.isDebugEnabled()) {
            resourceReloadMonitor5 = new ProfilingResourceReloadHandler(this, new ArrayList<ResourceReloadListener>(list), executor1, executor2, completableFuture);
        }
        else {
            resourceReloadMonitor5 = ResourceReloadHandler.create(this, new ArrayList<ResourceReloadListener>(list), executor1, executor2, completableFuture);
        }
        this.initialListeners.clear();
        return resourceReloadMonitor5;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public ResourceReloadMonitor beginInitialMonitoredReload(final Executor executor1, final Executor executor2, final CompletableFuture<Void> completableFuture) {
        return this.beginReloadInner(executor1, executor2, this.initialListeners, completableFuture);
    }
    
    @Override
    public ResourceReloadMonitor beginMonitoredReload(final Executor executor1, final Executor executor2, final CompletableFuture<Void> completableFuture, final List<ResourcePack> list) {
        this.clear();
        ReloadableResourceManagerImpl.LOGGER.info("Reloading ResourceManager: {}", list.stream().map(ResourcePack::getName).collect(Collectors.joining(", ")));
        for (final ResourcePack resourcePack6 : list) {
            this.addPack(resourcePack6);
        }
        return this.beginReloadInner(executor1, executor2, this.listeners, completableFuture);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
