package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity> extends EntityModel<T>
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
    
    public SpiderEntityModel() {
        final float float1 = 0.0f;
        final int integer2 = 15;
        (this.a = new Cuboid(this, 32, 4)).addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, 0.0f);
        this.a.setRotationPoint(0.0f, 15.0f, -3.0f);
        (this.b = new Cuboid(this, 0, 0)).addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6, 0.0f);
        this.b.setRotationPoint(0.0f, 15.0f, 0.0f);
        (this.f = new Cuboid(this, 0, 12)).addBox(-5.0f, -4.0f, -6.0f, 10, 8, 12, 0.0f);
        this.f.setRotationPoint(0.0f, 15.0f, 9.0f);
        (this.g = new Cuboid(this, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.g.setRotationPoint(-4.0f, 15.0f, 2.0f);
        (this.h = new Cuboid(this, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.h.setRotationPoint(4.0f, 15.0f, 2.0f);
        (this.i = new Cuboid(this, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.i.setRotationPoint(-4.0f, 15.0f, 1.0f);
        (this.j = new Cuboid(this, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.j.setRotationPoint(4.0f, 15.0f, 1.0f);
        (this.k = new Cuboid(this, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.k.setRotationPoint(-4.0f, 15.0f, 0.0f);
        (this.l = new Cuboid(this, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.l.setRotationPoint(4.0f, 15.0f, 0.0f);
        (this.m = new Cuboid(this, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.m.setRotationPoint(-4.0f, 15.0f, -1.0f);
        (this.n = new Cuboid(this, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.n.setRotationPoint(4.0f, 15.0f, -1.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
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
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.a.yaw = headYaw * 0.017453292f;
        this.a.pitch = headPitch * 0.017453292f;
        final float float8 = 0.7853982f;
        this.g.roll = -0.7853982f;
        this.h.roll = 0.7853982f;
        this.i.roll = -0.58119464f;
        this.j.roll = 0.58119464f;
        this.k.roll = -0.58119464f;
        this.l.roll = 0.58119464f;
        this.m.roll = -0.7853982f;
        this.n.roll = 0.7853982f;
        final float float9 = -0.0f;
        final float float10 = 0.3926991f;
        this.g.yaw = 0.7853982f;
        this.h.yaw = -0.7853982f;
        this.i.yaw = 0.3926991f;
        this.j.yaw = -0.3926991f;
        this.k.yaw = -0.3926991f;
        this.l.yaw = 0.3926991f;
        this.m.yaw = -0.7853982f;
        this.n.yaw = 0.7853982f;
        final float float11 = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 0.0f) * 0.4f) * limbDistance;
        final float float12 = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 3.1415927f) * 0.4f) * limbDistance;
        final float float13 = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * limbDistance;
        final float float14 = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 4.712389f) * 0.4f) * limbDistance;
        final float float15 = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 0.0f) * 0.4f) * limbDistance;
        final float float16 = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 3.1415927f) * 0.4f) * limbDistance;
        final float float17 = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 1.5707964f) * 0.4f) * limbDistance;
        final float float18 = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 4.712389f) * 0.4f) * limbDistance;
        final Cuboid g = this.g;
        g.yaw += float11;
        final Cuboid h = this.h;
        h.yaw += -float11;
        final Cuboid i = this.i;
        i.yaw += float12;
        final Cuboid j = this.j;
        j.yaw += -float12;
        final Cuboid k = this.k;
        k.yaw += float13;
        final Cuboid l = this.l;
        l.yaw += -float13;
        final Cuboid m = this.m;
        m.yaw += float14;
        final Cuboid n = this.n;
        n.yaw += -float14;
        final Cuboid g2 = this.g;
        g2.roll += float15;
        final Cuboid h2 = this.h;
        h2.roll += -float15;
        final Cuboid i2 = this.i;
        i2.roll += float16;
        final Cuboid j2 = this.j;
        j2.roll += -float16;
        final Cuboid k2 = this.k;
        k2.roll += float17;
        final Cuboid l2 = this.l;
        l2.roll += -float17;
        final Cuboid m2 = this.m;
        m2.roll += float18;
        final Cuboid n2 = this.n;
        n2.roll += -float18;
    }
}
