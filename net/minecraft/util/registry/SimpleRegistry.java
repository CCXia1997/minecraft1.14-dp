package net.minecraft.util.registry;

import org.apache.logging.log4j.LogManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Collection;
import java.util.Random;
import java.util.Collections;
import java.util.Set;
import java.util.Optional;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.Identifier;
import com.google.common.collect.BiMap;
import net.minecraft.util.Int2ObjectBiMap;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T> extends MutableRegistry<T>
{
    protected static final Logger LOGGER;
    protected final Int2ObjectBiMap<T> indexedEntries;
    protected final BiMap<Identifier, T> entries;
    protected Object[] randomEntries;
    private int nextId;
    
    public SimpleRegistry() {
        this.indexedEntries = new Int2ObjectBiMap<T>(256);
        this.entries = HashBiMap.create();
    }
    
    @Override
    public <V extends T> V set(final int rawId, final Identifier id, final V entry) {
        this.indexedEntries.put(entry, rawId);
        Validate.notNull(id);
        Validate.notNull(entry);
        this.randomEntries = null;
        if (this.entries.containsKey(id)) {
            SimpleRegistry.LOGGER.debug("Adding duplicate key '{}' to registry", id);
        }
        this.entries.put(id, entry);
        if (this.nextId <= rawId) {
            this.nextId = rawId + 1;
        }
        return entry;
    }
    
    @Override
    public <V extends T> V add(final Identifier id, final V entry) {
        return this.<V>set(this.nextId, id, entry);
    }
    
    @Nullable
    @Override
    public Identifier getId(final T entry) {
        return this.entries.inverse().get(entry);
    }
    
    @Override
    public int getRawId(@Nullable final T entry) {
        return this.indexedEntries.getId(entry);
    }
    
    @Nullable
    @Override
    public T get(final int index) {
        return this.indexedEntries.get(index);
    }
    
    @Override
    public Iterator<T> iterator() {
        return this.indexedEntries.iterator();
    }
    
    @Nullable
    @Override
    public T get(@Nullable final Identifier id) {
        return this.entries.get(id);
    }
    
    @Override
    public Optional<T> getOrEmpty(@Nullable final Identifier id) {
        return Optional.<T>ofNullable(this.entries.get(id));
    }
    
    @Override
    public Set<Identifier> getIds() {
        return Collections.<Identifier>unmodifiableSet(this.entries.keySet());
    }
    
    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }
    
    @Nullable
    @Override
    public T getRandom(final Random rand) {
        if (this.randomEntries == null) {
            final Collection<?> collection2 = this.entries.values();
            if (collection2.isEmpty()) {
                return null;
            }
            this.randomEntries = collection2.toArray(new Object[collection2.size()]);
        }
        return (T)this.randomEntries[rand.nextInt(this.randomEntries.length)];
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean containsId(final Identifier id) {
        return this.entries.containsKey(id);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
