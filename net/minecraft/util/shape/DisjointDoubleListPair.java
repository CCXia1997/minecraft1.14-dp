package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class DisjointDoubleListPair extends AbstractDoubleList implements DoubleListPair
{
    private final DoubleList first;
    private final DoubleList second;
    private final boolean c;
    
    public DisjointDoubleListPair(final DoubleList first, final DoubleList second, final boolean boolean3) {
        this.first = first;
        this.second = second;
        this.c = boolean3;
    }
    
    public int size() {
        return this.first.size() + this.second.size();
    }
    
    public boolean forAllOverlappingSections(final SectionPairPredicate predicate) {
        if (this.c) {
            return this.b((integer2, integer3, integer4) -> predicate.merge(integer3, integer2, integer4));
        }
        return this.b(predicate);
    }
    
    private boolean b(final SectionPairPredicate sectionPairPredicate) {
        final int integer2 = this.first.size() - 1;
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            if (!sectionPairPredicate.merge(integer3, -1, integer3)) {
                return false;
            }
        }
        if (!sectionPairPredicate.merge(integer2, -1, integer2)) {
            return false;
        }
        for (int integer3 = 0; integer3 < this.second.size(); ++integer3) {
            if (!sectionPairPredicate.merge(integer2, integer3, integer2 + 1 + integer3)) {
                return false;
            }
        }
        return true;
    }
    
    public double getDouble(final int position) {
        if (position < this.first.size()) {
            return this.first.getDouble(position);
        }
        return this.second.getDouble(position - this.first.size());
    }
    
    public DoubleList getMergedList() {
        return (DoubleList)this;
    }
}
