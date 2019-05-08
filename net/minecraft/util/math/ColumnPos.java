package net.minecraft.util.math;

public class ColumnPos
{
    public final int x;
    public final int z;
    
    public ColumnPos(final int x, final int z) {
        this.x = x;
        this.z = z;
    }
    
    public ColumnPos(final BlockPos pos) {
        this.x = pos.getX();
        this.z = pos.getZ();
    }
    
    public long toLong() {
        return toLong(this.x, this.z);
    }
    
    public static long toLong(final int x, final int z) {
        return ((long)x & 0xFFFFFFFFL) | ((long)z & 0xFFFFFFFFL) << 32;
    }
    
    @Override
    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }
    
    @Override
    public int hashCode() {
        final int integer1 = 1664525 * this.x + 1013904223;
        final int integer2 = 1664525 * (this.z ^ 0xDEADBEEF) + 1013904223;
        return integer1 ^ integer2;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ColumnPos) {
            final ColumnPos columnPos2 = (ColumnPos)object;
            return this.x == columnPos2.x && this.z == columnPos2.z;
        }
        return false;
    }
}
