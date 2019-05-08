package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class DolphinEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    
    public DolphinEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        final float float1 = 18.0f;
        final float float2 = -8.0f;
        (this.b = new Cuboid(this, 22, 0)).addBox(-4.0f, -7.0f, 0.0f, 8, 7, 13);
        this.b.setRotationPoint(0.0f, 22.0f, -5.0f);
        final Cuboid cuboid3 = new Cuboid(this, 51, 0);
        cuboid3.addBox(-0.5f, 0.0f, 8.0f, 1, 4, 5);
        cuboid3.pitch = 1.0471976f;
        this.b.addChild(cuboid3);
        final Cuboid cuboid4 = new Cuboid(this, 48, 20);
        cuboid4.mirror = true;
        cuboid4.addBox(-0.5f, -4.0f, 0.0f, 1, 4, 7);
        cuboid4.setRotationPoint(2.0f, -2.0f, 4.0f);
        cuboid4.pitch = 1.0471976f;
        cuboid4.roll = 2.0943952f;
        this.b.addChild(cuboid4);
        final Cuboid cuboid5 = new Cuboid(this, 48, 20);
        cuboid5.addBox(-0.5f, -4.0f, 0.0f, 1, 4, 7);
        cuboid5.setRotationPoint(-2.0f, -2.0f, 4.0f);
        cuboid5.pitch = 1.0471976f;
        cuboid5.roll = -2.0943952f;
        this.b.addChild(cuboid5);
        (this.f = new Cuboid(this, 0, 19)).addBox(-2.0f, -2.5f, 0.0f, 4, 5, 11);
        this.f.setRotationPoint(0.0f, -2.5f, 11.0f);
        this.f.pitch = -0.10471976f;
        this.b.addChild(this.f);
        (this.g = new Cuboid(this, 19, 20)).addBox(-5.0f, -0.5f, 0.0f, 10, 1, 6);
        this.g.setRotationPoint(0.0f, 0.0f, 9.0f);
        this.g.pitch = 0.0f;
        this.f.addChild(this.g);
        (this.a = new Cuboid(this, 0, 0)).addBox(-4.0f, -3.0f, -3.0f, 8, 7, 6);
        this.a.setRotationPoint(0.0f, -4.0f, -3.0f);
        final Cuboid cuboid6 = new Cuboid(this, 0, 13);
        cuboid6.addBox(-1.0f, 2.0f, -7.0f, 2, 2, 4);
        this.a.addChild(cuboid6);
        this.b.addChild(this.a);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.b.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.b.pitch = headPitch * 0.017453292f;
        this.b.yaw = headYaw * 0.017453292f;
        if (Entity.squaredHorizontalLength(entity.getVelocity()) > 1.0E-7) {
            final Cuboid b = this.b;
            b.pitch += -0.05f + -0.05f * MathHelper.cos(age * 0.3f);
            this.f.pitch = -0.1f * MathHelper.cos(age * 0.3f);
            this.g.pitch = -0.2f * MathHelper.cos(age * 0.3f);
        }
    }
}
