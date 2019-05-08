package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(EnvType.CLIENT)
public class IronGolemEntityModel<T extends IronGolemEntity> extends EntityModel<T>
{
    private final Cuboid b;
    private final Cuboid f;
    public final Cuboid a;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    
    public IronGolemEntityModel() {
        this(0.0f);
    }
    
    public IronGolemEntityModel(final float float1) {
        this(float1, -7.0f);
    }
    
    public IronGolemEntityModel(final float float1, final float float2) {
        final int integer3 = 128;
        final int integer4 = 128;
        (this.b = new Cuboid(this).setTextureSize(128, 128)).setRotationPoint(0.0f, 0.0f + float2, -2.0f);
        this.b.setTextureOffset(0, 0).addBox(-4.0f, -12.0f, -5.5f, 8, 10, 8, float1);
        this.b.setTextureOffset(24, 0).addBox(-1.0f, -5.0f, -7.5f, 2, 4, 2, float1);
        (this.f = new Cuboid(this).setTextureSize(128, 128)).setRotationPoint(0.0f, 0.0f + float2, 0.0f);
        this.f.setTextureOffset(0, 40).addBox(-9.0f, -2.0f, -6.0f, 18, 12, 11, float1);
        this.f.setTextureOffset(0, 70).addBox(-4.5f, 10.0f, -3.0f, 9, 5, 6, float1 + 0.5f);
        (this.a = new Cuboid(this).setTextureSize(128, 128)).setRotationPoint(0.0f, -7.0f, 0.0f);
        this.a.setTextureOffset(60, 21).addBox(-13.0f, -2.5f, -3.0f, 4, 30, 6, float1);
        (this.g = new Cuboid(this).setTextureSize(128, 128)).setRotationPoint(0.0f, -7.0f, 0.0f);
        this.g.setTextureOffset(60, 58).addBox(9.0f, -2.5f, -3.0f, 4, 30, 6, float1);
        (this.h = new Cuboid(this, 0, 22).setTextureSize(128, 128)).setRotationPoint(-4.0f, 18.0f + float2, 0.0f);
        this.h.setTextureOffset(37, 0).addBox(-3.5f, -3.0f, -3.0f, 6, 16, 5, float1);
        this.i = new Cuboid(this, 0, 22).setTextureSize(128, 128);
        this.i.mirror = true;
        this.i.setTextureOffset(60, 0).setRotationPoint(5.0f, 18.0f + float2, 0.0f);
        this.i.addBox(-3.5f, -3.0f, -3.0f, 6, 16, 5, float1);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.b.render(scale);
        this.f.render(scale);
        this.h.render(scale);
        this.i.render(scale);
        this.a.render(scale);
        this.g.render(scale);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.b.yaw = headYaw * 0.017453292f;
        this.b.pitch = headPitch * 0.017453292f;
        this.h.pitch = -1.5f * this.a(limbAngle, 13.0f) * limbDistance;
        this.i.pitch = 1.5f * this.a(limbAngle, 13.0f) * limbDistance;
        this.h.yaw = 0.0f;
        this.i.yaw = 0.0f;
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        final int integer5 = entity.l();
        if (integer5 > 0) {
            this.a.pitch = -2.0f + 1.5f * this.a(integer5 - tickDelta, 10.0f);
            this.g.pitch = -2.0f + 1.5f * this.a(integer5 - tickDelta, 10.0f);
        }
        else {
            final int integer6 = entity.dV();
            if (integer6 > 0) {
                this.a.pitch = -0.8f + 0.025f * this.a((float)integer6, 70.0f);
                this.g.pitch = 0.0f;
            }
            else {
                this.a.pitch = (-0.2f + 1.5f * this.a(limbAngle, 13.0f)) * limbDistance;
                this.g.pitch = (-0.2f - 1.5f * this.a(limbAngle, 13.0f)) * limbDistance;
            }
        }
    }
    
    private float a(final float float1, final float float2) {
        return (Math.abs(float1 % float2 - float2 * 0.5f) - float2 * 0.25f) / (float2 * 0.25f);
    }
    
    public Cuboid a() {
        return this.a;
    }
}
