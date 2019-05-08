package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class MinecartEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid[] a;
    
    public MinecartEntityModel() {
        (this.a = new Cuboid[7])[0] = new Cuboid(this, 0, 10);
        this.a[1] = new Cuboid(this, 0, 0);
        this.a[2] = new Cuboid(this, 0, 0);
        this.a[3] = new Cuboid(this, 0, 0);
        this.a[4] = new Cuboid(this, 0, 0);
        this.a[5] = new Cuboid(this, 44, 10);
        final int integer1 = 20;
        final int integer2 = 8;
        final int integer3 = 16;
        final int integer4 = 4;
        this.a[0].addBox(-10.0f, -8.0f, -1.0f, 20, 16, 2, 0.0f);
        this.a[0].setRotationPoint(0.0f, 4.0f, 0.0f);
        this.a[5].addBox(-9.0f, -7.0f, -1.0f, 18, 14, 1, 0.0f);
        this.a[5].setRotationPoint(0.0f, 4.0f, 0.0f);
        this.a[1].addBox(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
        this.a[1].setRotationPoint(-9.0f, 4.0f, 0.0f);
        this.a[2].addBox(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
        this.a[2].setRotationPoint(9.0f, 4.0f, 0.0f);
        this.a[3].addBox(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
        this.a[3].setRotationPoint(0.0f, 4.0f, -7.0f);
        this.a[4].addBox(-8.0f, -9.0f, -1.0f, 16, 8, 2, 0.0f);
        this.a[4].setRotationPoint(0.0f, 4.0f, 7.0f);
        this.a[0].pitch = 1.5707964f;
        this.a[1].yaw = 4.712389f;
        this.a[2].yaw = 1.5707964f;
        this.a[3].yaw = 3.1415927f;
        this.a[5].pitch = -1.5707964f;
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.a[5].rotationPointY = 4.0f - age;
        for (int integer8 = 0; integer8 < 6; ++integer8) {
            this.a[integer8].render(scale);
        }
    }
}
