package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;

public final class OffsetVoxelShape extends VoxelShape
{
    private final int xOffset;
    private final int yOffset;
    private final int zOffset;
    
    public OffsetVoxelShape(final VoxelSet shape, final int xOffset, final int yOffset, final int zOffset) {
        super(shape);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }
    
    @Override
    protected DoubleList getIncludedPoints(final Direction.Axis axis) {
        return (DoubleList)new OffsetFractionalDoubleList(this.voxels.getSize(axis), axis.choose(this.xOffset, this.yOffset, this.zOffset));
    }
}
