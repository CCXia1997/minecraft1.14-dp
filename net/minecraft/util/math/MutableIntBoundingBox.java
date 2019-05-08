package net.minecraft.util.math;

import net.minecraft.nbt.IntArrayTag;
import com.google.common.base.MoreObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class MutableIntBoundingBox
{
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;
    
    public MutableIntBoundingBox() {
    }
    
    public MutableIntBoundingBox(final int[] data) {
        if (data.length == 6) {
            this.minX = data[0];
            this.minY = data[1];
            this.minZ = data[2];
            this.maxX = data[3];
            this.maxY = data[4];
            this.maxZ = data[5];
        }
    }
    
    public static MutableIntBoundingBox empty() {
        return new MutableIntBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }
    
    public static MutableIntBoundingBox createRotated(final int x, final int y, final int z, final int integer4, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9, final Direction facing) {
        switch (facing) {
            default: {
                return new MutableIntBoundingBox(x + integer4, y + integer5, z + integer6, x + integer7 - 1 + integer4, y + integer8 - 1 + integer5, z + integer9 - 1 + integer6);
            }
            case NORTH: {
                return new MutableIntBoundingBox(x + integer4, y + integer5, z - integer9 + 1 + integer6, x + integer7 - 1 + integer4, y + integer8 - 1 + integer5, z + integer6);
            }
            case SOUTH: {
                return new MutableIntBoundingBox(x + integer4, y + integer5, z + integer6, x + integer7 - 1 + integer4, y + integer8 - 1 + integer5, z + integer9 - 1 + integer6);
            }
            case WEST: {
                return new MutableIntBoundingBox(x - integer9 + 1 + integer6, y + integer5, z + integer4, x + integer6, y + integer8 - 1 + integer5, z + integer7 - 1 + integer4);
            }
            case EAST: {
                return new MutableIntBoundingBox(x + integer6, y + integer5, z + integer4, x + integer9 - 1 + integer6, y + integer8 - 1 + integer5, z + integer7 - 1 + integer4);
            }
        }
    }
    
    public static MutableIntBoundingBox create(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        return new MutableIntBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
    }
    
    public MutableIntBoundingBox(final MutableIntBoundingBox source) {
        this.minX = source.minX;
        this.minY = source.minY;
        this.minZ = source.minZ;
        this.maxX = source.maxX;
        this.maxY = source.maxY;
        this.maxZ = source.maxZ;
    }
    
    public MutableIntBoundingBox(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }
    
    public MutableIntBoundingBox(final Vec3i v1, final Vec3i v2) {
        this.minX = Math.min(v1.getX(), v2.getX());
        this.minY = Math.min(v1.getY(), v2.getY());
        this.minZ = Math.min(v1.getZ(), v2.getZ());
        this.maxX = Math.max(v1.getX(), v2.getX());
        this.maxY = Math.max(v1.getY(), v2.getY());
        this.maxZ = Math.max(v1.getZ(), v2.getZ());
    }
    
    public MutableIntBoundingBox(final int minX, final int minZ, final int maxX, final int maxZ) {
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.minY = 1;
        this.maxY = 512;
    }
    
    public boolean intersects(final MutableIntBoundingBox other) {
        return this.maxX >= other.minX && this.minX <= other.maxX && this.maxZ >= other.minZ && this.minZ <= other.maxZ && this.maxY >= other.minY && this.minY <= other.maxY;
    }
    
    public boolean intersectsXZ(final int minX, final int minZ, final int maxX, final int maxZ) {
        return this.maxX >= minX && this.minX <= maxX && this.maxZ >= minZ && this.minZ <= maxZ;
    }
    
    public void setFrom(final MutableIntBoundingBox source) {
        this.minX = Math.min(this.minX, source.minX);
        this.minY = Math.min(this.minY, source.minY);
        this.minZ = Math.min(this.minZ, source.minZ);
        this.maxX = Math.max(this.maxX, source.maxX);
        this.maxY = Math.max(this.maxY, source.maxY);
        this.maxZ = Math.max(this.maxZ, source.maxZ);
    }
    
    public void translate(final int dx, final int dy, final int dz) {
        this.minX += dx;
        this.minY += dy;
        this.minZ += dz;
        this.maxX += dx;
        this.maxY += dy;
        this.maxZ += dz;
    }
    
    public MutableIntBoundingBox b(final int integer1, final int integer2, final int integer3) {
        return new MutableIntBoundingBox(this.minX + integer1, this.minY + integer2, this.minZ + integer3, this.maxX + integer1, this.maxY + integer2, this.maxZ + integer3);
    }
    
    public boolean contains(final Vec3i vec) {
        return vec.getX() >= this.minX && vec.getX() <= this.maxX && vec.getZ() >= this.minZ && vec.getZ() <= this.maxZ && vec.getY() >= this.minY && vec.getY() <= this.maxY;
    }
    
    public Vec3i getSize() {
        return new Vec3i(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
    }
    
    public int getBlockCountX() {
        return this.maxX - this.minX + 1;
    }
    
    public int getBlockCountY() {
        return this.maxY - this.minY + 1;
    }
    
    public int getBlockCountZ() {
        return this.maxZ - this.minZ + 1;
    }
    
    @Environment(EnvType.CLIENT)
    public Vec3i f() {
        return new BlockPos(this.minX + (this.maxX - this.minX + 1) / 2, this.minY + (this.maxY - this.minY + 1) / 2, this.minZ + (this.maxZ - this.minZ + 1) / 2);
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("x0", this.minX).add("y0", this.minY).add("z0", this.minZ).add("x1", this.maxX).add("y1", this.maxY).add("z1", this.maxZ).toString();
    }
    
    public IntArrayTag toNbt() {
        return new IntArrayTag(new int[] { this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ });
    }
}
