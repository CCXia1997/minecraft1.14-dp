package net.minecraft.entity.ai.goal;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class PounceAtTargetGoal extends Goal
{
    private final MobEntity owner;
    private LivingEntity target;
    private final float c;
    
    public PounceAtTargetGoal(final MobEntity owner, final float float2) {
        this.owner = owner;
        this.c = float2;
        this.setControls(EnumSet.<Control>of(Control.c, Control.a));
    }
    
    @Override
    public boolean canStart() {
        if (this.owner.hasPassengers()) {
            return false;
        }
        this.target = this.owner.getTarget();
        if (this.target == null) {
            return false;
        }
        final double double1 = this.owner.squaredDistanceTo(this.target);
        return double1 >= 4.0 && double1 <= 16.0 && this.owner.onGround && this.owner.getRand().nextInt(5) == 0;
    }
    
    @Override
    public boolean shouldContinue() {
        return !this.owner.onGround;
    }
    
    @Override
    public void start() {
        final Vec3d vec3d1 = this.owner.getVelocity();
        Vec3d vec3d2 = new Vec3d(this.target.x - this.owner.x, 0.0, this.target.z - this.owner.z);
        if (vec3d2.lengthSquared() > 1.0E-7) {
            vec3d2 = vec3d2.normalize().multiply(0.4).add(vec3d1.multiply(0.2));
        }
        this.owner.setVelocity(vec3d2.x, this.c, vec3d2.y);
    }
}
