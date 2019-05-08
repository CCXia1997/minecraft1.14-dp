package net.minecraft.util.shape;

import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import java.util.BitSet;

public final class BitSetVoxelSet extends VoxelSet
{
    private final BitSet storage;
    private int xMin;
    private int yMin;
    private int zMin;
    private int xMax;
    private int yMax;
    private int zMax;
    
    public BitSetVoxelSet(final int xSize, final int ySize, final int zSize) {
        this(xSize, ySize, zSize, xSize, ySize, zSize, 0, 0, 0);
    }
    
    public BitSetVoxelSet(final int xMask, final int yMask, final int zMask, final int xMin, final int yMin, final int zMin, final int xMax, final int yMax, final int zMax) {
        super(xMask, yMask, zMask);
        this.storage = new BitSet(xMask * yMask * zMask);
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;
    }
    
    public BitSetVoxelSet(final VoxelSet other) {
        super(other.xSize, other.ySize, other.zSize);
        if (other instanceof BitSetVoxelSet) {
            this.storage = (BitSet)((BitSetVoxelSet)other).storage.clone();
        }
        else {
            this.storage = new BitSet(this.xSize * this.ySize * this.zSize);
            for (int integer2 = 0; integer2 < this.xSize; ++integer2) {
                for (int integer3 = 0; integer3 < this.ySize; ++integer3) {
                    for (int integer4 = 0; integer4 < this.zSize; ++integer4) {
                        if (other.contains(integer2, integer3, integer4)) {
                            this.storage.set(this.getIndex(integer2, integer3, integer4));
                        }
                    }
                }
            }
        }
        this.xMin = other.getMin(Direction.Axis.X);
        this.yMin = other.getMin(Direction.Axis.Y);
        this.zMin = other.getMin(Direction.Axis.Z);
        this.xMax = other.getMax(Direction.Axis.X);
        this.yMax = other.getMax(Direction.Axis.Y);
        this.zMax = other.getMax(Direction.Axis.Z);
    }
    
    protected int getIndex(final int x, final int y, final int z) {
        return (x * this.ySize + y) * this.zSize + z;
    }
    
    @Override
    public boolean contains(final int x, final int y, final int z) {
        return this.storage.get(this.getIndex(x, y, z));
    }
    
    @Override
    public void set(final int x, final int y, final int z, final boolean resize, final boolean included) {
        this.storage.set(this.getIndex(x, y, z), included);
        if (resize && included) {
            this.xMin = Math.min(this.xMin, x);
            this.yMin = Math.min(this.yMin, y);
            this.zMin = Math.min(this.zMin, z);
            this.xMax = Math.max(this.xMax, x + 1);
            this.yMax = Math.max(this.yMax, y + 1);
            this.zMax = Math.max(this.zMax, z + 1);
        }
    }
    
    @Override
    public boolean isEmpty() {
        return this.storage.isEmpty();
    }
    
    @Override
    public int getMin(final Direction.Axis axis) {
        return axis.choose(this.xMin, this.yMin, this.zMin);
    }
    
    @Override
    public int getMax(final Direction.Axis axis) {
        return axis.choose(this.xMax, this.yMax, this.zMax);
    }
    
    @Override
    protected boolean isColumnFull(final int minZ, final int maxZ, final int x, final int y) {
        return x >= 0 && y >= 0 && minZ >= 0 && x < this.xSize && y < this.ySize && maxZ <= this.zSize && this.storage.nextClearBit(this.getIndex(x, y, minZ)) >= this.getIndex(x, y, maxZ);
    }
    
    @Override
    protected void setColumn(final int minZ, final int maxZ, final int x, final int y, final boolean included) {
        this.storage.set(this.getIndex(x, y, minZ), this.getIndex(x, y, maxZ), included);
    }
    
    static BitSetVoxelSet combine(final VoxelSet first, final VoxelSet second, final DoubleListPair xPoints, final DoubleListPair yPoints, final DoubleListPair zPoints, final BooleanBiFunction function) {
        final BitSetVoxelSet bitSetVoxelSet7 = new BitSetVoxelSet(xPoints.getMergedList().size() - 1, yPoints.getMergedList().size() - 1, zPoints.getMergedList().size() - 1);
        final int[] arr8 = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE };
        final boolean[] arr9;
        final boolean[] arr10;
        final boolean boolean14;
        final BitSetVoxelSet set;
        final Object o;
        final Object o2;
        final boolean boolean13;
        final Object o3;
        final Object o4;
        final boolean boolean12;
        final Object o5;
        xPoints.forAllOverlappingSections((integer8, integer9, integer10) -> {
            arr9 = new boolean[] { false };
            boolean12 = yPoints.forAllOverlappingSections((integer11, integer12, integer13) -> {
                arr10 = new boolean[] { false };
                boolean13 = zPoints.forAllOverlappingSections((integer13, integer14, integer15) -> {
                    boolean14 = function.apply(first.inBoundsAndContains(integer8, integer11, integer13), second.inBoundsAndContains(integer9, integer12, integer14));
                    if (boolean14) {
                        set.storage.set(set.getIndex(integer10, integer13, integer15));
                        o[2] = Math.min(o[2], integer15);
                        o[5] = Math.max(o[5], integer15);
                        o2[0] = true;
                    }
                    return true;
                });
                if (arr10[0]) {
                    o3[1] = Math.min(o3[1], integer13);
                    o3[4] = Math.max(o3[4], integer13);
                    o4[0] = true;
                }
                return boolean13;
            });
            if (arr9[0]) {
                o5[0] = Math.min(o5[0], integer10);
                o5[3] = Math.max(o5[3], integer10);
            }
            return boolean12;
        });
        bitSetVoxelSet7.xMin = arr8[0];
        bitSetVoxelSet7.yMin = arr8[1];
        bitSetVoxelSet7.zMin = arr8[2];
        bitSetVoxelSet7.xMax = arr8[3] + 1;
        bitSetVoxelSet7.yMax = arr8[4] + 1;
        bitSetVoxelSet7.zMax = arr8[5] + 1;
        return bitSetVoxelSet7;
    }
}
