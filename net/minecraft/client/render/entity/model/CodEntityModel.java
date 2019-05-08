package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class CodEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    private final Cuboid j;
    
    public CodEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        final int integer1 = 22;
        (this.a = new Cuboid(this, 0, 0)).addBox(-1.0f, -2.0f, 0.0f, 2, 4, 7);
        this.a.setRotationPoint(0.0f, 22.0f, 0.0f);
        (this.f = new Cuboid(this, 11, 0)).addBox(-1.0f, -2.0f, -3.0f, 2, 4, 3);
        this.f.setRotationPoint(0.0f, 22.0f, 0.0f);
        (this.g = new Cuboid(this, 0, 0)).addBox(-1.0f, -2.0f, -1.0f, 2, 3, 1);
        this.g.setRotationPoint(0.0f, 22.0f, -3.0f);
        (this.h = new Cuboid(this, 22, 1)).addBox(-2.0f, 0.0f, -1.0f, 2, 0, 2);
        this.h.setRotationPoint(-1.0f, 23.0f, 0.0f);
        this.h.roll = -0.7853982f;
        (this.i = new Cuboid(this, 22, 4)).addBox(0.0f, 0.0f, -1.0f, 2, 0, 2);
        this.i.setRotationPoint(1.0f, 23.0f, 0.0f);
        this.i.roll = 0.7853982f;
        (this.j = new Cuboid(this, 22, 3)).addBox(0.0f, -2.0f, 0.0f, 0, 4, 4);
        this.j.setRotationPoint(0.0f, 22.0f, 7.0f);
        (this.b = new Cuboid(this, 20, -6)).addBox(0.0f, -1.0f, -1.0f, 0, 1, 6);
        this.b.setRotationPoint(0.0f, 20.0f, 0.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
        this.f.render(scale);
        this.g.render(scale);
        this.h.render(scale);
        this.i.render(scale);
        this.j.render(scale);
        this.b.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        float float8 = 1.0f;
        if (!entity.isInsideWater()) {
            float8 = 1.5f;
        }
        this.j.yaw = -float8 * 0.45f * MathHelper.sin(0.6f * age);
    }
}
