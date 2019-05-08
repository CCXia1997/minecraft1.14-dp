package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    
    public ShulkerBulletEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.a = new Cuboid(this);
        this.a.setTextureOffset(0, 0).addBox(-4.0f, -4.0f, -1.0f, 8, 8, 2, 0.0f);
        this.a.setTextureOffset(0, 10).addBox(-1.0f, -4.0f, -4.0f, 2, 8, 8, 0.0f);
        this.a.setTextureOffset(20, 0).addBox(-4.0f, -1.0f, -4.0f, 8, 2, 8, 0.0f);
        this.a.setRotationPoint(0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.yaw = headYaw * 0.017453292f;
        this.a.pitch = headPitch * 0.017453292f;
    }
}
