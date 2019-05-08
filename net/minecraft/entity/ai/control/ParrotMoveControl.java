package net.minecraft.entity.ai.control;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.mob.MobEntity;

public class ParrotMoveControl extends MoveControl
{
    public ParrotMoveControl(final MobEntity entity) {
        super(entity);
    }
    
    @Override
    public void tick() {
        if (this.state == State.b) {
            this.state = State.a;
            this.entity.setUnaffectedByGravity(true);
            final double double1 = this.targetX - this.entity.x;
            final double double2 = this.targetY - this.entity.y;
            final double double3 = this.targetZ - this.entity.z;
            final double double4 = double1 * double1 + double2 * double2 + double3 * double3;
            if (double4 < 2.500000277905201E-7) {
                this.entity.setUpwardSpeed(0.0f);
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            final float float9 = (float)(MathHelper.atan2(double3, double1) * 57.2957763671875) - 90.0f;
            this.entity.yaw = this.changeAngle(this.entity.yaw, float9, 10.0f);
            float float10;
            if (this.entity.onGround) {
                float10 = (float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            }
            else {
                float10 = (float)(this.speed * this.entity.getAttributeInstance(EntityAttributes.FLYING_SPEED).getValue());
            }
            this.entity.setMovementSpeed(float10);
            final double double5 = MathHelper.sqrt(double1 * double1 + double3 * double3);
            final float float11 = (float)(-(MathHelper.atan2(double2, double5) * 57.2957763671875));
            this.entity.pitch = this.changeAngle(this.entity.pitch, float11, 10.0f);
            this.entity.setUpwardSpeed((double2 > 0.0) ? float10 : (-float10));
        }
        else {
            this.entity.setUnaffectedByGravity(false);
            this.entity.setUpwardSpeed(0.0f);
            this.entity.setForwardSpeed(0.0f);
        }
    }
}
