package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SalmonEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    private final Cuboid j;
    private final Cuboid k;
    
    public SalmonEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        final int integer1 = 20;
        (this.a = new Cuboid(this, 0, 0)).addBox(-1.5f, -2.5f, 0.0f, 3, 5, 8);
        this.a.setRotationPoint(0.0f, 20.0f, 0.0f);
        (this.b = new Cuboid(this, 0, 13)).addBox(-1.5f, -2.5f, 0.0f, 3, 5, 8);
        this.b.setRotationPoint(0.0f, 20.0f, 8.0f);
        (this.f = new Cuboid(this, 22, 0)).addBox(-1.0f, -2.0f, -3.0f, 2, 4, 3);
        this.f.setRotationPoint(0.0f, 20.0f, 0.0f);
        (this.i = new Cuboid(this, 20, 10)).addBox(0.0f, -2.5f, 0.0f, 0, 5, 6);
        this.i.setRotationPoint(0.0f, 0.0f, 8.0f);
        this.b.addChild(this.i);
        (this.g = new Cuboid(this, 2, 1)).addBox(0.0f, 0.0f, 0.0f, 0, 2, 3);
        this.g.setRotationPoint(0.0f, -4.5f, 5.0f);
        this.a.addChild(this.g);
        (this.h = new Cuboid(this, 0, 2)).addBox(0.0f, 0.0f, 0.0f, 0, 2, 4);
        this.h.setRotationPoint(0.0f, -4.5f, -1.0f);
        this.b.addChild(this.h);
        (this.j = new Cuboid(this, -4, 0)).addBox(-2.0f, 0.0f, 0.0f, 2, 0, 2);
        this.j.setRotationPoint(-1.5f, 21.5f, 0.0f);
        this.j.roll = -0.7853982f;
        (this.k = new Cuboid(this, 0, 0)).addBox(0.0f, 0.0f, 0.0f, 2, 0, 2);
        this.k.setRotationPoint(1.5f, 21.5f, 0.0f);
        this.k.roll = 0.7853982f;
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
        this.b.render(scale);
        this.f.render(scale);
        this.j.render(scale);
        this.k.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        float float8 = 1.0f;
        float float9 = 1.0f;
        if (!entity.isInsideWater()) {
            float8 = 1.3f;
            float9 = 1.7f;
        }
        this.b.yaw = -float8 * 0.25f * MathHelper.sin(float9 * 0.6f * age);
    }
}
