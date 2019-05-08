package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DragonHeadEntityModel extends SkullEntityModel
{
    private final Cuboid head;
    private final Cuboid jaw;
    
    public DragonHeadEntityModel(final float float1) {
        this.textureWidth = 256;
        this.textureHeight = 256;
        final float float2 = -16.0f;
        (this.head = new Cuboid(this, "head")).addBox("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, float1, 176, 44);
        this.head.addBox("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, float1, 112, 30);
        this.head.mirror = true;
        this.head.addBox("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, float1, 0, 0);
        this.head.addBox("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, float1, 112, 0);
        this.head.mirror = false;
        this.head.addBox("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, float1, 0, 0);
        this.head.addBox("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, float1, 112, 0);
        (this.jaw = new Cuboid(this, "jaw")).setRotationPoint(0.0f, 4.0f, -8.0f);
        this.jaw.addBox("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, float1, 176, 65);
        this.head.addChild(this.jaw);
    }
    
    @Override
    public void setRotationAngles(final float limbMoveAngle, final float limbMoveAmount, final float age, final float headYaw, final float headPitch, final float scale) {
        this.jaw.pitch = (float)(Math.sin(limbMoveAngle * 3.1415927f * 0.2f) + 1.0) * 0.2f;
        this.head.yaw = headYaw * 0.017453292f;
        this.head.pitch = headPitch * 0.017453292f;
        GlStateManager.translatef(0.0f, -0.374375f, 0.0f);
        GlStateManager.scalef(0.75f, 0.75f, 0.75f);
        this.head.render(scale);
    }
}
