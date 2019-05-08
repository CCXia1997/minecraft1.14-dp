package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkullOverlayEntityModel extends SkullEntityModel
{
    private final Cuboid b;
    
    public SkullOverlayEntityModel() {
        super(0, 0, 64, 64);
        (this.b = new Cuboid(this, 32, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, 0.25f);
        this.b.setRotationPoint(0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void setRotationAngles(final float limbMoveAngle, final float limbMoveAmount, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setRotationAngles(limbMoveAngle, limbMoveAmount, age, headYaw, headPitch, scale);
        this.b.yaw = this.a.yaw;
        this.b.pitch = this.a.pitch;
        this.b.render(scale);
    }
}
