package net.minecraft.entity.ai.control;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.mob.MobEntity;

public class DolphinLookControl extends LookControl
{
    private final int h;
    
    public DolphinLookControl(final MobEntity entity, final int integer) {
        super(entity);
        this.h = integer;
    }
    
    @Override
    public void tick() {
        if (this.active) {
            this.active = false;
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.getTargetYaw() + 20.0f, this.yawSpeed);
            this.entity.pitch = this.changeAngle(this.entity.pitch, this.getTargetPitch() + 10.0f, this.pitchSpeed);
        }
        else {
            if (this.entity.getNavigation().isIdle()) {
                this.entity.pitch = this.changeAngle(this.entity.pitch, 0.0f, 5.0f);
            }
            this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.aK, this.yawSpeed);
        }
        final float float1 = MathHelper.wrapDegrees(this.entity.headYaw - this.entity.aK);
        if (float1 < -this.h) {
            final MobEntity entity = this.entity;
            entity.aK -= 4.0f;
        }
        else if (float1 > this.h) {
            final MobEntity entity2 = this.entity;
            entity2.aK += 4.0f;
        }
    }
}
