package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.ParrotEntity;

@Environment(EnvType.CLIENT)
public class ParrotEntityModel extends EntityModel<ParrotEntity>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    private final Cuboid j;
    private final Cuboid k;
    private final Cuboid l;
    private final Cuboid m;
    private final Cuboid n;
    
    public ParrotEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        (this.a = new Cuboid(this, 2, 8)).addBox(-1.5f, 0.0f, -1.5f, 3, 6, 3);
        this.a.setRotationPoint(0.0f, 16.5f, -3.0f);
        (this.b = new Cuboid(this, 22, 1)).addBox(-1.5f, -1.0f, -1.0f, 3, 4, 1);
        this.b.setRotationPoint(0.0f, 21.07f, 1.16f);
        (this.f = new Cuboid(this, 19, 8)).addBox(-0.5f, 0.0f, -1.5f, 1, 5, 3);
        this.f.setRotationPoint(1.5f, 16.94f, -2.76f);
        (this.g = new Cuboid(this, 19, 8)).addBox(-0.5f, 0.0f, -1.5f, 1, 5, 3);
        this.g.setRotationPoint(-1.5f, 16.94f, -2.76f);
        (this.h = new Cuboid(this, 2, 2)).addBox(-1.0f, -1.5f, -1.0f, 2, 3, 2);
        this.h.setRotationPoint(0.0f, 15.69f, -2.76f);
        (this.i = new Cuboid(this, 10, 0)).addBox(-1.0f, -0.5f, -2.0f, 2, 1, 4);
        this.i.setRotationPoint(0.0f, -2.0f, -1.0f);
        this.h.addChild(this.i);
        (this.j = new Cuboid(this, 11, 7)).addBox(-0.5f, -1.0f, -0.5f, 1, 2, 1);
        this.j.setRotationPoint(0.0f, -0.5f, -1.5f);
        this.h.addChild(this.j);
        (this.k = new Cuboid(this, 16, 7)).addBox(-0.5f, 0.0f, -0.5f, 1, 2, 1);
        this.k.setRotationPoint(0.0f, -1.75f, -2.45f);
        this.h.addChild(this.k);
        (this.l = new Cuboid(this, 2, 18)).addBox(0.0f, -4.0f, -2.0f, 0, 5, 4);
        this.l.setRotationPoint(0.0f, -2.15f, 0.15f);
        this.h.addChild(this.l);
        (this.m = new Cuboid(this, 14, 18)).addBox(-0.5f, 0.0f, -0.5f, 1, 2, 1);
        this.m.setRotationPoint(1.0f, 22.0f, -1.05f);
        (this.n = new Cuboid(this, 14, 18)).addBox(-0.5f, 0.0f, -0.5f, 1, 2, 1);
        this.n.setRotationPoint(-1.0f, 22.0f, -1.05f);
    }
    
    @Override
    public void render(final ParrotEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.a(scale);
    }
    
    @Override
    public void render(final ParrotEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.a(a(entity), entity.age, limbAngle, limbDistance, age, headYaw, headPitch);
    }
    
    @Override
    public void animateModel(final ParrotEntity entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        this.a(a(entity));
    }
    
    public void a(final float float1, final float float2, final float float3, final float float4, final float float5, final int integer) {
        this.a(ParrotEntityModel.a.e);
        this.a(ParrotEntityModel.a.e, integer, float1, float2, 0.0f, float3, float4);
        this.a(float5);
    }
    
    private void a(final float float1) {
        this.a.render(float1);
        this.f.render(float1);
        this.g.render(float1);
        this.b.render(float1);
        this.h.render(float1);
        this.m.render(float1);
        this.n.render(float1);
    }
    
    private void a(final a a, final int integer, final float float3, final float float4, final float float5, final float float6, final float float7) {
        this.h.pitch = float7 * 0.017453292f;
        this.h.yaw = float6 * 0.017453292f;
        this.h.roll = 0.0f;
        this.h.rotationPointX = 0.0f;
        this.a.rotationPointX = 0.0f;
        this.b.rotationPointX = 0.0f;
        this.g.rotationPointX = -1.5f;
        this.f.rotationPointX = 1.5f;
        switch (a) {
            case c: {
                return;
            }
            case d: {
                final float float8 = MathHelper.cos((float)integer);
                final float float9 = MathHelper.sin((float)integer);
                this.h.rotationPointX = float8;
                this.h.rotationPointY = 15.69f + float9;
                this.h.pitch = 0.0f;
                this.h.yaw = 0.0f;
                this.h.roll = MathHelper.sin((float)integer) * 0.4f;
                this.a.rotationPointX = float8;
                this.a.rotationPointY = 16.5f + float9;
                this.f.roll = -0.0873f - float5;
                this.f.rotationPointX = 1.5f + float8;
                this.f.rotationPointY = 16.94f + float9;
                this.g.roll = 0.0873f + float5;
                this.g.rotationPointX = -1.5f + float8;
                this.g.rotationPointY = 16.94f + float9;
                this.b.rotationPointX = float8;
                this.b.rotationPointY = 21.07f + float9;
                return;
            }
            case b: {
                final Cuboid m = this.m;
                m.pitch += MathHelper.cos(float3 * 0.6662f) * 1.4f * float4;
                final Cuboid n = this.n;
                n.pitch += MathHelper.cos(float3 * 0.6662f + 3.1415927f) * 1.4f * float4;
                break;
            }
        }
        final float float10 = float5 * 0.3f;
        this.h.rotationPointY = 15.69f + float10;
        this.b.pitch = 1.015f + MathHelper.cos(float3 * 0.6662f) * 0.3f * float4;
        this.b.rotationPointY = 21.07f + float10;
        this.a.rotationPointY = 16.5f + float10;
        this.f.roll = -0.0873f - float5;
        this.f.rotationPointY = 16.94f + float10;
        this.g.roll = 0.0873f + float5;
        this.g.rotationPointY = 16.94f + float10;
        this.m.rotationPointY = 22.0f + float10;
        this.n.rotationPointY = 22.0f + float10;
    }
    
    private void a(final a a) {
        this.l.pitch = -0.2214f;
        this.a.pitch = 0.4937f;
        this.f.pitch = -0.6981f;
        this.f.yaw = -3.1415927f;
        this.g.pitch = -0.6981f;
        this.g.yaw = -3.1415927f;
        this.m.pitch = -0.0299f;
        this.n.pitch = -0.0299f;
        this.m.rotationPointY = 22.0f;
        this.n.rotationPointY = 22.0f;
        this.m.roll = 0.0f;
        this.n.roll = 0.0f;
        switch (a) {
            case a: {
                final Cuboid m = this.m;
                m.pitch += 0.6981317f;
                final Cuboid n = this.n;
                n.pitch += 0.6981317f;
                break;
            }
            case c: {
                final float float2 = 1.9f;
                this.h.rotationPointY = 17.59f;
                this.b.pitch = 1.5388988f;
                this.b.rotationPointY = 22.97f;
                this.a.rotationPointY = 18.4f;
                this.f.roll = -0.0873f;
                this.f.rotationPointY = 18.84f;
                this.g.roll = 0.0873f;
                this.g.rotationPointY = 18.84f;
                final Cuboid i = this.m;
                i.rotationPointY += 1.9f;
                final Cuboid n2 = this.n;
                n2.rotationPointY += 1.9f;
                final Cuboid j = this.m;
                j.pitch += 1.5707964f;
                final Cuboid n3 = this.n;
                n3.pitch += 1.5707964f;
                break;
            }
            case d: {
                this.m.roll = -0.34906584f;
                this.n.roll = 0.34906584f;
                break;
            }
        }
    }
    
    private static a a(final ParrotEntity parrotEntity) {
        if (parrotEntity.getSongPlaying()) {
            return a.d;
        }
        if (parrotEntity.isSitting()) {
            return a.c;
        }
        if (parrotEntity.isInAir()) {
            return a.a;
        }
        return a.b;
    }
    
    @Environment(EnvType.CLIENT)
    public enum a
    {
        a, 
        b, 
        c, 
        d, 
        e;
    }
}
