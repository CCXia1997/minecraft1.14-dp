package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LeashEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    
    public LeashEntityModel() {
        this(0, 0, 32, 32);
    }
    
    public LeashEntityModel(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.textureWidth = integer3;
        this.textureHeight = integer4;
        (this.a = new Cuboid(this, integer1, integer2)).addBox(-3.0f, -6.0f, -3.0f, 6, 8, 6, 0.0f);
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
