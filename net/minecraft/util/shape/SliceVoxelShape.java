package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;

public class SliceVoxelShape extends VoxelShape
{
    private final VoxelShape shape;
    private final Direction.Axis axis;
    private final DoubleList points;
    
    public SliceVoxelShape(final VoxelShape shape, final Direction.Axis axis, final int integer) {
        super(createVoxelSet(shape.voxels, axis, integer));
        this.points = (DoubleList)new FractionalDoubleList(1);
        this.shape = shape;
        this.axis = axis;
    }
    
    private static VoxelSet createVoxelSet(final VoxelSet voxelSet, final Direction.Axis axis, final int integer) {
        return new CroppedVoxelSet(voxelSet, axis.choose(integer, 0, 0), axis.choose(0, integer, 0), axis.choose(0, 0, integer), axis.choose(integer + 1, voxelSet.xSize, voxelSet.xSize), axis.choose(voxelSet.ySize, integer + 1, voxelSet.ySize), axis.choose(voxelSet.zSize, voxelSet.zSize, integer + 1));
    }
    
    @Override
    protected DoubleList getIncludedPoints(final Direction.Axis axis) {
        if (axis == this.axis) {
            return this.points;
        }
        return this.shape.getIncludedPoints(axis);
    }
}
