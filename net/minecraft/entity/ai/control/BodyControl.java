package net.minecraft.entity.ai.control;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.mob.MobEntity;

public class BodyControl
{
    private final MobEntity entity;
    private int b;
    private float c;
    
    public BodyControl(final MobEntity mobEntity) {
        this.entity = mobEntity;
    }
    
    public void a() {
        if (this.f()) {
            this.entity.aK = this.entity.yaw;
            this.c();
            this.c = this.entity.headYaw;
            this.b = 0;
            return;
        }
        if (this.e()) {
            if (Math.abs(this.entity.headYaw - this.c) > 15.0f) {
                this.b = 0;
                this.c = this.entity.headYaw;
                this.b();
            }
            else {
                ++this.b;
                if (this.b > 10) {
                    this.d();
                }
            }
        }
    }
    
    private void b() {
        this.entity.aK = MathHelper.b(this.entity.aK, this.entity.headYaw, (float)this.entity.dA());
    }
    
    private void c() {
        this.entity.headYaw = MathHelper.b(this.entity.headYaw, this.entity.aK, (float)this.entity.dA());
    }
    
    private void d() {
        final int integer1 = this.b - 10;
        final float float2 = MathHelper.clamp(integer1 / 10.0f, 0.0f, 1.0f);
        final float float3 = this.entity.dA() * (1.0f - float2);
        this.entity.aK = MathHelper.b(this.entity.aK, this.entity.headYaw, float3);
    }
    
    private boolean e() {
        return this.entity.getPassengerList().isEmpty() || !(this.entity.getPassengerList().get(0) instanceof MobEntity);
    }
    
    private boolean f() {
        final double double1 = this.entity.x - this.entity.prevX;
        final double double2 = this.entity.z - this.entity.prevZ;
        return double1 * double1 + double2 * double2 > 2.500000277905201E-7;
    }
}
