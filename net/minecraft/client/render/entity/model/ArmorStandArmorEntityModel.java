package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.ArmorStandEntity;

@Environment(EnvType.CLIENT)
public class ArmorStandArmorEntityModel extends BipedEntityModel<ArmorStandEntity>
{
    public ArmorStandArmorEntityModel() {
        this(0.0f);
    }
    
    public ArmorStandArmorEntityModel(final float float1) {
        this(float1, 64, 32);
    }
    
    protected ArmorStandArmorEntityModel(final float scale, final int textureWidth, final int integer3) {
        super(scale, 0.0f, textureWidth, integer3);
    }
    
    @Override
    public void setAngles(final ArmorStandEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.head.pitch = 0.017453292f * entity.getHeadRotation().getX();
        this.head.yaw = 0.017453292f * entity.getHeadRotation().getY();
        this.head.roll = 0.017453292f * entity.getHeadRotation().getZ();
        this.head.setRotationPoint(0.0f, 1.0f, 0.0f);
        this.body.pitch = 0.017453292f * entity.getBodyRotation().getX();
        this.body.yaw = 0.017453292f * entity.getBodyRotation().getY();
        this.body.roll = 0.017453292f * entity.getBodyRotation().getZ();
        this.leftArm.pitch = 0.017453292f * entity.getLeftArmRotation().getX();
        this.leftArm.yaw = 0.017453292f * entity.getLeftArmRotation().getY();
        this.leftArm.roll = 0.017453292f * entity.getLeftArmRotation().getZ();
        this.rightArm.pitch = 0.017453292f * entity.getRightArmRotation().getX();
        this.rightArm.yaw = 0.017453292f * entity.getRightArmRotation().getY();
        this.rightArm.roll = 0.017453292f * entity.getRightArmRotation().getZ();
        this.leftLeg.pitch = 0.017453292f * entity.getLeftLegRotation().getX();
        this.leftLeg.yaw = 0.017453292f * entity.getLeftLegRotation().getY();
        this.leftLeg.roll = 0.017453292f * entity.getLeftLegRotation().getZ();
        this.leftLeg.setRotationPoint(1.9f, 11.0f, 0.0f);
        this.rightLeg.pitch = 0.017453292f * entity.getRightLegRotation().getX();
        this.rightLeg.yaw = 0.017453292f * entity.getRightLegRotation().getY();
        this.rightLeg.roll = 0.017453292f * entity.getRightLegRotation().getZ();
        this.rightLeg.setRotationPoint(-1.9f, 11.0f, 0.0f);
        this.headwear.copyRotation(this.head);
    }
}
