package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.WitherEntity;

@Environment(EnvType.CLIENT)
public class WitherEntityModel<T extends WitherEntity> extends EntityModel<T>
{
    private final Cuboid[] a;
    private final Cuboid[] b;
    
    public WitherEntityModel(final float float1) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.a = new Cuboid[3];
        (this.a[0] = new Cuboid(this, 0, 16)).addBox(-10.0f, 3.9f, -0.5f, 20, 3, 3, float1);
        (this.a[1] = new Cuboid(this).setTextureSize(this.textureWidth, this.textureHeight)).setRotationPoint(-2.0f, 6.9f, -0.5f);
        this.a[1].setTextureOffset(0, 22).addBox(0.0f, 0.0f, 0.0f, 3, 10, 3, float1);
        this.a[1].setTextureOffset(24, 22).addBox(-4.0f, 1.5f, 0.5f, 11, 2, 2, float1);
        this.a[1].setTextureOffset(24, 22).addBox(-4.0f, 4.0f, 0.5f, 11, 2, 2, float1);
        this.a[1].setTextureOffset(24, 22).addBox(-4.0f, 6.5f, 0.5f, 11, 2, 2, float1);
        (this.a[2] = new Cuboid(this, 12, 22)).addBox(0.0f, 0.0f, 0.0f, 3, 6, 3, float1);
        this.b = new Cuboid[3];
        (this.b[0] = new Cuboid(this, 0, 0)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8, float1);
        (this.b[1] = new Cuboid(this, 32, 0)).addBox(-4.0f, -4.0f, -4.0f, 6, 6, 6, float1);
        this.b[1].rotationPointX = -8.0f;
        this.b[1].rotationPointY = 4.0f;
        (this.b[2] = new Cuboid(this, 32, 0)).addBox(-4.0f, -4.0f, -4.0f, 6, 6, 6, float1);
        this.b[2].rotationPointX = 10.0f;
        this.b[2].rotationPointY = 4.0f;
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        for (final Cuboid cuboid11 : this.b) {
            cuboid11.render(scale);
        }
        for (final Cuboid cuboid11 : this.a) {
            cuboid11.render(scale);
        }
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        final float float8 = MathHelper.cos(age * 0.1f);
        this.a[1].pitch = (0.065f + 0.05f * float8) * 3.1415927f;
        this.a[2].setRotationPoint(-2.0f, 6.9f + MathHelper.cos(this.a[1].pitch) * 10.0f, -0.5f + MathHelper.sin(this.a[1].pitch) * 10.0f);
        this.a[2].pitch = (0.265f + 0.1f * float8) * 3.1415927f;
        this.b[0].yaw = headYaw * 0.017453292f;
        this.b[0].pitch = headPitch * 0.017453292f;
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        for (int integer5 = 1; integer5 < 3; ++integer5) {
            this.b[integer5].yaw = (entity.a(integer5 - 1) - entity.aK) * 0.017453292f;
            this.b[integer5].pitch = entity.b(integer5 - 1) * 0.017453292f;
        }
    }
}
