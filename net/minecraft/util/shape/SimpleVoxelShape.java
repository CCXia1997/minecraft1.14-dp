package net.minecraft.util.shape;

import net.minecraft.util.math.MathHelper;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;

final class SimpleVoxelShape extends VoxelShape
{
    SimpleVoxelShape(final VoxelSet voxels) {
        super(voxels);
    }
    
    @Override
    protected DoubleList getIncludedPoints(final Direction.Axis axis) {
        return (DoubleList)new FractionalDoubleList(this.voxels.getSize(axis));
    }
    
    @Override
    protected int getCoordIndex(final Direction.Axis axis, final double coord) {
        final int integer4 = this.voxels.getSize(axis);
        return MathHelper.clamp(MathHelper.floor(coord * integer4), -1, integer4);
    }
}
