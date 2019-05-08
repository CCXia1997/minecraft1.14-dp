package net.minecraft.container;

public class ArrayPropertyDelegate implements PropertyDelegate
{
    private final int[] data;
    
    public ArrayPropertyDelegate(final int size) {
        this.data = new int[size];
    }
    
    @Override
    public int get(final int key) {
        return this.data[key];
    }
    
    @Override
    public void set(final int key, final int value) {
        this.data[key] = value;
    }
    
    @Override
    public int size() {
        return this.data.length;
    }
}
