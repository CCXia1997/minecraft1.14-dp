package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SilverfishEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid[] a;
    private final Cuboid[] b;
    private final float[] f;
    private static final int[][] g;
    private static final int[][] h;
    
    public SilverfishEntityModel() {
        this.f = new float[7];
        this.a = new Cuboid[7];
        float float1 = -3.5f;
        for (int integer2 = 0; integer2 < this.a.length; ++integer2) {
            (this.a[integer2] = new Cuboid(this, SilverfishEntityModel.h[integer2][0], SilverfishEntityModel.h[integer2][1])).addBox(SilverfishEntityModel.g[integer2][0] * -0.5f, 0.0f, SilverfishEntityModel.g[integer2][2] * -0.5f, SilverfishEntityModel.g[integer2][0], SilverfishEntityModel.g[integer2][1], SilverfishEntityModel.g[integer2][2]);
            this.a[integer2].setRotationPoint(0.0f, (float)(24 - SilverfishEntityModel.g[integer2][1]), float1);
            this.f[integer2] = float1;
            if (integer2 < this.a.length - 1) {
                float1 += (SilverfishEntityModel.g[integer2][2] + SilverfishEntityModel.g[integer2 + 1][2]) * 0.5f;
            }
        }
        this.b = new Cuboid[3];
        (this.b[0] = new Cuboid(this, 20, 0)).addBox(-5.0f, 0.0f, SilverfishEntityModel.g[2][2] * -0.5f, 10, 8, SilverfishEntityModel.g[2][2]);
        this.b[0].setRotationPoint(0.0f, 16.0f, this.f[2]);
        (this.b[1] = new Cuboid(this, 20, 11)).addBox(-3.0f, 0.0f, SilverfishEntityModel.g[4][2] * -0.5f, 6, 4, SilverfishEntityModel.g[4][2]);
        this.b[1].setRotationPoint(0.0f, 20.0f, this.f[4]);
        (this.b[2] = new Cuboid(this, 20, 18)).addBox(-3.0f, 0.0f, SilverfishEntityModel.g[4][2] * -0.5f, 6, 5, SilverfishEntityModel.g[1][2]);
        this.b[2].setRotationPoint(0.0f, 19.0f, this.f[1]);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        for (final Cuboid cuboid11 : this.a) {
            cuboid11.render(scale);
        }
        for (final Cuboid cuboid11 : this.b) {
            cuboid11.render(scale);
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        for (int integer8 = 0; integer8 < this.a.length; ++integer8) {
            this.a[integer8].yaw = MathHelper.cos(age * 0.9f + integer8 * 0.15f * 3.1415927f) * 3.1415927f * 0.05f * (1 + Math.abs(integer8 - 2));
            this.a[integer8].rotationPointX = MathHelper.sin(age * 0.9f + integer8 * 0.15f * 3.1415927f) * 3.1415927f * 0.2f * Math.abs(integer8 - 2);
        }
        this.b[0].yaw = this.a[2].yaw;
        this.b[1].yaw = this.a[4].yaw;
        this.b[1].rotationPointX = this.a[4].rotationPointX;
        this.b[2].yaw = this.a[1].yaw;
        this.b[2].rotationPointX = this.a[1].rotationPointX;
    }
    
    static {
        g = new int[][] { { 3, 2, 2 }, { 4, 3, 2 }, { 6, 4, 3 }, { 3, 3, 3 }, { 2, 2, 3 }, { 2, 1, 2 }, { 1, 1, 2 } };
        h = new int[][] { { 0, 0 }, { 0, 4 }, { 0, 9 }, { 0, 16 }, { 0, 22 }, { 11, 0 }, { 13, 4 } };
    }
}
