package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class BlazeEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid[] a;
    private final Cuboid b;
    
    public BlazeEntityModel() {
        this.a = new Cuboid[12];
        for (int integer1 = 0; integer1 < this.a.length; ++integer1) {
            (this.a[integer1] = new Cuboid(this, 0, 16)).addBox(0.0f, 0.0f, 0.0f, 2, 8, 2);
        }
        (this.b = new Cuboid(this, 0, 0)).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.b.render(scale);
        for (final Cuboid cuboid11 : this.a) {
            cuboid11.render(scale);
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        float float8 = age * 3.1415927f * -0.1f;
        for (int integer9 = 0; integer9 < 4; ++integer9) {
            this.a[integer9].rotationPointY = -2.0f + MathHelper.cos((integer9 * 2 + age) * 0.25f);
            this.a[integer9].rotationPointX = MathHelper.cos(float8) * 9.0f;
            this.a[integer9].rotationPointZ = MathHelper.sin(float8) * 9.0f;
            float8 += 1.5707964f;
        }
        float8 = 0.7853982f + age * 3.1415927f * 0.03f;
        for (int integer9 = 4; integer9 < 8; ++integer9) {
            this.a[integer9].rotationPointY = 2.0f + MathHelper.cos((integer9 * 2 + age) * 0.25f);
            this.a[integer9].rotationPointX = MathHelper.cos(float8) * 7.0f;
            this.a[integer9].rotationPointZ = MathHelper.sin(float8) * 7.0f;
            float8 += 1.5707964f;
        }
        float8 = 0.47123894f + age * 3.1415927f * -0.05f;
        for (int integer9 = 8; integer9 < 12; ++integer9) {
            this.a[integer9].rotationPointY = 11.0f + MathHelper.cos((integer9 * 1.5f + age) * 0.5f);
            this.a[integer9].rotationPointX = MathHelper.cos(float8) * 5.0f;
            this.a[integer9].rotationPointZ = MathHelper.sin(float8) * 5.0f;
            float8 += 1.5707964f;
        }
        this.b.yaw = headYaw * 0.017453292f;
        this.b.pitch = headPitch * 0.017453292f;
    }
}
