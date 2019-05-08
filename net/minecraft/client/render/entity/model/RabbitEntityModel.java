package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.RabbitEntity;

@Environment(EnvType.CLIENT)
public class RabbitEntityModel<T extends RabbitEntity> extends EntityModel<T>
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
    private final Cuboid o;
    private float p;
    
    public RabbitEntityModel() {
        (this.a = new Cuboid(this, 26, 24)).addBox(-1.0f, 5.5f, -3.7f, 2, 1, 7);
        this.a.setRotationPoint(3.0f, 17.5f, 3.7f);
        this.a.mirror = true;
        this.a(this.a, 0.0f, 0.0f, 0.0f);
        (this.b = new Cuboid(this, 8, 24)).addBox(-1.0f, 5.5f, -3.7f, 2, 1, 7);
        this.b.setRotationPoint(-3.0f, 17.5f, 3.7f);
        this.b.mirror = true;
        this.a(this.b, 0.0f, 0.0f, 0.0f);
        (this.f = new Cuboid(this, 30, 15)).addBox(-1.0f, 0.0f, 0.0f, 2, 4, 5);
        this.f.setRotationPoint(3.0f, 17.5f, 3.7f);
        this.f.mirror = true;
        this.a(this.f, -0.34906584f, 0.0f, 0.0f);
        (this.g = new Cuboid(this, 16, 15)).addBox(-1.0f, 0.0f, 0.0f, 2, 4, 5);
        this.g.setRotationPoint(-3.0f, 17.5f, 3.7f);
        this.g.mirror = true;
        this.a(this.g, -0.34906584f, 0.0f, 0.0f);
        (this.h = new Cuboid(this, 0, 0)).addBox(-3.0f, -2.0f, -10.0f, 6, 5, 10);
        this.h.setRotationPoint(0.0f, 19.0f, 8.0f);
        this.h.mirror = true;
        this.a(this.h, -0.34906584f, 0.0f, 0.0f);
        (this.i = new Cuboid(this, 8, 15)).addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2);
        this.i.setRotationPoint(3.0f, 17.0f, -1.0f);
        this.i.mirror = true;
        this.a(this.i, -0.17453292f, 0.0f, 0.0f);
        (this.j = new Cuboid(this, 0, 15)).addBox(-1.0f, 0.0f, -1.0f, 2, 7, 2);
        this.j.setRotationPoint(-3.0f, 17.0f, -1.0f);
        this.j.mirror = true;
        this.a(this.j, -0.17453292f, 0.0f, 0.0f);
        (this.k = new Cuboid(this, 32, 0)).addBox(-2.5f, -4.0f, -5.0f, 5, 4, 5);
        this.k.setRotationPoint(0.0f, 16.0f, -1.0f);
        this.k.mirror = true;
        this.a(this.k, 0.0f, 0.0f, 0.0f);
        (this.l = new Cuboid(this, 52, 0)).addBox(-2.5f, -9.0f, -1.0f, 2, 5, 1);
        this.l.setRotationPoint(0.0f, 16.0f, -1.0f);
        this.l.mirror = true;
        this.a(this.l, 0.0f, -0.2617994f, 0.0f);
        (this.m = new Cuboid(this, 58, 0)).addBox(0.5f, -9.0f, -1.0f, 2, 5, 1);
        this.m.setRotationPoint(0.0f, 16.0f, -1.0f);
        this.m.mirror = true;
        this.a(this.m, 0.0f, 0.2617994f, 0.0f);
        (this.n = new Cuboid(this, 52, 6)).addBox(-1.5f, -1.5f, 0.0f, 3, 3, 2);
        this.n.setRotationPoint(0.0f, 20.0f, 7.0f);
        this.n.mirror = true;
        this.a(this.n, -0.3490659f, 0.0f, 0.0f);
        (this.o = new Cuboid(this, 32, 9)).addBox(-0.5f, -2.5f, -5.5f, 1, 1, 1);
        this.o.setRotationPoint(0.0f, 16.0f, -1.0f);
        this.o.mirror = true;
        this.a(this.o, 0.0f, 0.0f, 0.0f);
    }
    
    private void a(final Cuboid cuboid, final float float2, final float float3, final float float4) {
        cuboid.pitch = float2;
        cuboid.yaw = float3;
        cuboid.roll = float4;
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            final float float8 = 1.5f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.56666666f, 0.56666666f, 0.56666666f);
            GlStateManager.translatef(0.0f, 22.0f * scale, 2.0f * scale);
            this.k.render(scale);
            this.m.render(scale);
            this.l.render(scale);
            this.o.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.4f, 0.4f, 0.4f);
            GlStateManager.translatef(0.0f, 36.0f * scale, 0.0f);
            this.a.render(scale);
            this.b.render(scale);
            this.f.render(scale);
            this.g.render(scale);
            this.h.render(scale);
            this.i.render(scale);
            this.j.render(scale);
            this.n.render(scale);
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.6f, 0.6f, 0.6f);
            GlStateManager.translatef(0.0f, 16.0f * scale, 0.0f);
            this.a.render(scale);
            this.b.render(scale);
            this.f.render(scale);
            this.g.render(scale);
            this.h.render(scale);
            this.i.render(scale);
            this.j.render(scale);
            this.k.render(scale);
            this.l.render(scale);
            this.m.render(scale);
            this.n.render(scale);
            this.o.render(scale);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        final float float8 = age - entity.age;
        this.o.pitch = headPitch * 0.017453292f;
        this.k.pitch = headPitch * 0.017453292f;
        this.l.pitch = headPitch * 0.017453292f;
        this.m.pitch = headPitch * 0.017453292f;
        this.o.yaw = headYaw * 0.017453292f;
        this.k.yaw = headYaw * 0.017453292f;
        this.l.yaw = this.o.yaw - 0.2617994f;
        this.m.yaw = this.o.yaw + 0.2617994f;
        this.p = MathHelper.sin(entity.v(float8) * 3.1415927f);
        this.f.pitch = (this.p * 50.0f - 21.0f) * 0.017453292f;
        this.g.pitch = (this.p * 50.0f - 21.0f) * 0.017453292f;
        this.a.pitch = this.p * 50.0f * 0.017453292f;
        this.b.pitch = this.p * 50.0f * 0.017453292f;
        this.i.pitch = (this.p * -40.0f - 11.0f) * 0.017453292f;
        this.j.pitch = (this.p * -40.0f - 11.0f) * 0.017453292f;
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
        this.p = MathHelper.sin(entity.v(tickDelta) * 3.1415927f);
    }
}
