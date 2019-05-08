package net.minecraft.tag;

import javax.annotation.Nullable;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import java.util.Optional;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Random;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.function.Function;
import java.util.Iterator;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.Identifier;

public class Tag<T>
{
    private final Identifier id;
    private final Set<T> values;
    private final Collection<Entry<T>> entries;
    
    public Tag(final Identifier identifier) {
        this.id = identifier;
        this.values = Collections.<T>emptySet();
        this.entries = Collections.emptyList();
    }
    
    public Tag(final Identifier id, final Collection<Entry<T>> entries, final boolean boolean3) {
        this.id = id;
        this.values = (Set<T>)(boolean3 ? Sets.newLinkedHashSet() : Sets.newHashSet());
        this.entries = entries;
        for (final Entry<T> entry5 : entries) {
            entry5.build(this.values);
        }
    }
    
    public JsonObject toJson(final Function<T, Identifier> function) {
        final JsonObject jsonObject2 = new JsonObject();
        final JsonArray jsonArray3 = new JsonArray();
        for (final Entry<T> entry5 : this.entries) {
            entry5.toJson(jsonArray3, function);
        }
        jsonObject2.addProperty("replace", false);
        jsonObject2.add("values", jsonArray3);
        return jsonObject2;
    }
    
    public boolean contains(final T object) {
        return this.values.contains(object);
    }
    
    public Collection<T> values() {
        return this.values;
    }
    
    public Collection<Entry<T>> entries() {
        return this.entries;
    }
    
    public T getRandom(final Random random) {
        final List<T> list2 = Lists.newArrayList(this.values());
        return list2.get(random.nextInt(list2.size()));
    }
    
    public Identifier getId() {
        return this.id;
    }
    
    public static class Builder<T>
    {
        private final Set<Entry<T>> entries;
        private boolean ordered;
        
        public Builder() {
            this.entries = Sets.newLinkedHashSet();
        }
        
        public static <T> Builder<T> create() {
            return new Builder<T>();
        }
        
        public Builder<T> add(final Entry<T> entry) {
            this.entries.add(entry);
            return this;
        }
        
        public Builder<T> add(final T object) {
            this.entries.add(new CollectionEntry<T>(Collections.<T>singleton(object)));
            return this;
        }
        
        @SafeVarargs
        public final Builder<T> add(final T... arr) {
            this.entries.add(new CollectionEntry<T>(Lists.<T>newArrayList(arr)));
            return this;
        }
        
        public Builder<T> add(final Collection<T> collection) {
            this.entries.add(new CollectionEntry<T>(collection));
            return this;
        }
        
        public Builder<T> add(final Identifier identifier) {
            this.entries.add(new TagEntry<T>(identifier));
            return this;
        }
        
        public Builder<T> add(final Tag<T> tag) {
            this.entries.add(new TagEntry<T>(tag));
            return this;
        }
        
        public Builder<T> ordered(final boolean boolean1) {
            this.ordered = boolean1;
            return this;
        }
        
        public boolean applyTagGetter(final Function<Identifier, Tag<T>> function) {
            for (final Entry<T> entry3 : this.entries) {
                if (!entry3.applyTagGetter(function)) {
                    return false;
                }
            }
            return true;
        }
        
        public Tag<T> build(final Identifier identifier) {
            return new Tag<T>(identifier, this.entries, this.ordered);
        }
        
        public Builder<T> fromJson(final Function<Identifier, Optional<T>> function, final JsonObject jsonObject) {
            final JsonArray jsonArray3 = JsonHelper.getArray(jsonObject, "values");
            if (JsonHelper.getBoolean(jsonObject, "replace", false)) {
                this.entries.clear();
            }
            for (final JsonElement jsonElement5 : jsonArray3) {
                final String string6 = JsonHelper.asString(jsonElement5, "value");
                if (string6.startsWith("#")) {
                    this.add(new Identifier(string6.substring(1)));
                }
                else {
                    final Identifier identifier7 = new Identifier(string6);
                    final Object o;
                    final Object o2;
                    this.add(function.apply(identifier7).<Throwable>orElseThrow(() -> {
                        new JsonParseException("Unknown value '" + o2 + "'");
                        return o;
                    }));
                }
            }
            return this;
        }
    }
    
    public interface Entry<T>
    {
        default boolean applyTagGetter(final Function<Identifier, Tag<T>> function) {
            return true;
        }
        
        void build(final Collection<T> arg1);
        
        void toJson(final JsonArray arg1, final Function<T, Identifier> arg2);
    }
    
    public static class CollectionEntry<T> implements Entry<T>
    {
        private final Collection<T> values;
        
        public CollectionEntry(final Collection<T> collection) {
            this.values = collection;
        }
        
        @Override
        public void build(final Collection<T> collection) {
            collection.addAll(this.values);
        }
        
        @Override
        public void toJson(final JsonArray jsonArray, final Function<T, Identifier> function) {
            for (final T object4 : this.values) {
                final Identifier identifier5 = function.apply(object4);
                if (identifier5 == null) {
                    throw new IllegalStateException("Unable to serialize an anonymous value to json!");
                }
                jsonArray.add(identifier5.toString());
            }
        }
        
        public Collection<T> getValues() {
            return this.values;
        }
    }
    
    public static class TagEntry<T> implements Entry<T>
    {
        @Nullable
        private final Identifier id;
        @Nullable
        private Tag<T> tag;
        
        public TagEntry(final Identifier identifier) {
            this.id = identifier;
        }
        
        public TagEntry(final Tag<T> tag) {
            this.id = tag.getId();
            this.tag = tag;
        }
        
        @Override
        public boolean applyTagGetter(final Function<Identifier, Tag<T>> function) {
            if (this.tag == null) {
                this.tag = function.apply(this.id);
            }
            return this.tag != null;
        }
        
        @Override
        public void build(final Collection<T> collection) {
            if (this.tag == null) {
                throw new IllegalStateException("Cannot build unresolved tag entry");
            }
            collection.addAll(this.tag.values());
        }
        
        public Identifier getId() {
            if (this.tag != null) {
                return this.tag.getId();
            }
            if (this.id != null) {
                return this.id;
            }
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
        }
        
        @Override
        public void toJson(final JsonArray jsonArray, final Function<T, Identifier> function) {
            jsonArray.add("#" + this.getId());
        }
    }
}
