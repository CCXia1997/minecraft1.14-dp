package net.minecraft.util.math;

public class PositionImpl implements Position
{
    protected final double x;
    protected final double y;
    protected final double z;
    
    public PositionImpl(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public double getX() {
        return this.x;
    }
    
    @Override
    public double getY() {
        return this.y;
    }
    
    @Override
    public double getZ() {
        return this.z;
    }
}
