package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.IllagerEntity;

@Environment(EnvType.CLIENT)
public class EvilVillagerEntityModel<T extends IllagerEntity> extends EntityModel<T> implements ModelWithArms, ModelWithHead
{
    protected final Cuboid a;
    private final Cuboid k;
    protected final Cuboid b;
    protected final Cuboid f;
    protected final Cuboid g;
    protected final Cuboid h;
    private final Cuboid l;
    protected final Cuboid i;
    protected final Cuboid j;
    private float m;
    
    public EvilVillagerEntityModel(final float float1, final float float2, final int integer3, final int integer4) {
        (this.a = new Cuboid(this).setTextureSize(integer3, integer4)).setRotationPoint(0.0f, 0.0f + float2, 0.0f);
        this.a.setTextureOffset(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, float1);
        (this.k = new Cuboid(this, 32, 0).setTextureSize(integer3, integer4)).addBox(-4.0f, -10.0f, -4.0f, 8, 12, 8, float1 + 0.45f);
        this.a.addChild(this.k);
        this.k.visible = false;
        (this.l = new Cuboid(this).setTextureSize(integer3, integer4)).setRotationPoint(0.0f, float2 - 2.0f, 0.0f);
        this.l.setTextureOffset(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2, 4, 2, float1);
        this.a.addChild(this.l);
        (this.b = new Cuboid(this).setTextureSize(integer3, integer4)).setRotationPoint(0.0f, 0.0f + float2, 0.0f);
        this.b.setTextureOffset(16, 20).addBox(-4.0f, 0.0f, -3.0f, 8, 12, 6, float1);
        this.b.setTextureOffset(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8, 18, 6, float1 + 0.5f);
        (this.f = new Cuboid(this).setTextureSize(integer3, integer4)).setRotationPoint(0.0f, 0.0f + float2 + 2.0f, 0.0f);
        this.f.setTextureOffset(44, 22).addBox(-8.0f, -2.0f, -2.0f, 4, 8, 4, float1);
        final Cuboid cuboid5 = new Cuboid(this, 44, 22).setTextureSize(integer3, integer4);
        cuboid5.mirror = true;
        cuboid5.addBox(4.0f, -2.0f, -2.0f, 4, 8, 4, float1);
        this.f.addChild(cuboid5);
        this.f.setTextureOffset(40, 38).addBox(-4.0f, 2.0f, -2.0f, 8, 4, 4, float1);
        (this.g = new Cuboid(this, 0, 22).setTextureSize(integer3, integer4)).setRotationPoint(-2.0f, 12.0f + float2, 0.0f);
        this.g.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
        this.h = new Cuboid(this, 0, 22).setTextureSize(integer3, integer4);
        this.h.mirror = true;
        this.h.setRotationPoint(2.0f, 12.0f + float2, 0.0f);
        this.h.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
        (this.i = new Cuboid(this, 40, 46).setTextureSize(integer3, integer4)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, float1);
        this.i.setRotationPoint(-5.0f, 2.0f + float2, 0.0f);
        this.j = new Cuboid(this, 40, 46).setTextureSize(integer3, integer4);
        this.j.mirror = true;
        this.j.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, float1);
        this.j.setRotationPoint(5.0f, 2.0f + float2, 0.0f);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
        this.b.render(scale);
        this.g.render(scale);
        this.h.render(scale);
        if (entity.getState() == IllagerEntity.State.a) {
            this.f.render(scale);
        }
        else {
            this.i.render(scale);
            this.j.render(scale);
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.a.yaw = headYaw * 0.017453292f;
        this.a.pitch = headPitch * 0.017453292f;
        this.f.rotationPointY = 3.0f;
        this.f.rotationPointZ = -1.0f;
        this.f.pitch = -0.75f;
        if (this.isRiding) {
            this.i.pitch = -0.62831855f;
            this.i.yaw = 0.0f;
            this.i.roll = 0.0f;
            this.j.pitch = -0.62831855f;
            this.j.yaw = 0.0f;
            this.j.roll = 0.0f;
            this.g.pitch = -1.4137167f;
            this.g.yaw = 0.31415927f;
            this.g.roll = 0.07853982f;
            this.h.pitch = -1.4137167f;
            this.h.yaw = -0.31415927f;
            this.h.roll = -0.07853982f;
        }
        else {
            this.i.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 2.0f * limbDistance * 0.5f;
            this.i.yaw = 0.0f;
            this.i.roll = 0.0f;
            this.j.pitch = MathHelper.cos(limbAngle * 0.6662f) * 2.0f * limbDistance * 0.5f;
            this.j.yaw = 0.0f;
            this.j.roll = 0.0f;
            this.g.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance * 0.5f;
            this.g.yaw = 0.0f;
            this.g.roll = 0.0f;
            this.h.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance * 0.5f;
            this.h.yaw = 0.0f;
            this.h.roll = 0.0f;
        }
        final IllagerEntity.State state8 = entity.getState();
        if (state8 == IllagerEntity.State.b) {
            final float float9 = MathHelper.sin(this.handSwingProgress * 3.1415927f);
            final float float10 = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * 3.1415927f);
            this.i.roll = 0.0f;
            this.j.roll = 0.0f;
            this.i.yaw = 0.15707964f;
            this.j.yaw = -0.15707964f;
            if (entity.getMainHand() == AbsoluteHand.b) {
                this.i.pitch = -1.8849558f + MathHelper.cos(age * 0.09f) * 0.15f;
                this.j.pitch = -0.0f + MathHelper.cos(age * 0.19f) * 0.5f;
                final Cuboid i = this.i;
                i.pitch += float9 * 2.2f - float10 * 0.4f;
                final Cuboid j = this.j;
                j.pitch += float9 * 1.2f - float10 * 0.4f;
            }
            else {
                this.i.pitch = -0.0f + MathHelper.cos(age * 0.19f) * 0.5f;
                this.j.pitch = -1.8849558f + MathHelper.cos(age * 0.09f) * 0.15f;
                final Cuboid k = this.i;
                k.pitch += float9 * 1.2f - float10 * 0.4f;
                final Cuboid l = this.j;
                l.pitch += float9 * 2.2f - float10 * 0.4f;
            }
            final Cuboid m = this.i;
            m.roll += MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
            final Cuboid j2 = this.j;
            j2.roll -= MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
            final Cuboid i2 = this.i;
            i2.pitch += MathHelper.sin(age * 0.067f) * 0.05f;
            final Cuboid j3 = this.j;
            j3.pitch -= MathHelper.sin(age * 0.067f) * 0.05f;
        }
        else if (state8 == IllagerEntity.State.c) {
            this.i.rotationPointZ = 0.0f;
            this.i.rotationPointX = -5.0f;
            this.j.rotationPointZ = 0.0f;
            this.j.rotationPointX = 5.0f;
            this.i.pitch = MathHelper.cos(age * 0.6662f) * 0.25f;
            this.j.pitch = MathHelper.cos(age * 0.6662f) * 0.25f;
            this.i.roll = 2.3561945f;
            this.j.roll = -2.3561945f;
            this.i.yaw = 0.0f;
            this.j.yaw = 0.0f;
        }
        else if (state8 == IllagerEntity.State.d) {
            this.i.yaw = -0.1f + this.a.yaw;
            this.i.pitch = -1.5707964f + this.a.pitch;
            this.j.pitch = -0.9424779f + this.a.pitch;
            this.j.yaw = this.a.yaw - 0.4f;
            this.j.roll = 1.5707964f;
        }
        else if (state8 == IllagerEntity.State.e) {
            this.i.yaw = -0.3f + this.a.yaw;
            this.j.yaw = 0.6f + this.a.yaw;
            this.i.pitch = -1.5707964f + this.a.pitch + 0.1f;
            this.j.pitch = -1.5f + this.a.pitch;
        }
        else if (state8 == IllagerEntity.State.f) {
            this.i.yaw = -0.8f;
            this.i.pitch = -0.97079635f;
            this.j.pitch = -0.97079635f;
            final float float9 = MathHelper.clamp(this.m, 0.0f, 25.0f);
            this.j.yaw = MathHelper.lerp(float9 / 25.0f, 0.4f, 0.85f);
            this.j.pitch = MathHelper.lerp(float9 / 25.0f, this.j.pitch, -1.5707964f);
        }
        else if (state8 == IllagerEntity.State.g) {
            this.i.rotationPointZ = 0.0f;
            this.i.rotationPointX = -5.0f;
            this.i.pitch = MathHelper.cos(age * 0.6662f) * 0.05f;
            this.i.roll = 2.670354f;
            this.i.yaw = 0.0f;
            this.j.rotationPointZ = 0.0f;
            this.j.rotationPointX = 5.0f;
            this.j.pitch = MathHelper.cos(age * 0.6662f) * 0.05f;
            this.j.roll = -2.3561945f;
            this.j.yaw = 0.0f;
        }
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        this.m = (float)entity.getItemUseTime();
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
    }
    
    private Cuboid a(final AbsoluteHand absoluteHand) {
        if (absoluteHand == AbsoluteHand.a) {
            return this.j;
        }
        return this.i;
    }
    
    public Cuboid b() {
        return this.k;
    }
    
    @Override
    public Cuboid getHead() {
        return this.a;
    }
    
    @Override
    public void setArmAngle(final float float1, final AbsoluteHand absoluteHand) {
        this.a(absoluteHand).applyTransform(0.0625f);
    }
}
