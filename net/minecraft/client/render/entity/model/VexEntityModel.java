package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.VexEntity;

@Environment(EnvType.CLIENT)
public class VexEntityModel extends BipedEntityModel<VexEntity>
{
    private final Cuboid a;
    private final Cuboid b;
    
    public VexEntityModel() {
        this(0.0f);
    }
    
    public VexEntityModel(final float float1) {
        super(float1, 0.0f, 64, 64);
        this.leftLeg.visible = false;
        this.headwear.visible = false;
        (this.rightLeg = new Cuboid(this, 32, 0)).addBox(-1.0f, -1.0f, -2.0f, 6, 10, 4, 0.0f);
        this.rightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        (this.b = new Cuboid(this, 0, 32)).addBox(-20.0f, 0.0f, 0.0f, 20, 12, 1);
        this.a = new Cuboid(this, 0, 32);
        this.a.mirror = true;
        this.a.addBox(0.0f, 0.0f, 0.0f, 20, 12, 1);
    }
    
    @Override
    public void render(final VexEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.b.render(scale);
        this.a.render(scale);
    }
    
    @Override
    public void render(final VexEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (entity.isCharging()) {
            if (entity.getMainHand() == AbsoluteHand.b) {
                this.rightArm.pitch = 3.7699115f;
            }
            else {
                this.leftArm.pitch = 3.7699115f;
            }
        }
        final Cuboid rightLeg = this.rightLeg;
        rightLeg.pitch += 0.62831855f;
        this.b.rotationPointZ = 2.0f;
        this.a.rotationPointZ = 2.0f;
        this.b.rotationPointY = 1.0f;
        this.a.rotationPointY = 1.0f;
        this.b.yaw = 0.47123894f + MathHelper.cos(age * 0.8f) * 3.1415927f * 0.05f;
        this.a.yaw = -this.b.yaw;
        this.a.roll = -0.47123894f;
        this.a.pitch = 0.47123894f;
        this.b.pitch = 0.47123894f;
        this.b.roll = 0.47123894f;
    }
}
