package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class PhantomEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    private final Cuboid j;
    private final Cuboid k;
    
    public PhantomEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.a = new Cuboid(this, 0, 8)).addBox(-3.0f, -2.0f, -8.0f, 5, 3, 9);
        (this.j = new Cuboid(this, 3, 20)).addBox(-2.0f, 0.0f, 0.0f, 3, 2, 6);
        this.j.setRotationPoint(0.0f, -2.0f, 1.0f);
        this.a.addChild(this.j);
        (this.k = new Cuboid(this, 4, 29)).addBox(-1.0f, 0.0f, 0.0f, 1, 1, 6);
        this.k.setRotationPoint(0.0f, 0.5f, 6.0f);
        this.j.addChild(this.k);
        (this.b = new Cuboid(this, 23, 12)).addBox(0.0f, 0.0f, 0.0f, 6, 2, 9);
        this.b.setRotationPoint(2.0f, -2.0f, -8.0f);
        (this.f = new Cuboid(this, 16, 24)).addBox(0.0f, 0.0f, 0.0f, 13, 1, 9);
        this.f.setRotationPoint(6.0f, 0.0f, 0.0f);
        this.b.addChild(this.f);
        this.g = new Cuboid(this, 23, 12);
        this.g.mirror = true;
        this.g.addBox(-6.0f, 0.0f, 0.0f, 6, 2, 9);
        this.g.setRotationPoint(-3.0f, -2.0f, -8.0f);
        this.h = new Cuboid(this, 16, 24);
        this.h.mirror = true;
        this.h.addBox(-13.0f, 0.0f, 0.0f, 13, 1, 9);
        this.h.setRotationPoint(-6.0f, 0.0f, 0.0f);
        this.g.addChild(this.h);
        this.b.roll = 0.1f;
        this.f.roll = 0.1f;
        this.g.roll = -0.1f;
        this.h.roll = -0.1f;
        this.a.pitch = -0.1f;
        (this.i = new Cuboid(this, 0, 0)).addBox(-4.0f, -2.0f, -5.0f, 7, 3, 5);
        this.i.setRotationPoint(0.0f, 1.0f, -7.0f);
        this.i.pitch = 0.2f;
        this.a.addChild(this.i);
        this.a.addChild(this.b);
        this.a.addChild(this.g);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.a.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        final float float8 = (entity.getEntityId() * 3 + age) * 0.13f;
        final float float9 = 16.0f;
        this.b.roll = MathHelper.cos(float8) * 16.0f * 0.017453292f;
        this.f.roll = MathHelper.cos(float8) * 16.0f * 0.017453292f;
        this.g.roll = -this.b.roll;
        this.h.roll = -this.f.roll;
        this.j.pitch = -(5.0f + MathHelper.cos(float8 * 2.0f) * 5.0f) * 0.017453292f;
        this.k.pitch = -(5.0f + MathHelper.cos(float8 * 2.0f) * 5.0f) * 0.017453292f;
    }
}
