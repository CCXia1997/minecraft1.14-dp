package net.minecraft.client.search;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.AbstractIterator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Iterator;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import net.minecraft.util.Identifier;
import java.util.stream.Stream;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class IdentifierSearchableContainer<T> implements SearchableContainer<T>
{
    protected SuffixArray<T> byNamespace;
    protected SuffixArray<T> byPath;
    private final Function<T, Stream<Identifier>> identifierFinder;
    private final List<T> entries;
    private final Object2IntMap<T> entryIds;
    
    public IdentifierSearchableContainer(final Function<T, Stream<Identifier>> function) {
        this.byNamespace = new SuffixArray<T>();
        this.byPath = new SuffixArray<T>();
        this.entries = Lists.newArrayList();
        this.entryIds = (Object2IntMap<T>)new Object2IntOpenHashMap();
        this.identifierFinder = function;
    }
    
    @Override
    public void reload() {
        this.byNamespace = new SuffixArray<T>();
        this.byPath = new SuffixArray<T>();
        for (final T object2 : this.entries) {
            this.index(object2);
        }
        this.byNamespace.reload();
        this.byPath.reload();
    }
    
    @Override
    public void add(final T object) {
        this.entryIds.put(object, this.entries.size());
        this.entries.add(object);
        this.index(object);
    }
    
    @Override
    public void clear() {
        this.entries.clear();
        this.entryIds.clear();
    }
    
    protected void index(final T object) {
        this.identifierFinder.apply(object).forEach(identifier -> {
            this.byNamespace.add(object, identifier.getNamespace().toLowerCase(Locale.ROOT));
            this.byPath.add(object, identifier.getPath().toLowerCase(Locale.ROOT));
        });
    }
    
    protected int compare(final T object1, final T object2) {
        return Integer.compare(this.entryIds.getInt(object1), this.entryIds.getInt(object2));
    }
    
    @Override
    public List<T> findAll(final String text) {
        final int integer2 = text.indexOf(58);
        if (integer2 == -1) {
            return this.byPath.findAll(text);
        }
        final List<T> list3 = this.byNamespace.findAll(text.substring(0, integer2).trim());
        final String string4 = text.substring(integer2 + 1).trim();
        final List<T> list4 = this.byPath.findAll(string4);
        return Lists.newArrayList(new Iterator<>(list3.iterator(), list4.iterator(), this::compare));
    }
    
    @Environment(EnvType.CLIENT)
    public static class Iterator<T> extends AbstractIterator<T>
    {
        private final PeekingIterator<T> a;
        private final PeekingIterator<T> b;
        private final Comparator<T> c;
        
        public Iterator(final java.util.Iterator<T> iterator1, final java.util.Iterator<T> iterator2, final Comparator<T> comparator) {
            this.a = Iterators.<T>peekingIterator(iterator1);
            this.b = Iterators.<T>peekingIterator(iterator2);
            this.c = comparator;
        }
        
        @Override
        protected T computeNext() {
            while (this.a.hasNext() && this.b.hasNext()) {
                final int integer1 = this.c.compare(this.a.peek(), this.b.peek());
                if (integer1 == 0) {
                    this.b.next();
                    return this.a.next();
                }
                if (integer1 < 0) {
                    this.a.next();
                }
                else {
                    this.b.next();
                }
            }
            return this.endOfData();
        }
    }
}
