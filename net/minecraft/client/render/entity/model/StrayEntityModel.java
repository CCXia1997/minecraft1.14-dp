package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StrayEntityModel<T extends MobEntity> extends BipedEntityModel<T>
{
    public StrayEntityModel() {
        this(0.0f, false);
    }
    
    public StrayEntityModel(final float float1, final boolean boolean2) {
        super(float1, 0.0f, 64, 32);
        if (!boolean2) {
            (this.rightArm = new Cuboid(this, 40, 16)).addBox(-1.0f, -2.0f, -1.0f, 2, 12, 2, float1);
            this.rightArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
            this.leftArm = new Cuboid(this, 40, 16);
            this.leftArm.mirror = true;
            this.leftArm.addBox(-1.0f, -2.0f, -1.0f, 2, 12, 2, float1);
            this.leftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
            (this.rightLeg = new Cuboid(this, 0, 16)).addBox(-1.0f, 0.0f, -1.0f, 2, 12, 2, float1);
            this.rightLeg.setRotationPoint(-2.0f, 12.0f, 0.0f);
            this.leftLeg = new Cuboid(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 12, 2, float1);
            this.leftLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
        }
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        this.rightArmPose = ArmPose.a;
        this.leftArmPose = ArmPose.a;
        final ItemStack itemStack5 = ((LivingEntity)entity).getStackInHand(Hand.a);
        if (itemStack5.getItem() == Items.jf && ((MobEntity)entity).isAttacking()) {
            if (((MobEntity)entity).getMainHand() == AbsoluteHand.b) {
                this.rightArmPose = ArmPose.d;
            }
            else {
                this.leftArmPose = ArmPose.d;
            }
        }
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        final ItemStack itemStack8 = ((LivingEntity)entity).getMainHandStack();
        if (((MobEntity)entity).isAttacking() && (itemStack8.isEmpty() || itemStack8.getItem() != Items.jf)) {
            final float float9 = MathHelper.sin(this.handSwingProgress * 3.1415927f);
            final float float10 = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * 3.1415927f);
            this.rightArm.roll = 0.0f;
            this.leftArm.roll = 0.0f;
            this.rightArm.yaw = -(0.1f - float9 * 0.6f);
            this.leftArm.yaw = 0.1f - float9 * 0.6f;
            this.rightArm.pitch = -1.5707964f;
            this.leftArm.pitch = -1.5707964f;
            final Cuboid rightArm = this.rightArm;
            rightArm.pitch -= float9 * 1.2f - float10 * 0.4f;
            final Cuboid leftArm = this.leftArm;
            leftArm.pitch -= float9 * 1.2f - float10 * 0.4f;
            final Cuboid rightArm2 = this.rightArm;
            rightArm2.roll += MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
            final Cuboid leftArm2 = this.leftArm;
            leftArm2.roll -= MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
            final Cuboid rightArm3 = this.rightArm;
            rightArm3.pitch += MathHelper.sin(age * 0.067f) * 0.05f;
            final Cuboid leftArm3 = this.leftArm;
            leftArm3.pitch -= MathHelper.sin(age * 0.067f) * 0.05f;
        }
    }
    
    @Override
    public void setArmAngle(final float float1, final AbsoluteHand absoluteHand) {
        final float float2 = (absoluteHand == AbsoluteHand.b) ? 1.0f : -1.0f;
        final Cuboid arm;
        final Cuboid cuboid4 = arm = this.getArm(absoluteHand);
        arm.rotationPointX += float2;
        cuboid4.applyTransform(float1);
        final Cuboid cuboid5 = cuboid4;
        cuboid5.rotationPointX -= float2;
    }
}
