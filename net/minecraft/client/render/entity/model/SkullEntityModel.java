package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class SkullEntityModel extends Model
{
    protected final Cuboid a;
    
    public SkullEntityModel() {
        this(0, 35, 64, 64);
    }
    
    public SkullEntityModel(final int integer1, final int integer2, final int integer3, final int integer4) {
        this.textureWidth = integer3;
        this.textureHeight = integer4;
        (this.a = new Cuboid(this, integer1, integer2)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, 0.0f);
        this.a.setRotationPoint(0.0f, 0.0f, 0.0f);
    }
    
    public void setRotationAngles(final float limbMoveAngle, final float limbMoveAmount, final float age, final float headYaw, final float headPitch, final float scale) {
        this.a.yaw = headYaw * 0.017453292f;
        this.a.pitch = headPitch * 0.017453292f;
        this.a.render(scale);
    }
}
