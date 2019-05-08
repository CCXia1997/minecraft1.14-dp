package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class EndermanEntityModel<T extends LivingEntity> extends BipedEntityModel<T>
{
    public boolean carryingBlock;
    public boolean angry;
    
    public EndermanEntityModel(final float float1) {
        super(0.0f, -14.0f, 64, 32);
        final float float2 = -14.0f;
        (this.headwear = new Cuboid(this, 0, 16)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, float1 - 0.5f);
        this.headwear.setRotationPoint(0.0f, -14.0f, 0.0f);
        (this.body = new Cuboid(this, 32, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, float1);
        this.body.setRotationPoint(0.0f, -14.0f, 0.0f);
        (this.rightArm = new Cuboid(this, 56, 0)).addBox(-1.0f, -2.0f, -1.0f, 2, 30, 2, float1);
        this.rightArm.setRotationPoint(-3.0f, -12.0f, 0.0f);
        this.leftArm = new Cuboid(this, 56, 0);
        this.leftArm.mirror = true;
        this.leftArm.addBox(-1.0f, -2.0f, -1.0f, 2, 30, 2, float1);
        this.leftArm.setRotationPoint(5.0f, -12.0f, 0.0f);
        (this.rightLeg = new Cuboid(this, 56, 0)).addBox(-1.0f, 0.0f, -1.0f, 2, 30, 2, float1);
        this.rightLeg.setRotationPoint(-2.0f, -2.0f, 0.0f);
        this.leftLeg = new Cuboid(this, 56, 0);
        this.leftLeg.mirror = true;
        this.leftLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 30, 2, float1);
        this.leftLeg.setRotationPoint(2.0f, -2.0f, 0.0f);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.head.visible = true;
        final float float8 = -14.0f;
        this.body.pitch = 0.0f;
        this.body.rotationPointY = -14.0f;
        this.body.rotationPointZ = -0.0f;
        final Cuboid rightLeg = this.rightLeg;
        rightLeg.pitch -= 0.0f;
        final Cuboid leftLeg = this.leftLeg;
        leftLeg.pitch -= 0.0f;
        final Cuboid rightArm = this.rightArm;
        rightArm.pitch *= 0.5;
        final Cuboid leftArm = this.leftArm;
        leftArm.pitch *= 0.5;
        final Cuboid rightLeg2 = this.rightLeg;
        rightLeg2.pitch *= 0.5;
        final Cuboid leftLeg2 = this.leftLeg;
        leftLeg2.pitch *= 0.5;
        final float float9 = 0.4f;
        if (this.rightArm.pitch > 0.4f) {
            this.rightArm.pitch = 0.4f;
        }
        if (this.leftArm.pitch > 0.4f) {
            this.leftArm.pitch = 0.4f;
        }
        if (this.rightArm.pitch < -0.4f) {
            this.rightArm.pitch = -0.4f;
        }
        if (this.leftArm.pitch < -0.4f) {
            this.leftArm.pitch = -0.4f;
        }
        if (this.rightLeg.pitch > 0.4f) {
            this.rightLeg.pitch = 0.4f;
        }
        if (this.leftLeg.pitch > 0.4f) {
            this.leftLeg.pitch = 0.4f;
        }
        if (this.rightLeg.pitch < -0.4f) {
            this.rightLeg.pitch = -0.4f;
        }
        if (this.leftLeg.pitch < -0.4f) {
            this.leftLeg.pitch = -0.4f;
        }
        if (this.carryingBlock) {
            this.rightArm.pitch = -0.5f;
            this.leftArm.pitch = -0.5f;
            this.rightArm.roll = 0.05f;
            this.leftArm.roll = -0.05f;
        }
        this.rightArm.rotationPointZ = 0.0f;
        this.leftArm.rotationPointZ = 0.0f;
        this.rightLeg.rotationPointZ = 0.0f;
        this.leftLeg.rotationPointZ = 0.0f;
        this.rightLeg.rotationPointY = -5.0f;
        this.leftLeg.rotationPointY = -5.0f;
        this.head.rotationPointZ = -0.0f;
        this.head.rotationPointY = -13.0f;
        this.headwear.rotationPointX = this.head.rotationPointX;
        this.headwear.rotationPointY = this.head.rotationPointY;
        this.headwear.rotationPointZ = this.head.rotationPointZ;
        this.headwear.pitch = this.head.pitch;
        this.headwear.yaw = this.head.yaw;
        this.headwear.roll = this.head.roll;
        if (this.angry) {
            final float float10 = 1.0f;
            final Cuboid head = this.head;
            head.rotationPointY -= 5.0f;
        }
    }
}
