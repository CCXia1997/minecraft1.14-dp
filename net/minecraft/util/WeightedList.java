package net.minecraft.util;

import java.util.function.Function;
import java.util.stream.Stream;
import java.util.Comparator;
import com.google.common.collect.Lists;
import java.util.Random;
import java.util.List;

public class WeightedList<U>
{
    private final List<Entry<? extends U>> entries;
    private final Random random;
    
    public WeightedList() {
        this(new Random());
    }
    
    public WeightedList(final Random random) {
        this.entries = Lists.newArrayList();
        this.random = random;
    }
    
    public void add(final U object, final int integer) {
        this.entries.add(new Entry<U>(object, integer));
    }
    
    public void shuffle() {
        this.entries.forEach(entry -> entry.setShuffledOrder(this.random.nextFloat()));
        this.entries.sort(Comparator.comparingDouble(Entry::getShuffledOrder));
    }
    
    public Stream<? extends U> stream() {
        return this.entries.stream().map(Entry::getElement);
    }
    
    @Override
    public String toString() {
        return "WeightedList[" + this.entries + "]";
    }
    
    class Entry<T>
    {
        private final T b;
        private final int weight;
        private double shuffledOrder;
        
        private Entry(final T object, final int integer) {
            this.weight = integer;
            this.b = object;
        }
        
        public double getShuffledOrder() {
            return this.shuffledOrder;
        }
        
        public void setShuffledOrder(final float random) {
            this.shuffledOrder = -Math.pow(random, 1.0f / this.weight);
        }
        
        public T getElement() {
            return this.b;
        }
        
        @Override
        public String toString() {
            return "" + this.weight + ":" + this.b;
        }
    }
}
