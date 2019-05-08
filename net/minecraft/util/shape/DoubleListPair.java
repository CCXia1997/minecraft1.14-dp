package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface DoubleListPair
{
    DoubleList getMergedList();
    
    boolean forAllOverlappingSections(final SectionPairPredicate arg1);
    
    public interface SectionPairPredicate
    {
        boolean merge(final int arg1, final int arg2, final int arg3);
    }
}
