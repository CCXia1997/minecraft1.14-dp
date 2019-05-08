package net.minecraft.util.shape;

import net.minecraft.util.math.Direction;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import java.util.Arrays;
import it.unimi.dsi.fastutil.doubles.DoubleList;

final class ArrayVoxelShape extends VoxelShape
{
    private final DoubleList xPoints;
    private final DoubleList yPoints;
    private final DoubleList zPoints;
    
    ArrayVoxelShape(final VoxelSet shape, final double[] xPoints, final double[] yPoints, final double[] zPoints) {
        this(shape, (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(xPoints, shape.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(yPoints, shape.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(zPoints, shape.getZSize() + 1)));
    }
    
    ArrayVoxelShape(final VoxelSet shape, final DoubleList xPoints, final DoubleList yPoints, final DoubleList zPoints) {
        super(shape);
        final int integer5 = shape.getXSize() + 1;
        final int integer6 = shape.getYSize() + 1;
        final int integer7 = shape.getZSize() + 1;
        if (integer5 != xPoints.size() || integer6 != yPoints.size() || integer7 != zPoints.size()) {
            throw new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.");
        }
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.zPoints = zPoints;
    }
    
    @Override
    protected DoubleList getIncludedPoints(final Direction.Axis axis) {
        switch (axis) {
            case X: {
                return this.xPoints;
            }
            case Y: {
                return this.yPoints;
            }
            case Z: {
                return this.zPoints;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
}
