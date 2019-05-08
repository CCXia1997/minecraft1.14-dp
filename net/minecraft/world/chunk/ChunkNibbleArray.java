package net.minecraft.world.chunk;

import javax.annotation.Nullable;

public class ChunkNibbleArray
{
    @Nullable
    protected byte[] byteArray;
    
    public ChunkNibbleArray() {
    }
    
    public ChunkNibbleArray(final byte[] arr) {
        this.byteArray = arr;
        if (arr.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + arr.length);
        }
    }
    
    protected ChunkNibbleArray(final int integer) {
        this.byteArray = new byte[integer];
    }
    
    public int get(final int x, final int y, final int z) {
        return this.get(this.getIndex(x, y, z));
    }
    
    public void set(final int x, final int y, final int z, final int value) {
        this.set(this.getIndex(x, y, z), value);
    }
    
    protected int getIndex(final int x, final int y, final int z) {
        return y << 8 | z << 4 | x;
    }
    
    private int get(final int integer) {
        if (this.byteArray == null) {
            return 0;
        }
        final int integer2 = this.divideByTwo(integer);
        if (this.isEven(integer)) {
            return this.byteArray[integer2] & 0xF;
        }
        return this.byteArray[integer2] >> 4 & 0xF;
    }
    
    private void set(final int index, final int value) {
        if (this.byteArray == null) {
            this.byteArray = new byte[2048];
        }
        final int integer3 = this.divideByTwo(index);
        if (this.isEven(index)) {
            this.byteArray[integer3] = (byte)((this.byteArray[integer3] & 0xF0) | (value & 0xF));
        }
        else {
            this.byteArray[integer3] = (byte)((this.byteArray[integer3] & 0xF) | (value & 0xF) << 4);
        }
    }
    
    private boolean isEven(final int n) {
        return (n & 0x1) == 0x0;
    }
    
    private int divideByTwo(final int n) {
        return n >> 1;
    }
    
    public byte[] asByteArray() {
        if (this.byteArray == null) {
            this.byteArray = new byte[2048];
        }
        return this.byteArray;
    }
    
    public ChunkNibbleArray copy() {
        if (this.byteArray == null) {
            return new ChunkNibbleArray();
        }
        return new ChunkNibbleArray(this.byteArray.clone());
    }
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder1 = new StringBuilder();
        for (int integer2 = 0; integer2 < 4096; ++integer2) {
            stringBuilder1.append(Integer.toHexString(this.get(integer2)));
            if ((integer2 & 0xF) == 0xF) {
                stringBuilder1.append("\n");
            }
            if ((integer2 & 0xFF) == 0xFF) {
                stringBuilder1.append("\n");
            }
        }
        return stringBuilder1.toString();
    }
    
    public boolean isUninitialized() {
        return this.byteArray == null;
    }
}
