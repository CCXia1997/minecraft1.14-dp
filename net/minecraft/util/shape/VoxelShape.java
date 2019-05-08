package net.minecraft.util.shape;

import com.google.common.math.DoubleMath;
import javax.annotation.Nullable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisCycle;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.OffsetDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;

public abstract class VoxelShape
{
    protected final VoxelSet voxels;
    
    VoxelShape(final VoxelSet voxels) {
        this.voxels = voxels;
    }
    
    public double getMinimum(final Direction.Axis axis) {
        final int integer2 = this.voxels.getMin(axis);
        if (integer2 >= this.voxels.getSize(axis)) {
            return Double.POSITIVE_INFINITY;
        }
        return this.getCoord(axis, integer2);
    }
    
    public double getMaximum(final Direction.Axis axis) {
        final int integer2 = this.voxels.getMax(axis);
        if (integer2 <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.getCoord(axis, integer2);
    }
    
    public BoundingBox getBoundingBox() {
        if (this.isEmpty()) {
            throw new UnsupportedOperationException("No bounds for empty shape.");
        }
        return new BoundingBox(this.getMinimum(Direction.Axis.X), this.getMinimum(Direction.Axis.Y), this.getMinimum(Direction.Axis.Z), this.getMaximum(Direction.Axis.X), this.getMaximum(Direction.Axis.Y), this.getMaximum(Direction.Axis.Z));
    }
    
    protected double getCoord(final Direction.Axis axis, final int index) {
        return this.getIncludedPoints(axis).getDouble(index);
    }
    
    protected abstract DoubleList getIncludedPoints(final Direction.Axis arg1);
    
    public boolean isEmpty() {
        return this.voxels.isEmpty();
    }
    
    public VoxelShape offset(final double x, final double y, final double z) {
        if (this.isEmpty()) {
            return VoxelShapes.empty();
        }
        return new ArrayVoxelShape(this.voxels, (DoubleList)new OffsetDoubleList(this.getIncludedPoints(Direction.Axis.X), x), (DoubleList)new OffsetDoubleList(this.getIncludedPoints(Direction.Axis.Y), y), (DoubleList)new OffsetDoubleList(this.getIncludedPoints(Direction.Axis.Z), z));
    }
    
    public VoxelShape simplify() {
        final VoxelShape[] arr1 = { VoxelShapes.empty() };
        final Object o;
        this.forEachBox((double2, double4, double6, double8, double10, double12) -> o[0] = VoxelShapes.combine(o[0], VoxelShapes.cuboid(double2, double4, double6, double8, double10, double12), BooleanBiFunction.OR));
        return arr1[0];
    }
    
    @Environment(EnvType.CLIENT)
    public void forEachEdge(final VoxelShapes.BoxConsumer boxConsumer) {
        this.voxels.forEachEdge((integer2, integer3, integer4, integer5, integer6, integer7) -> boxConsumer.consume(this.getCoord(Direction.Axis.X, integer2), this.getCoord(Direction.Axis.Y, integer3), this.getCoord(Direction.Axis.Z, integer4), this.getCoord(Direction.Axis.X, integer5), this.getCoord(Direction.Axis.Y, integer6), this.getCoord(Direction.Axis.Z, integer7)), true);
    }
    
    public void forEachBox(final VoxelShapes.BoxConsumer boxConsumer) {
        this.voxels.forEachBox((integer2, integer3, integer4, integer5, integer6, integer7) -> boxConsumer.consume(this.getCoord(Direction.Axis.X, integer2), this.getCoord(Direction.Axis.Y, integer3), this.getCoord(Direction.Axis.Z, integer4), this.getCoord(Direction.Axis.X, integer5), this.getCoord(Direction.Axis.Y, integer6), this.getCoord(Direction.Axis.Z, integer7)), true);
    }
    
    public List<BoundingBox> getBoundingBoxes() {
        final List<BoundingBox> list1 = Lists.newArrayList();
        this.forEachBox((double2, double4, double6, double8, double10, double12) -> list1.add(new BoundingBox(double2, double4, double6, double8, double10, double12)));
        return list1;
    }
    
    @Environment(EnvType.CLIENT)
    public double a(final Direction.Axis axis, final double double2, final double double4) {
        final Direction.Axis axis2 = AxisCycle.NEXT.cycle(axis);
        final Direction.Axis axis3 = AxisCycle.PREVIOUS.cycle(axis);
        final int integer8 = this.getCoordIndex(axis2, double2);
        final int integer9 = this.getCoordIndex(axis3, double4);
        final int integer10 = this.voxels.a(axis, integer8, integer9);
        if (integer10 >= this.voxels.getSize(axis)) {
            return Double.POSITIVE_INFINITY;
        }
        return this.getCoord(axis, integer10);
    }
    
    @Environment(EnvType.CLIENT)
    public double b(final Direction.Axis axis, final double double2, final double double4) {
        final Direction.Axis axis2 = AxisCycle.NEXT.cycle(axis);
        final Direction.Axis axis3 = AxisCycle.PREVIOUS.cycle(axis);
        final int integer8 = this.getCoordIndex(axis2, double2);
        final int integer9 = this.getCoordIndex(axis3, double4);
        final int integer10 = this.voxels.b(axis, integer8, integer9);
        if (integer10 <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.getCoord(axis, integer10);
    }
    
    protected int getCoordIndex(final Direction.Axis axis, final double coord) {
        return MathHelper.binarySearch(0, this.voxels.getSize(axis) + 1, integer4 -> {
            if (integer4 < 0) {
                return false;
            }
            else if (integer4 > this.voxels.getSize(axis)) {
                return true;
            }
            else {
                return coord < this.getCoord(axis, integer4);
            }
        }) - 1;
    }
    
    protected boolean contains(final double x, final double y, final double z) {
        return this.voxels.inBoundsAndContains(this.getCoordIndex(Direction.Axis.X, x), this.getCoordIndex(Direction.Axis.Y, y), this.getCoordIndex(Direction.Axis.Z, z));
    }
    
    @Nullable
    public BlockHitResult rayTrace(final Vec3d start, final Vec3d end, final BlockPos pos) {
        if (this.isEmpty()) {
            return null;
        }
        final Vec3d vec3d4 = end.subtract(start);
        if (vec3d4.lengthSquared() < 1.0E-7) {
            return null;
        }
        final Vec3d vec3d5 = start.add(vec3d4.multiply(0.001));
        if (this.contains(vec3d5.x - pos.getX(), vec3d5.y - pos.getY(), vec3d5.z - pos.getZ())) {
            return new BlockHitResult(vec3d5, Direction.getFacing(vec3d4.x, vec3d4.y, vec3d4.z).getOpposite(), pos, true);
        }
        return BoundingBox.rayTrace(this.getBoundingBoxes(), start, end, pos);
    }
    
    public VoxelShape getFace(final Direction facing) {
        if (this.isEmpty() || this == VoxelShapes.fullCube()) {
            return this;
        }
        final Direction.Axis axis2 = facing.getAxis();
        final Direction.AxisDirection axisDirection3 = facing.getDirection();
        final DoubleList doubleList4 = this.getIncludedPoints(axis2);
        if (doubleList4.size() == 2 && DoubleMath.fuzzyEquals(doubleList4.getDouble(0), 0.0, 1.0E-7) && DoubleMath.fuzzyEquals(doubleList4.getDouble(1), 1.0, 1.0E-7)) {
            return this;
        }
        final int integer5 = this.getCoordIndex(axis2, (axisDirection3 == Direction.AxisDirection.POSITIVE) ? 0.9999999 : 1.0E-7);
        return new SliceVoxelShape(this, axis2, integer5);
    }
    
    public double a(final Direction.Axis axis, final BoundingBox box, final double double3) {
        return this.a(AxisCycle.between(axis, Direction.Axis.X), box, double3);
    }
    
    protected double a(final AxisCycle axisCycle, final BoundingBox box, double maxDist) {
        if (this.isEmpty()) {
            return maxDist;
        }
        if (Math.abs(maxDist) < 1.0E-7) {
            return 0.0;
        }
        final AxisCycle axisCycle2 = axisCycle.opposite();
        final Direction.Axis axis6 = axisCycle2.cycle(Direction.Axis.X);
        final Direction.Axis axis7 = axisCycle2.cycle(Direction.Axis.Y);
        final Direction.Axis axis8 = axisCycle2.cycle(Direction.Axis.Z);
        final double double9 = box.getMax(axis6);
        final double double10 = box.getMin(axis6);
        final int integer13 = this.getCoordIndex(axis6, double10 + 1.0E-7);
        final int integer14 = this.getCoordIndex(axis6, double9 - 1.0E-7);
        final int integer15 = Math.max(0, this.getCoordIndex(axis7, box.getMin(axis7) + 1.0E-7));
        final int integer16 = Math.min(this.voxels.getSize(axis7), this.getCoordIndex(axis7, box.getMax(axis7) - 1.0E-7) + 1);
        final int integer17 = Math.max(0, this.getCoordIndex(axis8, box.getMin(axis8) + 1.0E-7));
        final int integer18 = Math.min(this.voxels.getSize(axis8), this.getCoordIndex(axis8, box.getMax(axis8) - 1.0E-7) + 1);
        final int integer19 = this.voxels.getSize(axis6);
        if (maxDist > 0.0) {
            for (int integer20 = integer14 + 1; integer20 < integer19; ++integer20) {
                for (int integer21 = integer15; integer21 < integer16; ++integer21) {
                    for (int integer22 = integer17; integer22 < integer18; ++integer22) {
                        if (this.voxels.inBoundsAndContains(axisCycle2, integer20, integer21, integer22)) {
                            final double double11 = this.getCoord(axis6, integer20) - double9;
                            if (double11 >= -1.0E-7) {
                                maxDist = Math.min(maxDist, double11);
                            }
                            return maxDist;
                        }
                    }
                }
            }
        }
        else if (maxDist < 0.0) {
            for (int integer20 = integer13 - 1; integer20 >= 0; --integer20) {
                for (int integer21 = integer15; integer21 < integer16; ++integer21) {
                    for (int integer22 = integer17; integer22 < integer18; ++integer22) {
                        if (this.voxels.inBoundsAndContains(axisCycle2, integer20, integer21, integer22)) {
                            final double double11 = this.getCoord(axis6, integer20 + 1) - double10;
                            if (double11 <= 1.0E-7) {
                                maxDist = Math.max(maxDist, double11);
                            }
                            return maxDist;
                        }
                    }
                }
            }
        }
        return maxDist;
    }
    
    @Override
    public String toString() {
        return this.isEmpty() ? "EMPTY" : ("VoxelShape[" + this.getBoundingBox() + "]");
    }
}
