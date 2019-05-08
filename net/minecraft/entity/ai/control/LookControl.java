package net.minecraft.entity.ai.control;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.mob.MobEntity;

public class LookControl
{
    protected final MobEntity entity;
    protected float yawSpeed;
    protected float pitchSpeed;
    protected boolean active;
    protected double lookX;
    protected double lookY;
    protected double lookZ;
    
    public LookControl(final MobEntity mobEntity) {
        this.entity = mobEntity;
    }
    
    public void a(final Vec3d vec3d) {
        this.a(vec3d.x, vec3d.y, vec3d.z);
    }
    
    public void lookAt(final Entity entity, final float yawSpeed, final float pitchSpeed) {
        this.lookAt(entity.x, getLookingHeightFor(entity), entity.z, yawSpeed, pitchSpeed);
    }
    
    public void a(final double double1, final double double3, final double double5) {
        this.lookAt(double1, double3, double5, (float)this.entity.getLookYawSpeed(), (float)this.entity.getLookPitchSpeed());
    }
    
    public void lookAt(final double x, final double y, final double z, final float yawSpeed, final float pitchSpeed) {
        this.lookX = x;
        this.lookY = y;
        this.lookZ = z;
        this.yawSpeed = yawSpeed;
        this.pitchSpeed = pitchSpeed;
        this.active = true;
    }
    
    public void tick() {
        if (this.b()) {
            this.entity.pitch = 0.0f;
        }
        if (this.active) {
            this.active = false;
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.getTargetYaw(), this.yawSpeed);
            this.entity.pitch = this.changeAngle(this.entity.pitch, this.getTargetPitch(), this.pitchSpeed);
        }
        else {
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.aK, 10.0f);
        }
        if (!this.entity.getNavigation().isIdle()) {
            this.entity.headYaw = MathHelper.b(this.entity.headYaw, this.entity.aK, (float)this.entity.dA());
        }
    }
    
    protected boolean b() {
        return true;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public double getLookX() {
        return this.lookX;
    }
    
    public double getLookY() {
        return this.lookY;
    }
    
    public double getLookZ() {
        return this.lookZ;
    }
    
    protected float getTargetPitch() {
        final double double1 = this.lookX - this.entity.x;
        final double double2 = this.lookY - (this.entity.y + this.entity.getStandingEyeHeight());
        final double double3 = this.lookZ - this.entity.z;
        final double double4 = MathHelper.sqrt(double1 * double1 + double3 * double3);
        return (float)(-(MathHelper.atan2(double2, double4) * 57.2957763671875));
    }
    
    protected float getTargetYaw() {
        final double double1 = this.lookX - this.entity.x;
        final double double2 = this.lookZ - this.entity.z;
        return (float)(MathHelper.atan2(double2, double1) * 57.2957763671875) - 90.0f;
    }
    
    protected float changeAngle(final float from, final float to, final float max) {
        final float float4 = MathHelper.subtractAngles(from, to);
        final float float5 = MathHelper.clamp(float4, -max, max);
        return from + float5;
    }
    
    private static double getLookingHeightFor(final Entity entity) {
        if (entity instanceof LivingEntity) {
            return entity.y + entity.getStandingEyeHeight();
        }
        return (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
    }
}
