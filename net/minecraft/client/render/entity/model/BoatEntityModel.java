package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.vehicle.BoatEntity;

@Environment(EnvType.CLIENT)
public class BoatEntityModel extends EntityModel<BoatEntity>
{
    private final Cuboid[] a;
    private final Cuboid[] b;
    private final Cuboid f;
    
    public BoatEntityModel() {
        this.a = new Cuboid[5];
        this.b = new Cuboid[2];
        this.a[0] = new Cuboid(this, 0, 0).setTextureSize(128, 64);
        this.a[1] = new Cuboid(this, 0, 19).setTextureSize(128, 64);
        this.a[2] = new Cuboid(this, 0, 27).setTextureSize(128, 64);
        this.a[3] = new Cuboid(this, 0, 35).setTextureSize(128, 64);
        this.a[4] = new Cuboid(this, 0, 43).setTextureSize(128, 64);
        final int integer1 = 32;
        final int integer2 = 6;
        final int integer3 = 20;
        final int integer4 = 4;
        final int integer5 = 28;
        this.a[0].addBox(-14.0f, -9.0f, -3.0f, 28, 16, 3, 0.0f);
        this.a[0].setRotationPoint(0.0f, 3.0f, 1.0f);
        this.a[1].addBox(-13.0f, -7.0f, -1.0f, 18, 6, 2, 0.0f);
        this.a[1].setRotationPoint(-15.0f, 4.0f, 4.0f);
        this.a[2].addBox(-8.0f, -7.0f, -1.0f, 16, 6, 2, 0.0f);
        this.a[2].setRotationPoint(15.0f, 4.0f, 0.0f);
        this.a[3].addBox(-14.0f, -7.0f, -1.0f, 28, 6, 2, 0.0f);
        this.a[3].setRotationPoint(0.0f, 4.0f, -9.0f);
        this.a[4].addBox(-14.0f, -7.0f, -1.0f, 28, 6, 2, 0.0f);
        this.a[4].setRotationPoint(0.0f, 4.0f, 9.0f);
        this.a[0].pitch = 1.5707964f;
        this.a[1].yaw = 4.712389f;
        this.a[2].yaw = 1.5707964f;
        this.a[3].yaw = 3.1415927f;
        (this.b[0] = this.a(true)).setRotationPoint(3.0f, -5.0f, 9.0f);
        (this.b[1] = this.a(false)).setRotationPoint(3.0f, -5.0f, -9.0f);
        this.b[1].yaw = 3.1415927f;
        this.b[0].roll = 0.19634955f;
        this.b[1].roll = 0.19634955f;
        (this.f = new Cuboid(this, 0, 0).setTextureSize(128, 64)).addBox(-14.0f, -9.0f, -3.0f, 28, 16, 3, 0.0f);
        this.f.setRotationPoint(0.0f, -3.0f, 1.0f);
        this.f.pitch = 1.5707964f;
    }
    
    @Override
    public void render(final BoatEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        for (int integer8 = 0; integer8 < 5; ++integer8) {
            this.a[integer8].render(scale);
        }
        this.a(entity, 0, scale, limbAngle);
        this.a(entity, 1, scale, limbAngle);
    }
    
    public void renderPass(final Entity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.colorMask(false, false, false, false);
        this.f.render(float7);
        GlStateManager.colorMask(true, true, true, true);
    }
    
    protected Cuboid a(final boolean boolean1) {
        final Cuboid cuboid2 = new Cuboid(this, 62, boolean1 ? 0 : 20).setTextureSize(128, 64);
        final int integer3 = 20;
        final int integer4 = 7;
        final int integer5 = 6;
        final float float6 = -5.0f;
        cuboid2.addBox(-1.0f, 0.0f, -5.0f, 2, 2, 18);
        cuboid2.addBox(boolean1 ? -1.001f : 0.001f, -3.0f, 8.0f, 1, 6, 7);
        return cuboid2;
    }
    
    protected void a(final BoatEntity boatEntity, final int integer, final float float3, final float float4) {
        final float float5 = boatEntity.a(integer, float4);
        final Cuboid cuboid6 = this.b[integer];
        cuboid6.pitch = (float)MathHelper.clampedLerp(-1.0471975803375244, -0.2617993950843811, (MathHelper.sin(-float5) + 1.0f) / 2.0f);
        cuboid6.yaw = (float)MathHelper.clampedLerp(-0.7853981852531433, 0.7853981852531433, (MathHelper.sin(-float5 + 1.0f) + 1.0f) / 2.0f);
        if (integer == 1) {
            cuboid6.yaw = 3.1415927f - cuboid6.yaw;
        }
        cuboid6.render(float3);
    }
}
