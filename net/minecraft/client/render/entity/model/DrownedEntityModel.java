package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class DrownedEntityModel<T extends ZombieEntity> extends ZombieEntityModel<T>
{
    public DrownedEntityModel(final float scale, final float float2, final int textureWidth, final int integer4) {
        super(scale, float2, textureWidth, integer4);
        (this.rightArm = new Cuboid(this, 32, 48)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, scale);
        this.rightArm.setRotationPoint(-5.0f, 2.0f + float2, 0.0f);
        (this.rightLeg = new Cuboid(this, 16, 48)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale);
        this.rightLeg.setRotationPoint(-1.9f, 12.0f + float2, 0.0f);
    }
    
    public DrownedEntityModel(final float float1, final boolean boolean2) {
        super(float1, 0.0f, 64, boolean2 ? 32 : 64);
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        this.rightArmPose = ArmPose.a;
        this.leftArmPose = ArmPose.a;
        final ItemStack itemStack5 = entity.getStackInHand(Hand.a);
        if (itemStack5.getItem() == Items.pu && entity.isAttacking()) {
            if (entity.getMainHand() == AbsoluteHand.b) {
                this.rightArmPose = ArmPose.e;
            }
            else {
                this.leftArmPose = ArmPose.e;
            }
        }
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.leftArmPose == ArmPose.e) {
            this.leftArm.pitch = this.leftArm.pitch * 0.5f - 3.1415927f;
            this.leftArm.yaw = 0.0f;
        }
        if (this.rightArmPose == ArmPose.e) {
            this.rightArm.pitch = this.rightArm.pitch * 0.5f - 3.1415927f;
            this.rightArm.yaw = 0.0f;
        }
        if (this.p > 0.0f) {
            this.rightArm.pitch = this.a(this.rightArm.pitch, -2.5132742f, this.p) + this.p * 0.35f * MathHelper.sin(0.1f * age);
            this.leftArm.pitch = this.a(this.leftArm.pitch, -2.5132742f, this.p) - this.p * 0.35f * MathHelper.sin(0.1f * age);
            this.rightArm.roll = this.a(this.rightArm.roll, -0.15f, this.p);
            this.leftArm.roll = this.a(this.leftArm.roll, 0.15f, this.p);
            final Cuboid leftLeg = this.leftLeg;
            leftLeg.pitch -= this.p * 0.55f * MathHelper.sin(0.1f * age);
            final Cuboid rightLeg = this.rightLeg;
            rightLeg.pitch += this.p * 0.55f * MathHelper.sin(0.1f * age);
            this.head.pitch = 0.0f;
        }
    }
}
