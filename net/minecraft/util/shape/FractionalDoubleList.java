package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class FractionalDoubleList extends AbstractDoubleList
{
    private final int sectionCount;
    
    FractionalDoubleList(final int sectionCount) {
        this.sectionCount = sectionCount;
    }
    
    public double getDouble(final int position) {
        return position / (double)this.sectionCount;
    }
    
    public int size() {
        return this.sectionCount + 1;
    }
}
