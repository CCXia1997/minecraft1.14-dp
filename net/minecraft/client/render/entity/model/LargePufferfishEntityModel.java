package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LargePufferfishEntityModel<T extends Entity> extends EntityModel<T>
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
    private final Cuboid p;
    
    public LargePufferfishEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        final int integer1 = 22;
        (this.a = new Cuboid(this, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8);
        this.a.setRotationPoint(0.0f, 22.0f, 0.0f);
        (this.b = new Cuboid(this, 24, 0)).addBox(-2.0f, 0.0f, -1.0f, 2, 1, 2);
        this.b.setRotationPoint(-4.0f, 15.0f, -2.0f);
        (this.f = new Cuboid(this, 24, 3)).addBox(0.0f, 0.0f, -1.0f, 2, 1, 2);
        this.f.setRotationPoint(4.0f, 15.0f, -2.0f);
        (this.g = new Cuboid(this, 15, 17)).addBox(-4.0f, -1.0f, 0.0f, 8, 1, 0);
        this.g.setRotationPoint(0.0f, 14.0f, -4.0f);
        this.g.pitch = 0.7853982f;
        (this.h = new Cuboid(this, 14, 16)).addBox(-4.0f, -1.0f, 0.0f, 8, 1, 1);
        this.h.setRotationPoint(0.0f, 14.0f, 0.0f);
        (this.i = new Cuboid(this, 23, 18)).addBox(-4.0f, -1.0f, 0.0f, 8, 1, 0);
        this.i.setRotationPoint(0.0f, 14.0f, 4.0f);
        this.i.pitch = -0.7853982f;
        (this.j = new Cuboid(this, 5, 17)).addBox(-1.0f, -8.0f, 0.0f, 1, 8, 0);
        this.j.setRotationPoint(-4.0f, 22.0f, -4.0f);
        this.j.yaw = -0.7853982f;
        (this.k = new Cuboid(this, 1, 17)).addBox(0.0f, -8.0f, 0.0f, 1, 8, 0);
        this.k.setRotationPoint(4.0f, 22.0f, -4.0f);
        this.k.yaw = 0.7853982f;
        (this.l = new Cuboid(this, 15, 20)).addBox(-4.0f, 0.0f, 0.0f, 8, 1, 0);
        this.l.setRotationPoint(0.0f, 22.0f, -4.0f);
        this.l.pitch = -0.7853982f;
        (this.n = new Cuboid(this, 15, 20)).addBox(-4.0f, 0.0f, 0.0f, 8, 1, 0);
        this.n.setRotationPoint(0.0f, 22.0f, 0.0f);
        (this.m = new Cuboid(this, 15, 20)).addBox(-4.0f, 0.0f, 0.0f, 8, 1, 0);
        this.m.setRotationPoint(0.0f, 22.0f, 4.0f);
        this.m.pitch = 0.7853982f;
        (this.o = new Cuboid(this, 9, 17)).addBox(-1.0f, -8.0f, 0.0f, 1, 8, 0);
        this.o.setRotationPoint(-4.0f, 22.0f, 4.0f);
        this.o.yaw = 0.7853982f;
        (this.p = new Cuboid(this, 9, 17)).addBox(0.0f, -8.0f, 0.0f, 1, 8, 0);
        this.p.setRotationPoint(4.0f, 22.0f, 4.0f);
        this.p.yaw = -0.7853982f;
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
        this.n.render(scale);
        this.m.render(scale);
        this.o.render(scale);
        this.p.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.b.roll = -0.2f + 0.4f * MathHelper.sin(age * 0.2f);
        this.f.roll = 0.2f - 0.4f * MathHelper.sin(age * 0.2f);
    }
}
