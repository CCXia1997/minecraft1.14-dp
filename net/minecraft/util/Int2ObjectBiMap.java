package net.minecraft.util;

import java.util.Arrays;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;

public class Int2ObjectBiMap<K> implements IndexedIterable<K>
{
    private static final Object empty;
    private K[] values;
    private int[] ids;
    private K[] idToValues;
    private int nextId;
    private int size;
    
    public Int2ObjectBiMap(int integer) {
        integer /= (int)0.8f;
        this.values = (K[])new Object[integer];
        this.ids = new int[integer];
        this.idToValues = (K[])new Object[integer];
    }
    
    public int getId(@Nullable final K object) {
        return this.getIdFromIndex(this.findIndex(object, this.getIdealIndex(object)));
    }
    
    @Nullable
    @Override
    public K get(final int index) {
        if (index < 0 || index >= this.idToValues.length) {
            return null;
        }
        return this.idToValues[index];
    }
    
    private int getIdFromIndex(final int integer) {
        if (integer == -1) {
            return -1;
        }
        return this.ids[integer];
    }
    
    public int add(final K object) {
        final int integer2 = this.nextId();
        this.put(object, integer2);
        return integer2;
    }
    
    private int nextId() {
        while (this.nextId < this.idToValues.length && this.idToValues[this.nextId] != null) {
            ++this.nextId;
        }
        return this.nextId;
    }
    
    private void resize(final int integer) {
        final K[] arr2 = this.values;
        final int[] arr3 = this.ids;
        this.values = (K[])new Object[integer];
        this.ids = new int[integer];
        this.idToValues = (K[])new Object[integer];
        this.nextId = 0;
        this.size = 0;
        for (int integer2 = 0; integer2 < arr2.length; ++integer2) {
            if (arr2[integer2] != null) {
                this.put(arr2[integer2], arr3[integer2]);
            }
        }
    }
    
    public void put(final K object, final int integer) {
        final int integer2 = Math.max(integer, this.size + 1);
        if (integer2 >= this.values.length * 0.8f) {
            int integer3;
            for (integer3 = this.values.length << 1; integer3 < integer; integer3 <<= 1) {}
            this.resize(integer3);
        }
        int integer3 = this.findFree(this.getIdealIndex(object));
        this.values[integer3] = object;
        this.ids[integer3] = integer;
        this.idToValues[integer] = object;
        ++this.size;
        if (integer == this.nextId) {
            ++this.nextId;
        }
    }
    
    private int getIdealIndex(@Nullable final K object) {
        return (MathHelper.f(System.identityHashCode(object)) & Integer.MAX_VALUE) % this.values.length;
    }
    
    private int findIndex(@Nullable final K object, final int integer) {
        for (int integer2 = integer; integer2 < this.values.length; ++integer2) {
            if (this.values[integer2] == object) {
                return integer2;
            }
            if (this.values[integer2] == Int2ObjectBiMap.empty) {
                return -1;
            }
        }
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            if (this.values[integer2] == object) {
                return integer2;
            }
            if (this.values[integer2] == Int2ObjectBiMap.empty) {
                return -1;
            }
        }
        return -1;
    }
    
    private int findFree(final int integer) {
        for (int integer2 = integer; integer2 < this.values.length; ++integer2) {
            if (this.values[integer2] == Int2ObjectBiMap.empty) {
                return integer2;
            }
        }
        for (int integer2 = 0; integer2 < integer; ++integer2) {
            if (this.values[integer2] == Int2ObjectBiMap.empty) {
                return integer2;
            }
        }
        throw new RuntimeException("Overflowed :(");
    }
    
    @Override
    public Iterator<K> iterator() {
        return Iterators.<K>filter(Iterators.<K>forArray(this.idToValues), Predicates.notNull());
    }
    
    public void clear() {
        Arrays.fill(this.values, null);
        Arrays.fill(this.idToValues, null);
        this.nextId = 0;
        this.size = 0;
    }
    
    public int size() {
        return this.size;
    }
    
    static {
        empty = null;
    }
}
