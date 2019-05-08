package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SquidEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid[] b;
    
    public SquidEntityModel() {
        this.b = new Cuboid[8];
        final int integer1 = -16;
        (this.a = new Cuboid(this, 0, 0)).addBox(-6.0f, -8.0f, -6.0f, 12, 16, 12);
        final Cuboid a = this.a;
        a.rotationPointY += 8.0f;
        for (int integer2 = 0; integer2 < this.b.length; ++integer2) {
            this.b[integer2] = new Cuboid(this, 48, 0);
            double double3 = integer2 * 3.141592653589793 * 2.0 / this.b.length;
            final float float5 = (float)Math.cos(double3) * 5.0f;
            final float float6 = (float)Math.sin(double3) * 5.0f;
            this.b[integer2].addBox(-1.0f, 0.0f, -1.0f, 2, 18, 2);
            this.b[integer2].rotationPointX = float5;
            this.b[integer2].rotationPointZ = float6;
            this.b[integer2].rotationPointY = 15.0f;
            double3 = integer2 * 3.141592653589793 * -2.0 / this.b.length + 1.5707963267948966;
            this.b[integer2].yaw = (float)double3;
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        for (final Cuboid cuboid11 : this.b) {
            cuboid11.pitch = age;
        }
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
        for (final Cuboid cuboid11 : this.b) {
            cuboid11.render(scale);
        }
    }
}
