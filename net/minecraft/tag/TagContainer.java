package net.minecraft.tag;

import org.apache.logging.log4j.LogManager;
import java.io.Closeable;
import java.io.IOException;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonObject;
import net.minecraft.resource.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import java.util.Map;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

public class TagContainer<T>
{
    private static final Logger LOGGER;
    private static final Gson GSON;
    private static final int JSON_EXTENSION_LENGTH;
    private final Map<Identifier, Tag<T>> idMap;
    private final Function<Identifier, Optional<T>> getter;
    private final String path;
    private final boolean ordered;
    private final String type;
    
    public TagContainer(final Function<Identifier, Optional<T>> function, final String string2, final boolean boolean3, final String string4) {
        this.idMap = Maps.newHashMap();
        this.getter = function;
        this.path = string2;
        this.ordered = boolean3;
        this.type = string4;
    }
    
    public void add(final Tag<T> tag) {
        if (this.idMap.containsKey(tag.getId())) {
            throw new IllegalArgumentException("Duplicate " + this.type + " tag '" + tag.getId() + "'");
        }
        this.idMap.put(tag.getId(), tag);
    }
    
    @Nullable
    public Tag<T> get(final Identifier id) {
        return this.idMap.get(id);
    }
    
    public Tag<T> getOrCreate(final Identifier id) {
        final Tag<T> tag2 = this.idMap.get(id);
        if (tag2 == null) {
            return new Tag<T>(id);
        }
        return tag2;
    }
    
    public Collection<Identifier> getKeys() {
        return this.idMap.keySet();
    }
    
    @Environment(EnvType.CLIENT)
    public Collection<Identifier> getTagsFor(final T object) {
        final List<Identifier> list2 = Lists.newArrayList();
        for (final Map.Entry<Identifier, Tag<T>> entry4 : this.idMap.entrySet()) {
            if (entry4.getValue().contains(object)) {
                list2.add(entry4.getKey());
            }
        }
        return list2;
    }
    
    public void clear() {
        this.idMap.clear();
    }
    
    public CompletableFuture<Map<Identifier, Tag.Builder<T>>> prepareReload(final ResourceManager resourceManager, final Executor executor) {
        final Map<Identifier, Tag.Builder<T>> map2;
        final Iterator<Identifier> iterator;
        Identifier identifier4;
        String string2;
        Identifier identifier5;
        final Iterator<Resource> iterator2;
        Resource resource8;
        JsonObject jsonObject9;
        Tag.Builder<T> builder10;
        return CompletableFuture.<Map<Identifier, Tag.Builder<T>>>supplyAsync(() -> {
            map2 = Maps.newHashMap();
            resourceManager.findResources(this.path, string -> string.endsWith(".json")).iterator();
            while (iterator.hasNext()) {
                identifier4 = iterator.next();
                string2 = identifier4.getPath();
                identifier5 = new Identifier(identifier4.getNamespace(), string2.substring(this.path.length() + 1, string2.length() - TagContainer.JSON_EXTENSION_LENGTH));
                try {
                    resourceManager.getAllResources(identifier4).iterator();
                    while (iterator2.hasNext()) {
                        resource8 = iterator2.next();
                        try {
                            jsonObject9 = JsonHelper.<JsonObject>deserialize(TagContainer.GSON, IOUtils.toString(resource8.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                            if (jsonObject9 == null) {
                                TagContainer.LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.type, identifier5, identifier4, resource8.getResourcePackName());
                            }
                            else {
                                builder10 = map2.getOrDefault(identifier5, Tag.Builder.<T>create());
                                builder10.fromJson(this.getter, jsonObject9);
                                map2.put(identifier5, builder10);
                            }
                        }
                        catch (IOException ex) {}
                        catch (RuntimeException exception9) {
                            TagContainer.LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.type, identifier5, identifier4, resource8.getResourcePackName(), exception9);
                        }
                        finally {
                            IOUtils.closeQuietly((Closeable)resource8);
                        }
                    }
                }
                catch (IOException iOException7) {
                    TagContainer.LOGGER.error("Couldn't read {} tag list {} from {}", this.type, identifier5, identifier4, iOException7);
                }
            }
            return map2;
        }, executor);
    }
    
    public void applyReload(final Map<Identifier, Tag.Builder<T>> map) {
        while (!map.isEmpty()) {
            boolean boolean2 = false;
            final Iterator<Map.Entry<Identifier, Tag.Builder<T>>> iterator3 = map.entrySet().iterator();
            while (iterator3.hasNext()) {
                final Map.Entry<Identifier, Tag.Builder<T>> entry4 = iterator3.next();
                if (entry4.getValue().applyTagGetter(this::get)) {
                    boolean2 = true;
                    this.add(entry4.getValue().build(entry4.getKey()));
                    iterator3.remove();
                }
            }
            if (!boolean2) {
                for (final Map.Entry<Identifier, Tag.Builder<T>> entry4 : map.entrySet()) {
                    TagContainer.LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.type, entry4.getKey());
                }
                break;
            }
        }
        for (final Map.Entry<Identifier, Tag.Builder<T>> entry5 : map.entrySet()) {
            this.add(entry5.getValue().ordered(this.ordered).build(entry5.getKey()));
        }
    }
    
    public Map<Identifier, Tag<T>> getEntries() {
        return this.idMap;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        GSON = new Gson();
        JSON_EXTENSION_LENGTH = ".json".length();
    }
}
