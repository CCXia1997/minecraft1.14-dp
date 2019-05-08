package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class MediumPufferfishEntityModel<T extends Entity> extends EntityModel<T>
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
    
    public MediumPufferfishEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        final int integer1 = 22;
        (this.a = new Cuboid(this, 12, 22)).addBox(-2.5f, -5.0f, -2.5f, 5, 5, 5);
        this.a.setRotationPoint(0.0f, 22.0f, 0.0f);
        (this.b = new Cuboid(this, 24, 0)).addBox(-2.0f, 0.0f, 0.0f, 2, 0, 2);
        this.b.setRotationPoint(-2.5f, 17.0f, -1.5f);
        (this.f = new Cuboid(this, 24, 3)).addBox(0.0f, 0.0f, 0.0f, 2, 0, 2);
        this.f.setRotationPoint(2.5f, 17.0f, -1.5f);
        (this.g = new Cuboid(this, 15, 16)).addBox(-2.5f, -1.0f, 0.0f, 5, 1, 1);
        this.g.setRotationPoint(0.0f, 17.0f, -2.5f);
        this.g.pitch = 0.7853982f;
        (this.h = new Cuboid(this, 10, 16)).addBox(-2.5f, -1.0f, -1.0f, 5, 1, 1);
        this.h.setRotationPoint(0.0f, 17.0f, 2.5f);
        this.h.pitch = -0.7853982f;
        (this.i = new Cuboid(this, 8, 16)).addBox(-1.0f, -5.0f, 0.0f, 1, 5, 1);
        this.i.setRotationPoint(-2.5f, 22.0f, -2.5f);
        this.i.yaw = -0.7853982f;
        (this.j = new Cuboid(this, 8, 16)).addBox(-1.0f, -5.0f, 0.0f, 1, 5, 1);
        this.j.setRotationPoint(-2.5f, 22.0f, 2.5f);
        this.j.yaw = 0.7853982f;
        (this.k = new Cuboid(this, 4, 16)).addBox(0.0f, -5.0f, 0.0f, 1, 5, 1);
        this.k.setRotationPoint(2.5f, 22.0f, 2.5f);
        this.k.yaw = -0.7853982f;
        (this.l = new Cuboid(this, 0, 16)).addBox(0.0f, -5.0f, 0.0f, 1, 5, 1);
        this.l.setRotationPoint(2.5f, 22.0f, -2.5f);
        this.l.yaw = 0.7853982f;
        (this.m = new Cuboid(this, 8, 22)).addBox(0.0f, 0.0f, 0.0f, 1, 1, 1);
        this.m.setRotationPoint(0.5f, 22.0f, 2.5f);
        this.m.pitch = 0.7853982f;
        (this.n = new Cuboid(this, 17, 21)).addBox(-2.5f, 0.0f, 0.0f, 5, 1, 1);
        this.n.setRotationPoint(0.0f, 22.0f, -2.5f);
        this.n.pitch = -0.7853982f;
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
        this.b.roll = -0.2f + 0.4f * MathHelper.sin(age * 0.2f);
        this.f.roll = 0.2f - 0.4f * MathHelper.sin(age * 0.2f);
    }
}
