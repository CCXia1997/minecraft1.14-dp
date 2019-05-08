package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class OffsetFractionalDoubleList extends AbstractDoubleList
{
    private final int count;
    private final int offset;
    
    OffsetFractionalDoubleList(final int count, final int offset) {
        this.count = count;
        this.offset = offset;
    }
    
    public double getDouble(final int position) {
        return this.offset + position;
    }
    
    public int size() {
        return this.count + 1;
    }
}
