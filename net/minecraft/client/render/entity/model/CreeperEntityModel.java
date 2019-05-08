package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class CreeperEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid head;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    private final Cuboid j;
    
    public CreeperEntityModel() {
        this(0.0f);
    }
    
    public CreeperEntityModel(final float float1) {
        final int integer2 = 6;
        (this.head = new Cuboid(this, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, float1);
        this.head.setRotationPoint(0.0f, 6.0f, 0.0f);
        (this.b = new Cuboid(this, 32, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, float1 + 0.5f);
        this.b.setRotationPoint(0.0f, 6.0f, 0.0f);
        (this.f = new Cuboid(this, 16, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, float1);
        this.f.setRotationPoint(0.0f, 6.0f, 0.0f);
        (this.g = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, float1);
        this.g.setRotationPoint(-2.0f, 18.0f, 4.0f);
        (this.h = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, float1);
        this.h.setRotationPoint(2.0f, 18.0f, 4.0f);
        (this.i = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, float1);
        this.i.setRotationPoint(-2.0f, 18.0f, -4.0f);
        (this.j = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, float1);
        this.j.setRotationPoint(2.0f, 18.0f, -4.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.head.render(scale);
        this.f.render(scale);
        this.g.render(scale);
        this.h.render(scale);
        this.i.render(scale);
        this.j.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.head.yaw = headYaw * 0.017453292f;
        this.head.pitch = headPitch * 0.017453292f;
        this.g.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.h.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
        this.i.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
        this.j.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
    }
}
