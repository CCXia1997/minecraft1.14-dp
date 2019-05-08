package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.BatEntity;

@Environment(EnvType.CLIENT)
public class BatEntityModel extends EntityModel<BatEntity>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    
    public BatEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.a = new Cuboid(this, 0, 0)).addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6);
        final Cuboid cuboid1 = new Cuboid(this, 24, 0);
        cuboid1.addBox(-4.0f, -6.0f, -2.0f, 3, 4, 1);
        this.a.addChild(cuboid1);
        final Cuboid cuboid2 = new Cuboid(this, 24, 0);
        cuboid2.mirror = true;
        cuboid2.addBox(1.0f, -6.0f, -2.0f, 3, 4, 1);
        this.a.addChild(cuboid2);
        (this.b = new Cuboid(this, 0, 16)).addBox(-3.0f, 4.0f, -3.0f, 6, 12, 6);
        this.b.setTextureOffset(0, 34).addBox(-5.0f, 16.0f, 0.0f, 10, 6, 1);
        (this.f = new Cuboid(this, 42, 0)).addBox(-12.0f, 1.0f, 1.5f, 10, 16, 1);
        (this.h = new Cuboid(this, 24, 16)).setRotationPoint(-12.0f, 1.0f, 1.5f);
        this.h.addBox(-8.0f, 1.0f, 0.0f, 8, 12, 1);
        this.g = new Cuboid(this, 42, 0);
        this.g.mirror = true;
        this.g.addBox(2.0f, 1.0f, 1.5f, 10, 16, 1);
        this.i = new Cuboid(this, 24, 16);
        this.i.mirror = true;
        this.i.setRotationPoint(12.0f, 1.0f, 1.5f);
        this.i.addBox(0.0f, 1.0f, 0.0f, 8, 12, 1);
        this.b.addChild(this.f);
        this.b.addChild(this.g);
        this.f.addChild(this.h);
        this.g.addChild(this.i);
    }
    
    @Override
    public void setAngles(final BatEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
        this.b.render(scale);
    }
    
    @Override
    public void setAngles(final BatEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        if (entity.isRoosting()) {
            this.a.pitch = headPitch * 0.017453292f;
            this.a.yaw = 3.1415927f - headYaw * 0.017453292f;
            this.a.roll = 3.1415927f;
            this.a.setRotationPoint(0.0f, -2.0f, 0.0f);
            this.f.setRotationPoint(-3.0f, 0.0f, 3.0f);
            this.g.setRotationPoint(3.0f, 0.0f, 3.0f);
            this.b.pitch = 3.1415927f;
            this.f.pitch = -0.15707964f;
            this.f.yaw = -1.2566371f;
            this.h.yaw = -1.7278761f;
            this.g.pitch = this.f.pitch;
            this.g.yaw = -this.f.yaw;
            this.i.yaw = -this.h.yaw;
        }
        else {
            this.a.pitch = headPitch * 0.017453292f;
            this.a.yaw = headYaw * 0.017453292f;
            this.a.roll = 0.0f;
            this.a.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.f.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.g.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.b.pitch = 0.7853982f + MathHelper.cos(age * 0.1f) * 0.15f;
            this.b.yaw = 0.0f;
            this.f.yaw = MathHelper.cos(age * 1.3f) * 3.1415927f * 0.25f;
            this.g.yaw = -this.f.yaw;
            this.h.yaw = this.f.yaw * 0.5f;
            this.i.yaw = -this.f.yaw * 0.5f;
        }
    }
}
