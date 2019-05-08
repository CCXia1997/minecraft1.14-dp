package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Vec3i implements Comparable<Vec3i>
{
    public static final Vec3i ZERO;
    private final int x;
    private final int y;
    private final int z;
    
    public Vec3i(final int integer1, final int integer2, final int integer3) {
        this.x = integer1;
        this.y = integer2;
        this.z = integer3;
    }
    
    public Vec3i(final double double1, final double double3, final double double5) {
        this(MathHelper.floor(double1), MathHelper.floor(double3), MathHelper.floor(double5));
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Vec3i)) {
            return false;
        }
        final Vec3i vec3i2 = (Vec3i)object;
        return this.getX() == vec3i2.getX() && this.getY() == vec3i2.getY() && this.getZ() == vec3i2.getZ();
    }
    
    @Override
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }
    
    public int l(final Vec3i vec3i) {
        if (this.getY() != vec3i.getY()) {
            return this.getY() - vec3i.getY();
        }
        if (this.getZ() == vec3i.getZ()) {
            return this.getX() - vec3i.getX();
        }
        return this.getZ() - vec3i.getZ();
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public Vec3i crossProduct(final Vec3i vec) {
        return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }
    
    public boolean isWithinDistance(final Vec3i vec, final double distance) {
        return this.getSquaredDistance(vec.x, vec.y, vec.z, false) < distance * distance;
    }
    
    public boolean isWithinDistance(final Position pos, final double distance) {
        return this.getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), true) < distance * distance;
    }
    
    public double getSquaredDistance(final Vec3i vec) {
        return this.getSquaredDistance(vec.getX(), vec.getY(), vec.getZ(), true);
    }
    
    public double getSquaredDistance(final Position pos, final boolean treatAsBlockPos) {
        return this.getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), treatAsBlockPos);
    }
    
    public double getSquaredDistance(final double x, final double y, final double z, final boolean treatAsBlockPos) {
        final double double8 = treatAsBlockPos ? 0.5 : 0.0;
        final double double9 = this.getX() + double8 - x;
        final double double10 = this.getY() + double8 - y;
        final double double11 = this.getZ() + double8 - z;
        return double9 * double9 + double10 * double10 + double11 * double11;
    }
    
    public int getManhattanDistance(final Vec3i vec) {
        final float float2 = (float)Math.abs(vec.getX() - this.x);
        final float float3 = (float)Math.abs(vec.getY() - this.y);
        final float float4 = (float)Math.abs(vec.getZ() - this.z);
        return (int)(float2 + float3 + float4);
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }
    
    static {
        ZERO = new Vec3i(0, 0, 0);
    }
}
