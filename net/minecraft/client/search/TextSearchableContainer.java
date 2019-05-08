package net.minecraft.client.search;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.Identifier;
import java.util.stream.Stream;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextSearchableContainer<T> extends IdentifierSearchableContainer<T>
{
    protected SuffixArray<T> byText;
    private final Function<T, Stream<String>> textFinder;
    
    public TextSearchableContainer(final Function<T, Stream<String>> function1, final Function<T, Stream<Identifier>> function2) {
        super(function2);
        this.byText = new SuffixArray<T>();
        this.textFinder = function1;
    }
    
    @Override
    public void reload() {
        this.byText = new SuffixArray<T>();
        super.reload();
        this.byText.reload();
    }
    
    @Override
    protected void index(final T object) {
        super.index(object);
        this.textFinder.apply(object).forEach(string -> this.byText.add(object, string.toLowerCase(Locale.ROOT)));
    }
    
    @Override
    public List<T> findAll(final String text) {
        final int integer2 = text.indexOf(58);
        if (integer2 < 0) {
            return this.byText.findAll(text);
        }
        final List<T> list3 = this.byNamespace.findAll(text.substring(0, integer2).trim());
        final String string4 = text.substring(integer2 + 1).trim();
        final List<T> list4 = this.byPath.findAll(string4);
        final List<T> list5 = this.byText.findAll(string4);
        return Lists.newArrayList(new IdentifierSearchableContainer.Iterator<>(list3.iterator(), new Iterator<>(list4.iterator(), list5.iterator(), this::compare), this::compare));
    }
    
    @Environment(EnvType.CLIENT)
    static class Iterator<T> extends AbstractIterator<T>
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
            final boolean boolean1 = !this.a.hasNext();
            final boolean boolean2 = !this.b.hasNext();
            if (boolean1 && boolean2) {
                return this.endOfData();
            }
            if (boolean1) {
                return this.b.next();
            }
            if (boolean2) {
                return this.a.next();
            }
            final int integer3 = this.c.compare(this.a.peek(), this.b.peek());
            if (integer3 == 0) {
                this.b.next();
            }
            return (integer3 <= 0) ? this.a.next() : this.b.next();
        }
    }
}
