package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.RavagerEntity;

@Environment(EnvType.CLIENT)
public class IllagerBeastEntityModel extends EntityModel<RavagerEntity>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    private final Cuboid j;
    private final Cuboid k;
    
    public IllagerBeastEntityModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        final int integer1 = 16;
        final float float2 = 0.0f;
        (this.k = new Cuboid(this)).setRotationPoint(0.0f, -7.0f, -1.5f);
        this.k.setTextureOffset(68, 73).addBox(-5.0f, -1.0f, -18.0f, 10, 10, 18, 0.0f);
        (this.a = new Cuboid(this)).setRotationPoint(0.0f, 16.0f, -17.0f);
        this.a.setTextureOffset(0, 0).addBox(-8.0f, -20.0f, -14.0f, 16, 20, 16, 0.0f);
        this.a.setTextureOffset(0, 0).addBox(-2.0f, -6.0f, -18.0f, 4, 8, 4, 0.0f);
        final Cuboid cuboid3 = new Cuboid(this);
        cuboid3.setRotationPoint(-10.0f, -14.0f, -8.0f);
        cuboid3.setTextureOffset(74, 55).addBox(0.0f, -14.0f, -2.0f, 2, 14, 4, 0.0f);
        cuboid3.pitch = 1.0995574f;
        this.a.addChild(cuboid3);
        final Cuboid cuboid4 = new Cuboid(this);
        cuboid4.mirror = true;
        cuboid4.setRotationPoint(8.0f, -14.0f, -8.0f);
        cuboid4.setTextureOffset(74, 55).addBox(0.0f, -14.0f, -2.0f, 2, 14, 4, 0.0f);
        cuboid4.pitch = 1.0995574f;
        this.a.addChild(cuboid4);
        (this.b = new Cuboid(this)).setRotationPoint(0.0f, -2.0f, 2.0f);
        this.b.setTextureOffset(0, 36).addBox(-8.0f, 0.0f, -16.0f, 16, 3, 16, 0.0f);
        this.a.addChild(this.b);
        this.k.addChild(this.a);
        this.f = new Cuboid(this);
        this.f.setTextureOffset(0, 55).addBox(-7.0f, -10.0f, -7.0f, 14, 16, 20, 0.0f);
        this.f.setTextureOffset(0, 91).addBox(-6.0f, 6.0f, -7.0f, 12, 13, 18, 0.0f);
        this.f.setRotationPoint(0.0f, 1.0f, 2.0f);
        (this.g = new Cuboid(this, 96, 0)).addBox(-4.0f, 0.0f, -4.0f, 8, 37, 8, 0.0f);
        this.g.setRotationPoint(-8.0f, -13.0f, 18.0f);
        this.h = new Cuboid(this, 96, 0);
        this.h.mirror = true;
        this.h.addBox(-4.0f, 0.0f, -4.0f, 8, 37, 8, 0.0f);
        this.h.setRotationPoint(8.0f, -13.0f, 18.0f);
        (this.i = new Cuboid(this, 64, 0)).addBox(-4.0f, 0.0f, -4.0f, 8, 37, 8, 0.0f);
        this.i.setRotationPoint(-8.0f, -13.0f, -5.0f);
        this.j = new Cuboid(this, 64, 0);
        this.j.mirror = true;
        this.j.addBox(-4.0f, 0.0f, -4.0f, 8, 37, 8, 0.0f);
        this.j.setRotationPoint(8.0f, -13.0f, -5.0f);
    }
    
    @Override
    public void setAngles(final RavagerEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.k.render(scale);
        this.f.render(scale);
        this.g.render(scale);
        this.h.render(scale);
        this.i.render(scale);
        this.j.render(scale);
    }
    
    @Override
    public void setAngles(final RavagerEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.a.pitch = headPitch * 0.017453292f;
        this.a.yaw = headYaw * 0.017453292f;
        this.f.pitch = 1.5707964f;
        final float float8 = 0.4f * limbDistance;
        this.g.pitch = MathHelper.cos(limbAngle * 0.6662f) * float8;
        this.h.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * float8;
        this.i.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * float8;
        this.j.pitch = MathHelper.cos(limbAngle * 0.6662f) * float8;
    }
    
    @Override
    public void animateModel(final RavagerEntity entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
        final int integer5 = entity.getStunTick();
        final int integer6 = entity.getRoarTick();
        final int integer7 = 20;
        final int integer8 = entity.getAttackTick();
        final int integer9 = 10;
        if (integer8 > 0) {
            final float float10 = this.a(integer8 - tickDelta, 10.0f);
            final float float11 = (1.0f + float10) * 0.5f;
            final float float12 = float11 * float11 * float11 * 12.0f;
            final float float13 = float12 * MathHelper.sin(this.k.pitch);
            this.k.rotationPointZ = -6.5f + float12;
            this.k.rotationPointY = -7.0f - float13;
            final float float14 = MathHelper.sin((integer8 - tickDelta) / 10.0f * 3.1415927f * 0.25f);
            this.b.pitch = 1.5707964f * float14;
            if (integer8 > 5) {
                this.b.pitch = MathHelper.sin((-4 + integer8 - tickDelta) / 4.0f) * 3.1415927f * 0.4f;
            }
            else {
                this.b.pitch = 0.15707964f * MathHelper.sin(3.1415927f * (integer8 - tickDelta) / 10.0f);
            }
        }
        else {
            final float float10 = -1.0f;
            final float float11 = -1.0f * MathHelper.sin(this.k.pitch);
            this.k.rotationPointX = 0.0f;
            this.k.rotationPointY = -7.0f - float11;
            this.k.rotationPointZ = 5.5f;
            final boolean boolean12 = integer5 > 0;
            this.k.pitch = (boolean12 ? 0.21991149f : 0.0f);
            this.b.pitch = 3.1415927f * (boolean12 ? 0.05f : 0.01f);
            if (boolean12) {
                final double double13 = integer5 / 40.0;
                this.k.rotationPointX = (float)Math.sin(double13 * 10.0) * 3.0f;
            }
            else if (integer6 > 0) {
                final float float13 = MathHelper.sin((20 - integer6 - tickDelta) / 20.0f * 3.1415927f * 0.25f);
                this.b.pitch = 1.5707964f * float13;
            }
        }
    }
    
    private float a(final float float1, final float float2) {
        return (Math.abs(float1 % float2 - float2 * 0.5f) - float2 * 0.25f) / (float2 * 0.25f);
    }
}
