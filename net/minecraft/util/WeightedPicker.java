package net.minecraft.util;

import java.util.Random;
import java.util.List;

public class WeightedPicker
{
    public static int getWeightSum(final List<? extends Entry> list) {
        int integer2 = 0;
        for (int integer3 = 0, integer4 = list.size(); integer3 < integer4; ++integer3) {
            final Entry entry5 = (Entry)list.get(integer3);
            integer2 += entry5.weight;
        }
        return integer2;
    }
    
    public static <T extends Entry> T getRandom(final Random random, final List<T> list, final int weightSum) {
        if (weightSum <= 0) {
            throw new IllegalArgumentException();
        }
        final int integer4 = random.nextInt(weightSum);
        return WeightedPicker.<T>getAt(list, integer4);
    }
    
    public static <T extends Entry> T getAt(final List<T> list, int pos) {
        for (int integer3 = 0, integer4 = list.size(); integer3 < integer4; ++integer3) {
            final T entry5 = list.get(integer3);
            pos -= entry5.weight;
            if (pos < 0) {
                return entry5;
            }
        }
        return null;
    }
    
    public static <T extends Entry> T getRandom(final Random random, final List<T> list) {
        return WeightedPicker.<T>getRandom(random, list, getWeightSum(list));
    }
    
    public static class Entry
    {
        protected final int weight;
        
        public Entry(final int integer) {
            this.weight = integer;
        }
    }
}
