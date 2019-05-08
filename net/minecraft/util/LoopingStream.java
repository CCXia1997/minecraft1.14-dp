package net.minecraft.util;

import java.util.stream.StreamSupport;
import java.util.function.Consumer;
import java.util.Spliterators;
import com.google.common.collect.Lists;
import java.util.stream.Stream;
import java.util.Spliterator;
import java.util.List;

public class LoopingStream<T>
{
    private final List<T> a;
    private final Spliterator<T> b;
    
    public LoopingStream(final Stream<T> stream) {
        this.a = Lists.newArrayList();
        this.b = stream.spliterator();
    }
    
    public Stream<T> getStream() {
        return StreamSupport.<T>stream((Spliterator<T>)new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
            private int b;
            
            @Override
            public boolean tryAdvance(final Consumer<? super T> consumer) {
                while (this.b >= LoopingStream.this.a.size()) {
                    if (!LoopingStream.this.b.tryAdvance(LoopingStream.this.a::add)) {
                        return false;
                    }
                }
                consumer.accept(LoopingStream.this.a.get(this.b++));
                return true;
            }
        }, false);
    }
}
