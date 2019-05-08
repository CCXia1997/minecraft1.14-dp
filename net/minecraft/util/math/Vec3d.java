package net.minecraft.util.math;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Vec3d implements Position
{
    public static final Vec3d ZERO;
    public final double x;
    public final double y;
    public final double z;
    
    public Vec3d(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vec3d(final Vec3i vec3i) {
        this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }
    
    public Vec3d reverseSubtract(final Vec3d vec3d) {
        return new Vec3d(vec3d.x - this.x, vec3d.y - this.y, vec3d.z - this.z);
    }
    
    public Vec3d normalize() {
        final double double1 = MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (double1 < 1.0E-4) {
            return Vec3d.ZERO;
        }
        return new Vec3d(this.x / double1, this.y / double1, this.z / double1);
    }
    
    public double dotProduct(final Vec3d vec3d) {
        return this.x * vec3d.x + this.y * vec3d.y + this.z * vec3d.z;
    }
    
    public Vec3d crossProduct(final Vec3d vec3d) {
        return new Vec3d(this.y * vec3d.z - this.z * vec3d.y, this.z * vec3d.x - this.x * vec3d.z, this.x * vec3d.y - this.y * vec3d.x);
    }
    
    public Vec3d subtract(final Vec3d vec3d) {
        return this.subtract(vec3d.x, vec3d.y, vec3d.z);
    }
    
    public Vec3d subtract(final double x, final double double3, final double double5) {
        return this.add(-x, -double3, -double5);
    }
    
    public Vec3d add(final Vec3d vec3d) {
        return this.add(vec3d.x, vec3d.y, vec3d.z);
    }
    
    public Vec3d add(final double x, final double y, final double z) {
        return new Vec3d(this.x + x, this.y + y, this.z + z);
    }
    
    public double distanceTo(final Vec3d vec3d) {
        final double double2 = vec3d.x - this.x;
        final double double3 = vec3d.y - this.y;
        final double double4 = vec3d.z - this.z;
        return MathHelper.sqrt(double2 * double2 + double3 * double3 + double4 * double4);
    }
    
    public double squaredDistanceTo(final Vec3d vec3d) {
        final double double2 = vec3d.x - this.x;
        final double double3 = vec3d.y - this.y;
        final double double4 = vec3d.z - this.z;
        return double2 * double2 + double3 * double3 + double4 * double4;
    }
    
    public double squaredDistanceTo(final double x, final double y, final double z) {
        final double double7 = x - this.x;
        final double double8 = y - this.y;
        final double double9 = z - this.z;
        return double7 * double7 + double8 * double8 + double9 * double9;
    }
    
    public Vec3d multiply(final double mult) {
        return this.multiply(mult, mult, mult);
    }
    
    @Environment(EnvType.CLIENT)
    public Vec3d negate() {
        return this.multiply(-1.0);
    }
    
    public Vec3d multiply(final Vec3d mult) {
        return this.multiply(mult.x, mult.y, mult.z);
    }
    
    public Vec3d multiply(final double multX, final double multY, final double multZ) {
        return new Vec3d(this.x * multX, this.y * multY, this.z * multZ);
    }
    
    public double length() {
        return MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vec3d)) {
            return false;
        }
        final Vec3d vec3d2 = (Vec3d)o;
        return Double.compare(vec3d2.x, this.x) == 0 && Double.compare(vec3d2.y, this.y) == 0 && Double.compare(vec3d2.z, this.z) == 0;
    }
    
    @Override
    public int hashCode() {
        long long2 = Double.doubleToLongBits(this.x);
        int integer1 = (int)(long2 ^ long2 >>> 32);
        long2 = Double.doubleToLongBits(this.y);
        integer1 = 31 * integer1 + (int)(long2 ^ long2 >>> 32);
        long2 = Double.doubleToLongBits(this.z);
        integer1 = 31 * integer1 + (int)(long2 ^ long2 >>> 32);
        return integer1;
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
    
    public Vec3d rotateX(final float float1) {
        final float float2 = MathHelper.cos(float1);
        final float float3 = MathHelper.sin(float1);
        final double double4 = this.x;
        final double double5 = this.y * float2 + this.z * float3;
        final double double6 = this.z * float2 - this.y * float3;
        return new Vec3d(double4, double5, double6);
    }
    
    public Vec3d rotateY(final float float1) {
        final float float2 = MathHelper.cos(float1);
        final float float3 = MathHelper.sin(float1);
        final double double4 = this.x * float2 + this.z * float3;
        final double double5 = this.y;
        final double double6 = this.z * float2 - this.x * float3;
        return new Vec3d(double4, double5, double6);
    }
    
    @Environment(EnvType.CLIENT)
    public static Vec3d fromPolar(final Vec2f polar) {
        return fromPolar(polar.x, polar.y);
    }
    
    @Environment(EnvType.CLIENT)
    public static Vec3d fromPolar(final float pitch, final float yaw) {
        final float float3 = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f);
        final float float4 = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f);
        final float float5 = -MathHelper.cos(-pitch * 0.017453292f);
        final float float6 = MathHelper.sin(-pitch * 0.017453292f);
        return new Vec3d(float4 * float5, float6, float3 * float5);
    }
    
    public Vec3d floorAlongAxes(final EnumSet<Direction.Axis> axes) {
        final double double2 = axes.contains(Direction.Axis.X) ? MathHelper.floor(this.x) : this.x;
        final double double3 = axes.contains(Direction.Axis.Y) ? MathHelper.floor(this.y) : this.y;
        final double double4 = axes.contains(Direction.Axis.Z) ? MathHelper.floor(this.z) : this.z;
        return new Vec3d(double2, double3, double4);
    }
    
    public double getComponentAlongAxis(final Direction.Axis axis) {
        return axis.choose(this.x, this.y, this.z);
    }
    
    @Override
    public final double getX() {
        return this.x;
    }
    
    @Override
    public final double getY() {
        return this.y;
    }
    
    @Override
    public final double getZ() {
        return this.z;
    }
    
    static {
        ZERO = new Vec3d(0.0, 0.0, 0.0);
    }
}
