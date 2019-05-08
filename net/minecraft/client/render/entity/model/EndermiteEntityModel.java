package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class EndermiteEntityModel<T extends Entity> extends EntityModel<T>
{
    private static final int[][] a;
    private static final int[][] b;
    private static final int f;
    private final Cuboid[] g;
    
    public EndermiteEntityModel() {
        this.g = new Cuboid[EndermiteEntityModel.f];
        float float1 = -3.5f;
        for (int integer2 = 0; integer2 < this.g.length; ++integer2) {
            (this.g[integer2] = new Cuboid(this, EndermiteEntityModel.b[integer2][0], EndermiteEntityModel.b[integer2][1])).addBox(EndermiteEntityModel.a[integer2][0] * -0.5f, 0.0f, EndermiteEntityModel.a[integer2][2] * -0.5f, EndermiteEntityModel.a[integer2][0], EndermiteEntityModel.a[integer2][1], EndermiteEntityModel.a[integer2][2]);
            this.g[integer2].setRotationPoint(0.0f, (float)(24 - EndermiteEntityModel.a[integer2][1]), float1);
            if (integer2 < this.g.length - 1) {
                float1 += (EndermiteEntityModel.a[integer2][2] + EndermiteEntityModel.a[integer2 + 1][2]) * 0.5f;
            }
        }
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        for (final Cuboid cuboid11 : this.g) {
            cuboid11.render(scale);
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        for (int integer8 = 0; integer8 < this.g.length; ++integer8) {
            this.g[integer8].yaw = MathHelper.cos(age * 0.9f + integer8 * 0.15f * 3.1415927f) * 3.1415927f * 0.01f * (1 + Math.abs(integer8 - 2));
            this.g[integer8].rotationPointX = MathHelper.sin(age * 0.9f + integer8 * 0.15f * 3.1415927f) * 3.1415927f * 0.1f * Math.abs(integer8 - 2);
        }
    }
    
    static {
        a = new int[][] { { 4, 3, 2 }, { 6, 4, 5 }, { 3, 3, 1 }, { 1, 2, 1 } };
        b = new int[][] { { 0, 0 }, { 0, 5 }, { 0, 14 }, { 0, 18 } };
        f = EndermiteEntityModel.a.length;
    }
}
