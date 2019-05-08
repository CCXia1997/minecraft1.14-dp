package net.minecraft.util.math;

import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.util.hit.BlockHitResult;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class BoundingBox
{
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;
    
    public BoundingBox(final double x1, final double y1, final double z1, final double double7, final double double9, final double double11) {
        this.minX = Math.min(x1, double7);
        this.minY = Math.min(y1, double9);
        this.minZ = Math.min(z1, double11);
        this.maxX = Math.max(x1, double7);
        this.maxY = Math.max(y1, double9);
        this.maxZ = Math.max(z1, double11);
    }
    
    public BoundingBox(final BlockPos blockPos) {
        this(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1);
    }
    
    public BoundingBox(final BlockPos min, final BlockPos blockPos2) {
        this(min.getX(), min.getY(), min.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
    }
    
    public BoundingBox(final Vec3d min, final Vec3d max) {
        this(min.x, min.y, min.z, max.x, max.y, max.z);
    }
    
    public static BoundingBox from(final MutableIntBoundingBox mutableIntBoundingBox) {
        return new BoundingBox(mutableIntBoundingBox.minX, mutableIntBoundingBox.minY, mutableIntBoundingBox.minZ, mutableIntBoundingBox.maxX + 1, mutableIntBoundingBox.maxY + 1, mutableIntBoundingBox.maxZ + 1);
    }
    
    public double getMin(final Direction.Axis axis) {
        return axis.choose(this.minX, this.minY, this.minZ);
    }
    
    public double getMax(final Direction.Axis axis) {
        return axis.choose(this.maxX, this.maxY, this.maxZ);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoundingBox)) {
            return false;
        }
        final BoundingBox boundingBox2 = (BoundingBox)o;
        return Double.compare(boundingBox2.minX, this.minX) == 0 && Double.compare(boundingBox2.minY, this.minY) == 0 && Double.compare(boundingBox2.minZ, this.minZ) == 0 && Double.compare(boundingBox2.maxX, this.maxX) == 0 && Double.compare(boundingBox2.maxY, this.maxY) == 0 && Double.compare(boundingBox2.maxZ, this.maxZ) == 0;
    }
    
    @Override
    public int hashCode() {
        long long1 = Double.doubleToLongBits(this.minX);
        int integer3 = (int)(long1 ^ long1 >>> 32);
        long1 = Double.doubleToLongBits(this.minY);
        integer3 = 31 * integer3 + (int)(long1 ^ long1 >>> 32);
        long1 = Double.doubleToLongBits(this.minZ);
        integer3 = 31 * integer3 + (int)(long1 ^ long1 >>> 32);
        long1 = Double.doubleToLongBits(this.maxX);
        integer3 = 31 * integer3 + (int)(long1 ^ long1 >>> 32);
        long1 = Double.doubleToLongBits(this.maxY);
        integer3 = 31 * integer3 + (int)(long1 ^ long1 >>> 32);
        long1 = Double.doubleToLongBits(this.maxZ);
        integer3 = 31 * integer3 + (int)(long1 ^ long1 >>> 32);
        return integer3;
    }
    
    public BoundingBox shrink(final double x, final double y, final double z) {
        double double7 = this.minX;
        double double8 = this.minY;
        double double9 = this.minZ;
        double double10 = this.maxX;
        double double11 = this.maxY;
        double double12 = this.maxZ;
        if (x < 0.0) {
            double7 -= x;
        }
        else if (x > 0.0) {
            double10 -= x;
        }
        if (y < 0.0) {
            double8 -= y;
        }
        else if (y > 0.0) {
            double11 -= y;
        }
        if (z < 0.0) {
            double9 -= z;
        }
        else if (z > 0.0) {
            double12 -= z;
        }
        return new BoundingBox(double7, double8, double9, double10, double11, double12);
    }
    
    public BoundingBox stretch(final Vec3d vec3d) {
        return this.stretch(vec3d.x, vec3d.y, vec3d.z);
    }
    
    public BoundingBox stretch(final double x, final double y, final double z) {
        double double7 = this.minX;
        double double8 = this.minY;
        double double9 = this.minZ;
        double double10 = this.maxX;
        double double11 = this.maxY;
        double double12 = this.maxZ;
        if (x < 0.0) {
            double7 += x;
        }
        else if (x > 0.0) {
            double10 += x;
        }
        if (y < 0.0) {
            double8 += y;
        }
        else if (y > 0.0) {
            double11 += y;
        }
        if (z < 0.0) {
            double9 += z;
        }
        else if (z > 0.0) {
            double12 += z;
        }
        return new BoundingBox(double7, double8, double9, double10, double11, double12);
    }
    
    public BoundingBox expand(final double x, final double y, final double z) {
        final double double7 = this.minX - x;
        final double double8 = this.minY - y;
        final double double9 = this.minZ - z;
        final double double10 = this.maxX + x;
        final double double11 = this.maxY + y;
        final double double12 = this.maxZ + z;
        return new BoundingBox(double7, double8, double9, double10, double11, double12);
    }
    
    public BoundingBox expand(final double value) {
        return this.expand(value, value, value);
    }
    
    public BoundingBox intersection(final BoundingBox boundingBox) {
        final double double2 = Math.max(this.minX, boundingBox.minX);
        final double double3 = Math.max(this.minY, boundingBox.minY);
        final double double4 = Math.max(this.minZ, boundingBox.minZ);
        final double double5 = Math.min(this.maxX, boundingBox.maxX);
        final double double6 = Math.min(this.maxY, boundingBox.maxY);
        final double double7 = Math.min(this.maxZ, boundingBox.maxZ);
        return new BoundingBox(double2, double3, double4, double5, double6, double7);
    }
    
    public BoundingBox union(final BoundingBox boundingBox) {
        final double double2 = Math.min(this.minX, boundingBox.minX);
        final double double3 = Math.min(this.minY, boundingBox.minY);
        final double double4 = Math.min(this.minZ, boundingBox.minZ);
        final double double5 = Math.max(this.maxX, boundingBox.maxX);
        final double double6 = Math.max(this.maxY, boundingBox.maxY);
        final double double7 = Math.max(this.maxZ, boundingBox.maxZ);
        return new BoundingBox(double2, double3, double4, double5, double6, double7);
    }
    
    public BoundingBox offset(final double x, final double y, final double z) {
        return new BoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }
    
    public BoundingBox offset(final BlockPos blockPos) {
        return new BoundingBox(this.minX + blockPos.getX(), this.minY + blockPos.getY(), this.minZ + blockPos.getZ(), this.maxX + blockPos.getX(), this.maxY + blockPos.getY(), this.maxZ + blockPos.getZ());
    }
    
    public BoundingBox offset(final Vec3d vec3d) {
        return this.offset(vec3d.x, vec3d.y, vec3d.z);
    }
    
    public boolean intersects(final BoundingBox boundingBox) {
        return this.intersects(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    }
    
    public boolean intersects(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean intersects(final Vec3d vec3d1, final Vec3d vec3d2) {
        return this.intersects(Math.min(vec3d1.x, vec3d2.x), Math.min(vec3d1.y, vec3d2.y), Math.min(vec3d1.z, vec3d2.z), Math.max(vec3d1.x, vec3d2.x), Math.max(vec3d1.y, vec3d2.y), Math.max(vec3d1.z, vec3d2.z));
    }
    
    public boolean contains(final Vec3d vec) {
        return this.contains(vec.x, vec.y, vec.z);
    }
    
    public boolean contains(final double x, final double y, final double z) {
        return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
    }
    
    public double averageDimension() {
        final double double1 = this.getXSize();
        final double double2 = this.getYSize();
        final double double3 = this.getZSize();
        return (double1 + double2 + double3) / 3.0;
    }
    
    public double getXSize() {
        return this.maxX - this.minX;
    }
    
    public double getYSize() {
        return this.maxY - this.minY;
    }
    
    public double getZSize() {
        return this.maxZ - this.minZ;
    }
    
    public BoundingBox contract(final double value) {
        return this.expand(-value);
    }
    
    public Optional<Vec3d> rayTrace(final Vec3d vec3d1, final Vec3d vec3d2) {
        final double[] arr3 = { 1.0 };
        final double double4 = vec3d2.x - vec3d1.x;
        final double double5 = vec3d2.y - vec3d1.y;
        final double double6 = vec3d2.z - vec3d1.z;
        final Direction direction10 = a(this, vec3d1, arr3, null, double4, double5, double6);
        if (direction10 == null) {
            return Optional.<Vec3d>empty();
        }
        final double double7 = arr3[0];
        return Optional.<Vec3d>of(vec3d1.add(double7 * double4, double7 * double5, double7 * double6));
    }
    
    @Nullable
    public static BlockHitResult rayTrace(final Iterable<BoundingBox> boxes, final Vec3d from, final Vec3d to, final BlockPos blockPos) {
        final double[] arr5 = { 1.0 };
        Direction direction6 = null;
        final double double7 = to.x - from.x;
        final double double8 = to.y - from.y;
        final double double9 = to.z - from.z;
        for (final BoundingBox boundingBox14 : boxes) {
            direction6 = a(boundingBox14.offset(blockPos), from, arr5, direction6, double7, double8, double9);
        }
        if (direction6 == null) {
            return null;
        }
        final double double10 = arr5[0];
        return new BlockHitResult(from.add(double10 * double7, double10 * double8, double10 * double9), direction6, blockPos, false);
    }
    
    @Nullable
    private static Direction a(final BoundingBox boundingBox, final Vec3d vec3d, final double[] arr, @Nullable Direction direction, final double double5, final double double7, final double double9) {
        if (double5 > 1.0E-7) {
            direction = a(arr, direction, double5, double7, double9, boundingBox.minX, boundingBox.minY, boundingBox.maxY, boundingBox.minZ, boundingBox.maxZ, Direction.WEST, vec3d.x, vec3d.y, vec3d.z);
        }
        else if (double5 < -1.0E-7) {
            direction = a(arr, direction, double5, double7, double9, boundingBox.maxX, boundingBox.minY, boundingBox.maxY, boundingBox.minZ, boundingBox.maxZ, Direction.EAST, vec3d.x, vec3d.y, vec3d.z);
        }
        if (double7 > 1.0E-7) {
            direction = a(arr, direction, double7, double9, double5, boundingBox.minY, boundingBox.minZ, boundingBox.maxZ, boundingBox.minX, boundingBox.maxX, Direction.DOWN, vec3d.y, vec3d.z, vec3d.x);
        }
        else if (double7 < -1.0E-7) {
            direction = a(arr, direction, double7, double9, double5, boundingBox.maxY, boundingBox.minZ, boundingBox.maxZ, boundingBox.minX, boundingBox.maxX, Direction.UP, vec3d.y, vec3d.z, vec3d.x);
        }
        if (double9 > 1.0E-7) {
            direction = a(arr, direction, double9, double5, double7, boundingBox.minZ, boundingBox.minX, boundingBox.maxX, boundingBox.minY, boundingBox.maxY, Direction.NORTH, vec3d.z, vec3d.x, vec3d.y);
        }
        else if (double9 < -1.0E-7) {
            direction = a(arr, direction, double9, double5, double7, boundingBox.maxZ, boundingBox.minX, boundingBox.maxX, boundingBox.minY, boundingBox.maxY, Direction.SOUTH, vec3d.z, vec3d.x, vec3d.y);
        }
        return direction;
    }
    
    @Nullable
    private static Direction a(final double[] arr, @Nullable final Direction direction2, final double double3, final double double5, final double double7, final double double9, final double double11, final double double13, final double double15, final double double17, final Direction maxZ, final double double20, final double double22, final double double24) {
        final double double25 = (double9 - double20) / double3;
        final double double26 = double22 + double25 * double5;
        final double double27 = double24 + double25 * double7;
        if (0.0 < double25 && double25 < arr[0] && double11 - 1.0E-7 < double26 && double26 < double13 + 1.0E-7 && double15 - 1.0E-7 < double27 && double27 < double17 + 1.0E-7) {
            arr[0] = double25;
            return maxZ;
        }
        return direction2;
    }
    
    @Override
    public String toString() {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isValid() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }
    
    public Vec3d getCenter() {
        return new Vec3d(MathHelper.lerp(0.5, this.minX, this.maxX), MathHelper.lerp(0.5, this.minY, this.maxY), MathHelper.lerp(0.5, this.minZ, this.maxZ));
    }
}
