package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SmallPufferfishEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    
    public SmallPufferfishEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        final int integer1 = 23;
        (this.a = new Cuboid(this, 0, 27)).addBox(-1.5f, -2.0f, -1.5f, 3, 2, 3);
        this.a.setRotationPoint(0.0f, 23.0f, 0.0f);
        (this.b = new Cuboid(this, 24, 6)).addBox(-1.5f, 0.0f, -1.5f, 1, 1, 1);
        this.b.setRotationPoint(0.0f, 20.0f, 0.0f);
        (this.f = new Cuboid(this, 28, 6)).addBox(0.5f, 0.0f, -1.5f, 1, 1, 1);
        this.f.setRotationPoint(0.0f, 20.0f, 0.0f);
        (this.i = new Cuboid(this, -3, 0)).addBox(-1.5f, 0.0f, 0.0f, 3, 0, 3);
        this.i.setRotationPoint(0.0f, 22.0f, 1.5f);
        (this.g = new Cuboid(this, 25, 0)).addBox(-1.0f, 0.0f, 0.0f, 1, 0, 2);
        this.g.setRotationPoint(-1.5f, 22.0f, -1.5f);
        (this.h = new Cuboid(this, 25, 0)).addBox(0.0f, 0.0f, 0.0f, 1, 0, 2);
        this.h.setRotationPoint(1.5f, 22.0f, -1.5f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
        this.b.render(scale);
        this.f.render(scale);
        this.i.render(scale);
        this.g.render(scale);
        this.h.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.g.roll = -0.2f + 0.4f * MathHelper.sin(age * 0.2f);
        this.h.roll = 0.2f - 0.4f * MathHelper.sin(age * 0.2f);
    }
}
