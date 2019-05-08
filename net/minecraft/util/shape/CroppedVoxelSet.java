package net.minecraft.util.shape;

import net.minecraft.util.math.Direction;

final class CroppedVoxelSet extends VoxelSet
{
    private final VoxelSet parent;
    private final int xMin;
    private final int yMin;
    private final int zMin;
    private final int xMax;
    private final int yMax;
    private final int zMax;
    
    public CroppedVoxelSet(final VoxelSet parent, final int xMin, final int yMin, final int zMin, final int xMax, final int yMax, final int zMax) {
        super(xMax - xMin, yMax - yMin, zMax - zMin);
        this.parent = parent;
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;
    }
    
    @Override
    public boolean contains(final int x, final int y, final int z) {
        return this.parent.contains(this.xMin + x, this.yMin + y, this.zMin + z);
    }
    
    @Override
    public void set(final int x, final int y, final int z, final boolean resize, final boolean included) {
        this.parent.set(this.xMin + x, this.yMin + y, this.zMin + z, resize, included);
    }
    
    @Override
    public int getMin(final Direction.Axis axis) {
        return Math.max(0, this.parent.getMin(axis) - axis.choose(this.xMin, this.yMin, this.zMin));
    }
    
    @Override
    public int getMax(final Direction.Axis axis) {
        return Math.min(axis.choose(this.xMax, this.yMax, this.zMax), this.parent.getMax(axis) - axis.choose(this.xMin, this.yMin, this.zMin));
    }
}
