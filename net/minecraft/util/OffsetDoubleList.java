package net.minecraft.util;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class OffsetDoubleList extends AbstractDoubleList
{
    private final DoubleList oldList;
    private final double offset;
    
    public OffsetDoubleList(final DoubleList oldList, final double offset) {
        this.oldList = oldList;
        this.offset = offset;
    }
    
    public double getDouble(final int position) {
        return this.oldList.getDouble(position) + this.offset;
    }
    
    public int size() {
        return this.oldList.size();
    }
}
