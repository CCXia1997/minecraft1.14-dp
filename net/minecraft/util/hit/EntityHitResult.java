package net.minecraft.util.hit;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;

public class EntityHitResult extends HitResult
{
    private final Entity entity;
    
    public EntityHitResult(final Entity entity) {
        this(entity, new Vec3d(entity.x, entity.y, entity.z));
    }
    
    public EntityHitResult(final Entity entity, final Vec3d pos) {
        super(pos);
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    @Override
    public Type getType() {
        return Type.ENTITY;
    }
}
