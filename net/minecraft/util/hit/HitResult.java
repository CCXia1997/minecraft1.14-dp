package net.minecraft.util.hit;

import net.minecraft.util.math.Vec3d;

public abstract class HitResult
{
    protected final Vec3d pos;
    
    protected HitResult(final Vec3d pos) {
        this.pos = pos;
    }
    
    public abstract Type getType();
    
    public Vec3d getPos() {
        return this.pos;
    }
    
    public enum Type
    {
        NONE, 
        BLOCK, 
        ENTITY;
    }
}
