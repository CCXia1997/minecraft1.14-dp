package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.HorseBaseEntity;

@Environment(EnvType.CLIENT)
public class HorseEntityModel<T extends HorseBaseEntity> extends EntityModel<T>
{
    protected final Cuboid a;
    protected final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    private final Cuboid j;
    private final Cuboid[] k;
    private final Cuboid[] l;
    
    public HorseEntityModel(final float float1) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.a = new Cuboid(this, 0, 32)).addBox(-5.0f, -8.0f, -17.0f, 10, 10, 22, 0.05f);
        this.a.setRotationPoint(0.0f, 11.0f, 5.0f);
        (this.b = new Cuboid(this, 0, 35)).addBox(-2.05f, -6.0f, -2.0f, 4, 12, 7);
        this.b.pitch = 0.5235988f;
        final Cuboid cuboid2 = new Cuboid(this, 0, 13);
        cuboid2.addBox(-3.0f, -11.0f, -2.0f, 6, 5, 7, float1);
        final Cuboid cuboid3 = new Cuboid(this, 56, 36);
        cuboid3.addBox(-1.0f, -11.0f, 5.01f, 2, 16, 2, float1);
        final Cuboid cuboid4 = new Cuboid(this, 0, 25);
        cuboid4.addBox(-2.0f, -11.0f, -7.0f, 4, 5, 5, float1);
        this.b.addChild(cuboid2);
        this.b.addChild(cuboid3);
        this.b.addChild(cuboid4);
        this.a(this.b);
        this.f = new Cuboid(this, 48, 21);
        this.f.mirror = true;
        this.f.addBox(-3.0f, -1.01f, -1.0f, 4, 11, 4, float1);
        this.f.setRotationPoint(4.0f, 14.0f, 7.0f);
        (this.g = new Cuboid(this, 48, 21)).addBox(-1.0f, -1.01f, -1.0f, 4, 11, 4, float1);
        this.g.setRotationPoint(-4.0f, 14.0f, 7.0f);
        this.h = new Cuboid(this, 48, 21);
        this.h.mirror = true;
        this.h.addBox(-3.0f, -1.01f, -1.9f, 4, 11, 4, float1);
        this.h.setRotationPoint(4.0f, 6.0f, -12.0f);
        (this.i = new Cuboid(this, 48, 21)).addBox(-1.0f, -1.01f, -1.9f, 4, 11, 4, float1);
        this.i.setRotationPoint(-4.0f, 6.0f, -12.0f);
        (this.j = new Cuboid(this, 42, 36)).addBox(-1.5f, 0.0f, 0.0f, 3, 14, 4, float1);
        this.j.setRotationPoint(0.0f, -5.0f, 2.0f);
        this.j.pitch = 0.5235988f;
        this.a.addChild(this.j);
        final Cuboid cuboid5 = new Cuboid(this, 26, 0);
        cuboid5.addBox(-5.0f, -8.0f, -9.0f, 10, 9, 9, 0.5f);
        this.a.addChild(cuboid5);
        final Cuboid cuboid6 = new Cuboid(this, 29, 5);
        cuboid6.addBox(2.0f, -9.0f, -6.0f, 1, 2, 2, float1);
        this.b.addChild(cuboid6);
        final Cuboid cuboid7 = new Cuboid(this, 29, 5);
        cuboid7.addBox(-3.0f, -9.0f, -6.0f, 1, 2, 2, float1);
        this.b.addChild(cuboid7);
        final Cuboid cuboid8 = new Cuboid(this, 32, 2);
        cuboid8.addBox(3.1f, -6.0f, -8.0f, 0, 3, 16, float1);
        cuboid8.pitch = -0.5235988f;
        this.b.addChild(cuboid8);
        final Cuboid cuboid9 = new Cuboid(this, 32, 2);
        cuboid9.addBox(-3.1f, -6.0f, -8.0f, 0, 3, 16, float1);
        cuboid9.pitch = -0.5235988f;
        this.b.addChild(cuboid9);
        final Cuboid cuboid10 = new Cuboid(this, 1, 1);
        cuboid10.addBox(-3.0f, -11.0f, -1.9f, 6, 5, 6, 0.2f);
        this.b.addChild(cuboid10);
        final Cuboid cuboid11 = new Cuboid(this, 19, 0);
        cuboid11.addBox(-2.0f, -11.0f, -4.0f, 4, 5, 2, 0.2f);
        this.b.addChild(cuboid11);
        this.k = new Cuboid[] { cuboid5, cuboid6, cuboid7, cuboid10, cuboid11 };
        this.l = new Cuboid[] { cuboid8, cuboid9 };
    }
    
    protected void a(final Cuboid cuboid) {
        final Cuboid cuboid2 = new Cuboid(this, 19, 16);
        cuboid2.addBox(0.55f, -13.0f, 4.0f, 2, 3, 1, -0.001f);
        final Cuboid cuboid3 = new Cuboid(this, 19, 16);
        cuboid3.addBox(-2.55f, -13.0f, 4.0f, 2, 3, 1, -0.001f);
        cuboid.addChild(cuboid2);
        cuboid.addChild(cuboid3);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        final boolean boolean8 = entity.isChild();
        final float float9 = entity.getScaleFactor();
        final boolean boolean9 = entity.isSaddled();
        final boolean boolean10 = entity.hasPassengers();
        for (final Cuboid cuboid15 : this.k) {
            cuboid15.visible = boolean9;
        }
        for (final Cuboid cuboid15 : this.l) {
            cuboid15.visible = (boolean10 && boolean9);
        }
        if (boolean8) {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(float9, 0.5f + float9 * 0.5f, float9);
            GlStateManager.translatef(0.0f, 0.95f * (1.0f - float9), 0.0f);
        }
        this.f.render(scale);
        this.g.render(scale);
        this.h.render(scale);
        this.i.render(scale);
        if (boolean8) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(float9, float9, float9);
            GlStateManager.translatef(0.0f, 2.3f * (1.0f - float9), 0.0f);
        }
        this.a.render(scale);
        if (boolean8) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            final float float10 = float9 + 0.1f * float9;
            GlStateManager.scalef(float10, float10, float10);
            GlStateManager.translatef(0.0f, 2.25f * (1.0f - float10), 0.1f * (1.4f - float10));
        }
        this.b.render(scale);
        if (boolean8) {
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
        final float float5 = this.a(entity.aL, entity.aK, tickDelta);
        final float float6 = this.a(entity.prevHeadYaw, entity.headYaw, tickDelta);
        final float float7 = MathHelper.lerp(tickDelta, entity.prevPitch, entity.pitch);
        float float8 = float6 - float5;
        float float9 = float7 * 0.017453292f;
        if (float8 > 20.0f) {
            float8 = 20.0f;
        }
        if (float8 < -20.0f) {
            float8 = -20.0f;
        }
        if (limbDistance > 0.2f) {
            float9 += MathHelper.cos(limbAngle * 0.4f) * 0.15f * limbDistance;
        }
        final float float10 = entity.getEatingGrassAnimationProgress(tickDelta);
        final float float11 = entity.getAngryAnimationProgress(tickDelta);
        final float float12 = 1.0f - float11;
        final float float13 = entity.getEatingAnimationProgress(tickDelta);
        final boolean boolean14 = entity.bA != 0;
        final float float14 = entity.age + tickDelta;
        this.b.rotationPointY = 4.0f;
        this.b.rotationPointZ = -12.0f;
        this.a.pitch = 0.0f;
        this.b.pitch = 0.5235988f + float9;
        this.b.yaw = float8 * 0.017453292f;
        final float float15 = entity.isInsideWater() ? 0.2f : 1.0f;
        final float float16 = MathHelper.cos(float15 * limbAngle * 0.6662f + 3.1415927f);
        final float float17 = float16 * 0.8f * limbDistance;
        final float float18 = (1.0f - Math.max(float11, float10)) * (0.5235988f + float9 + float13 * MathHelper.sin(float14) * 0.05f);
        this.b.pitch = float11 * (0.2617994f + float9) + float10 * (2.1816616f + MathHelper.sin(float14) * 0.05f) + float18;
        this.b.yaw = float11 * float8 * 0.017453292f + (1.0f - Math.max(float11, float10)) * this.b.yaw;
        this.b.rotationPointY = float11 * -4.0f + float10 * 11.0f + (1.0f - Math.max(float11, float10)) * this.b.rotationPointY;
        this.b.rotationPointZ = float11 * -4.0f + float10 * -12.0f + (1.0f - Math.max(float11, float10)) * this.b.rotationPointZ;
        this.a.pitch = float11 * -0.7853982f + float12 * this.a.pitch;
        final float float19 = 0.2617994f * float11;
        final float float20 = MathHelper.cos(float14 * 0.6f + 3.1415927f);
        this.h.rotationPointY = 2.0f * float11 + 14.0f * float12;
        this.h.rotationPointZ = -6.0f * float11 - 10.0f * float12;
        this.i.rotationPointY = this.h.rotationPointY;
        this.i.rotationPointZ = this.h.rotationPointZ;
        final float float21 = (-1.0471976f + float20) * float11 + float17 * float12;
        final float float22 = (-1.0471976f - float20) * float11 - float17 * float12;
        this.f.pitch = float19 - float16 * 0.5f * limbDistance * float12;
        this.g.pitch = float19 + float16 * 0.5f * limbDistance * float12;
        this.h.pitch = float21;
        this.i.pitch = float22;
        this.j.pitch = 0.5235988f + limbDistance * 0.75f;
        this.j.rotationPointY = -5.0f + limbDistance;
        this.j.rotationPointZ = 2.0f + limbDistance * 2.0f;
        if (boolean14) {
            this.j.yaw = MathHelper.cos(float14 * 0.7f);
        }
        else {
            this.j.yaw = 0.0f;
        }
    }
    
    private float a(final float float1, final float float2, final float float3) {
        float float4;
        for (float4 = float2 - float1; float4 < -180.0f; float4 += 360.0f) {}
        while (float4 >= 180.0f) {
            float4 -= 360.0f;
        }
        return float1 + float3 * float4;
    }
}
