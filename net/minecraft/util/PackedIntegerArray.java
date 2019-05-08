package net.minecraft.util;

import org.apache.commons.lang3.Validate;
import net.minecraft.util.math.MathHelper;

public class PackedIntegerArray
{
    private final long[] storage;
    private final int elementBits;
    private final long maxValue;
    private final int size;
    
    public PackedIntegerArray(final int elementBits, final int size) {
        this(elementBits, size, new long[MathHelper.roundUp(size * elementBits, 64) / 64]);
    }
    
    public PackedIntegerArray(final int elementBits, final int size, final long[] storage) {
        Validate.inclusiveBetween(1L, 32L, (long)elementBits);
        this.size = size;
        this.elementBits = elementBits;
        this.storage = storage;
        this.maxValue = (1L << elementBits) - 1L;
        final int integer4 = MathHelper.roundUp(size * elementBits, 64) / 64;
        if (storage.length != integer4) {
            throw new RuntimeException("Invalid length given for storage, got: " + storage.length + " but expected: " + integer4);
        }
    }
    
    public int setAndGetOldValue(final int index, final int value) {
        Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
        Validate.inclusiveBetween(0L, this.maxValue, (long)value);
        final int integer3 = index * this.elementBits;
        final int integer4 = integer3 >> 6;
        final int integer5 = (index + 1) * this.elementBits - 1 >> 6;
        final int integer6 = integer3 ^ integer4 << 6;
        int integer7 = 0;
        integer7 |= (int)(this.storage[integer4] >>> integer6 & this.maxValue);
        this.storage[integer4] = ((this.storage[integer4] & ~(this.maxValue << integer6)) | ((long)value & this.maxValue) << integer6);
        if (integer4 != integer5) {
            final int integer8 = 64 - integer6;
            final int integer9 = this.elementBits - integer8;
            integer7 |= (int)(this.storage[integer5] << integer8 & this.maxValue);
            this.storage[integer5] = (this.storage[integer5] >>> integer9 << integer9 | ((long)value & this.maxValue) >> integer8);
        }
        return integer7;
    }
    
    public void set(final int index, final int value) {
        Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
        Validate.inclusiveBetween(0L, this.maxValue, (long)value);
        final int integer3 = index * this.elementBits;
        final int integer4 = integer3 >> 6;
        final int integer5 = (index + 1) * this.elementBits - 1 >> 6;
        final int integer6 = integer3 ^ integer4 << 6;
        this.storage[integer4] = ((this.storage[integer4] & ~(this.maxValue << integer6)) | ((long)value & this.maxValue) << integer6);
        if (integer4 != integer5) {
            final int integer7 = 64 - integer6;
            final int integer8 = this.elementBits - integer7;
            this.storage[integer5] = (this.storage[integer5] >>> integer8 << integer8 | ((long)value & this.maxValue) >> integer7);
        }
    }
    
    public int get(final int index) {
        Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)index);
        final int integer2 = index * this.elementBits;
        final int integer3 = integer2 >> 6;
        final int integer4 = (index + 1) * this.elementBits - 1 >> 6;
        final int integer5 = integer2 ^ integer3 << 6;
        if (integer3 == integer4) {
            return (int)(this.storage[integer3] >>> integer5 & this.maxValue);
        }
        final int integer6 = 64 - integer5;
        return (int)((this.storage[integer3] >>> integer5 | this.storage[integer4] << integer6) & this.maxValue);
    }
    
    public long[] getStorage() {
        return this.storage;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getElementBits() {
        return this.elementBits;
    }
}
