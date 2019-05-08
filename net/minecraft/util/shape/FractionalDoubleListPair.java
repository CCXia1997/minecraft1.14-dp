package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import com.google.common.math.IntMath;

public final class FractionalDoubleListPair implements DoubleListPair
{
    private final FractionalDoubleList mergedList;
    private final int firstSectionCount;
    private final int secondSectionCount;
    private final int gcd;
    
    FractionalDoubleListPair(final int firstSectionCount, final int secondSectionCount) {
        this.mergedList = new FractionalDoubleList((int)VoxelShapes.lcm(firstSectionCount, secondSectionCount));
        this.firstSectionCount = firstSectionCount;
        this.secondSectionCount = secondSectionCount;
        this.gcd = IntMath.gcd(firstSectionCount, secondSectionCount);
    }
    
    @Override
    public boolean forAllOverlappingSections(final SectionPairPredicate predicate) {
        final int integer2 = this.firstSectionCount / this.gcd;
        final int integer3 = this.secondSectionCount / this.gcd;
        for (int integer4 = 0; integer4 <= this.mergedList.size(); ++integer4) {
            if (!predicate.merge(integer4 / integer3, integer4 / integer2, integer4)) {
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
