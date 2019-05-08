package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;

final class SimpleDoubleListPair implements DoubleListPair
{
    private final DoubleArrayList mergedList;
    private final IntArrayList b;
    private final IntArrayList c;
    
    SimpleDoubleListPair(final DoubleList first, final DoubleList second, final boolean includeFirstOnly, final boolean includeSecondOnly) {
        int integer5 = 0;
        int integer6 = 0;
        double double7 = Double.NaN;
        final int integer7 = first.size();
        final int integer8 = second.size();
        final int integer9 = integer7 + integer8;
        this.mergedList = new DoubleArrayList(integer9);
        this.b = new IntArrayList(integer9);
        this.c = new IntArrayList(integer9);
        while (true) {
            final boolean boolean12 = integer5 < integer7;
            final boolean boolean13 = integer6 < integer8;
            if (!boolean12 && !boolean13) {
                break;
            }
            final boolean boolean14 = boolean12 && (!boolean13 || first.getDouble(integer5) < second.getDouble(integer6) + 1.0E-7);
            final double double8 = boolean14 ? first.getDouble(integer5++) : second.getDouble(integer6++);
            if ((integer5 == 0 || !boolean12) && !boolean14 && !includeSecondOnly) {
                continue;
            }
            if ((integer6 == 0 || !boolean13) && boolean14 && !includeFirstOnly) {
                continue;
            }
            if (double7 < double8 - 1.0E-7) {
                this.b.add(integer5 - 1);
                this.c.add(integer6 - 1);
                this.mergedList.add(double8);
                double7 = double8;
            }
            else {
                if (this.mergedList.isEmpty()) {
                    continue;
                }
                this.b.set(this.b.size() - 1, integer5 - 1);
                this.c.set(this.c.size() - 1, integer6 - 1);
            }
        }
        if (this.mergedList.isEmpty()) {
            this.mergedList.add(Math.min(first.getDouble(integer7 - 1), second.getDouble(integer8 - 1)));
        }
    }
    
    @Override
    public boolean forAllOverlappingSections(final SectionPairPredicate predicate) {
        for (int integer2 = 0; integer2 < this.mergedList.size() - 1; ++integer2) {
            if (!predicate.merge(this.b.getInt(integer2), this.c.getInt(integer2), integer2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public DoubleList getMergedList() {
        return (DoubleList)this.mergedList;
    }
}
