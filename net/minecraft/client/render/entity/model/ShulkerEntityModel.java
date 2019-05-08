package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.ShulkerEntity;

@Environment(EnvType.CLIENT)
public class ShulkerEntityModel<T extends ShulkerEntity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    
    public ShulkerEntityModel() {
        this.textureHeight = 64;
        this.textureWidth = 64;
        this.b = new Cuboid(this);
        this.a = new Cuboid(this);
        this.f = new Cuboid(this);
        this.b.setTextureOffset(0, 0).addBox(-8.0f, -16.0f, -8.0f, 16, 12, 16);
        this.b.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.a.setTextureOffset(0, 28).addBox(-8.0f, -8.0f, -8.0f, 16, 8, 16);
        this.a.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.f.setTextureOffset(0, 52).addBox(-3.0f, 0.0f, -3.0f, 6, 6, 6);
        this.f.setRotationPoint(0.0f, 12.0f, 0.0f);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        final float float8 = age - entity.age;
        final float float9 = (0.5f + entity.v(float8)) * 3.1415927f;
        final float float10 = -1.0f + MathHelper.sin(float9);
        float float11 = 0.0f;
        if (float9 > 3.1415927f) {
            float11 = MathHelper.sin(age * 0.1f) * 0.7f;
        }
        this.b.setRotationPoint(0.0f, 16.0f + MathHelper.sin(float9) * 8.0f + float11, 0.0f);
        if (entity.v(float8) > 0.3f) {
            this.b.yaw = float10 * float10 * float10 * float10 * 3.1415927f * 0.125f;
        }
        else {
            this.b.yaw = 0.0f;
        }
        this.f.pitch = headPitch * 0.017453292f;
        this.f.yaw = headYaw * 0.017453292f;
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.a.render(scale);
        this.b.render(scale);
    }
    
    public Cuboid a() {
        return this.a;
    }
    
    public Cuboid b() {
        return this.b;
    }
    
    public Cuboid c() {
        return this.f;
    }
}
