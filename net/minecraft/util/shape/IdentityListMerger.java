package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class IdentityListMerger implements DoubleListPair
{
    private final DoubleList merged;
    
    public IdentityListMerger(final DoubleList doubleList) {
        this.merged = doubleList;
    }
    
    @Override
    public boolean forAllOverlappingSections(final SectionPairPredicate predicate) {
        for (int integer2 = 0; integer2 <= this.merged.size(); ++integer2) {
            if (!predicate.merge(integer2, integer2, integer2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public DoubleList getMergedList() {
        return this.merged;
    }
}
