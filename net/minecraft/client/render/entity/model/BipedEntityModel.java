package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class BipedEntityModel<T extends LivingEntity> extends EntityModel<T> implements ModelWithArms, ModelWithHead
{
    public Cuboid head;
    public Cuboid headwear;
    public Cuboid body;
    public Cuboid rightArm;
    public Cuboid leftArm;
    public Cuboid rightLeg;
    public Cuboid leftLeg;
    public ArmPose leftArmPose;
    public ArmPose rightArmPose;
    public boolean isSneaking;
    public float p;
    private float a;
    
    public BipedEntityModel() {
        this(0.0f);
    }
    
    public BipedEntityModel(final float float1) {
        this(float1, 0.0f, 64, 32);
    }
    
    public BipedEntityModel(final float scale, final float float2, final int textureWidth, final int integer4) {
        this.leftArmPose = ArmPose.a;
        this.rightArmPose = ArmPose.a;
        this.textureWidth = textureWidth;
        this.textureHeight = integer4;
        (this.head = new Cuboid(this, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, scale);
        this.head.setRotationPoint(0.0f, 0.0f + float2, 0.0f);
        (this.headwear = new Cuboid(this, 32, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, scale + 0.5f);
        this.headwear.setRotationPoint(0.0f, 0.0f + float2, 0.0f);
        (this.body = new Cuboid(this, 16, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, scale);
        this.body.setRotationPoint(0.0f, 0.0f + float2, 0.0f);
        (this.rightArm = new Cuboid(this, 40, 16)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, scale);
        this.rightArm.setRotationPoint(-5.0f, 2.0f + float2, 0.0f);
        this.leftArm = new Cuboid(this, 40, 16);
        this.leftArm.mirror = true;
        this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, scale);
        this.leftArm.setRotationPoint(5.0f, 2.0f + float2, 0.0f);
        (this.rightLeg = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale);
        this.rightLeg.setRotationPoint(-1.9f, 12.0f + float2, 0.0f);
        this.leftLeg = new Cuboid(this, 0, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale);
        this.leftLeg.setRotationPoint(1.9f, 12.0f + float2, 0.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        GlStateManager.pushMatrix();
        if (this.isChild) {
            final float float8 = 2.0f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 16.0f * scale, 0.0f);
            this.head.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
            this.body.render(scale);
            this.rightArm.render(scale);
            this.leftArm.render(scale);
            this.rightLeg.render(scale);
            this.leftLeg.render(scale);
            this.headwear.render(scale);
        }
        else {
            if (entity.isInSneakingPose()) {
                GlStateManager.translatef(0.0f, 0.2f, 0.0f);
            }
            this.head.render(scale);
            this.body.render(scale);
            this.rightArm.render(scale);
            this.leftArm.render(scale);
            this.rightLeg.render(scale);
            this.leftLeg.render(scale);
            this.headwear.render(scale);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        this.p = entity.a(tickDelta);
        this.a = (float)entity.getItemUseTime();
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        final boolean boolean8 = entity.ds() > 4;
        final boolean boolean9 = entity.isInSwimmingPose();
        this.head.yaw = headYaw * 0.017453292f;
        if (boolean8) {
            this.head.pitch = -0.7853982f;
        }
        else if (this.p > 0.0f) {
            if (boolean9) {
                this.head.pitch = this.a(this.head.pitch, -0.7853982f, this.p);
            }
            else {
                this.head.pitch = this.a(this.head.pitch, headPitch * 0.017453292f, this.p);
            }
        }
        else {
            this.head.pitch = headPitch * 0.017453292f;
        }
        this.body.yaw = 0.0f;
        this.rightArm.rotationPointZ = 0.0f;
        this.rightArm.rotationPointX = -5.0f;
        this.leftArm.rotationPointZ = 0.0f;
        this.leftArm.rotationPointX = 5.0f;
        float float10 = 1.0f;
        if (boolean8) {
            float10 = (float)entity.getVelocity().lengthSquared();
            float10 /= 0.2f;
            float10 *= float10 * float10;
        }
        if (float10 < 1.0f) {
            float10 = 1.0f;
        }
        this.rightArm.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 2.0f * limbDistance * 0.5f / float10;
        this.leftArm.pitch = MathHelper.cos(limbAngle * 0.6662f) * 2.0f * limbDistance * 0.5f / float10;
        this.rightArm.roll = 0.0f;
        this.leftArm.roll = 0.0f;
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance / float10;
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance / float10;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
        this.rightLeg.roll = 0.0f;
        this.leftLeg.roll = 0.0f;
        if (this.isRiding) {
            final Cuboid rightArm = this.rightArm;
            rightArm.pitch -= 0.62831855f;
            final Cuboid leftArm = this.leftArm;
            leftArm.pitch -= 0.62831855f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = 0.31415927f;
            this.rightLeg.roll = 0.07853982f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = -0.31415927f;
            this.leftLeg.roll = -0.07853982f;
        }
        this.rightArm.yaw = 0.0f;
        this.rightArm.roll = 0.0f;
        switch (this.leftArmPose) {
            case a: {
                this.leftArm.yaw = 0.0f;
                break;
            }
            case c: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.9424779f;
                this.leftArm.yaw = 0.5235988f;
                break;
            }
            case b: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.31415927f;
                this.leftArm.yaw = 0.0f;
                break;
            }
        }
        switch (this.rightArmPose) {
            case a: {
                this.rightArm.yaw = 0.0f;
                break;
            }
            case c: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.9424779f;
                this.rightArm.yaw = -0.5235988f;
                break;
            }
            case b: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.31415927f;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case e: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 3.1415927f;
                this.rightArm.yaw = 0.0f;
                break;
            }
        }
        if (this.leftArmPose == ArmPose.e && this.rightArmPose != ArmPose.c && this.rightArmPose != ArmPose.e && this.rightArmPose != ArmPose.d) {
            this.leftArm.pitch = this.leftArm.pitch * 0.5f - 3.1415927f;
            this.leftArm.yaw = 0.0f;
        }
        if (this.handSwingProgress > 0.0f) {
            final AbsoluteHand absoluteHand11 = this.getPreferedHand(entity);
            final Cuboid cuboid12 = this.getArm(absoluteHand11);
            float float11 = this.handSwingProgress;
            this.body.yaw = MathHelper.sin(MathHelper.sqrt(float11) * 6.2831855f) * 0.2f;
            if (absoluteHand11 == AbsoluteHand.a) {
                final Cuboid body = this.body;
                body.yaw *= -1.0f;
            }
            this.rightArm.rotationPointZ = MathHelper.sin(this.body.yaw) * 5.0f;
            this.rightArm.rotationPointX = -MathHelper.cos(this.body.yaw) * 5.0f;
            this.leftArm.rotationPointZ = -MathHelper.sin(this.body.yaw) * 5.0f;
            this.leftArm.rotationPointX = MathHelper.cos(this.body.yaw) * 5.0f;
            final Cuboid rightArm2 = this.rightArm;
            rightArm2.yaw += this.body.yaw;
            final Cuboid leftArm2 = this.leftArm;
            leftArm2.yaw += this.body.yaw;
            final Cuboid leftArm3 = this.leftArm;
            leftArm3.pitch += this.body.yaw;
            float11 = 1.0f - this.handSwingProgress;
            float11 *= float11;
            float11 *= float11;
            float11 = 1.0f - float11;
            final float float12 = MathHelper.sin(float11 * 3.1415927f);
            final float float13 = MathHelper.sin(this.handSwingProgress * 3.1415927f) * -(this.head.pitch - 0.7f) * 0.75f;
            final Cuboid cuboid13 = cuboid12;
            cuboid13.pitch -= (float)(float12 * 1.2 + float13);
            final Cuboid cuboid14 = cuboid12;
            cuboid14.yaw += this.body.yaw * 2.0f;
            final Cuboid cuboid15 = cuboid12;
            cuboid15.roll += MathHelper.sin(this.handSwingProgress * 3.1415927f) * -0.4f;
        }
        if (this.isSneaking) {
            this.body.pitch = 0.5f;
            final Cuboid rightArm3 = this.rightArm;
            rightArm3.pitch += 0.4f;
            final Cuboid leftArm4 = this.leftArm;
            leftArm4.pitch += 0.4f;
            this.rightLeg.rotationPointZ = 4.0f;
            this.leftLeg.rotationPointZ = 4.0f;
            this.rightLeg.rotationPointY = 9.0f;
            this.leftLeg.rotationPointY = 9.0f;
            this.head.rotationPointY = 1.0f;
        }
        else {
            this.body.pitch = 0.0f;
            this.rightLeg.rotationPointZ = 0.1f;
            this.leftLeg.rotationPointZ = 0.1f;
            this.rightLeg.rotationPointY = 12.0f;
            this.leftLeg.rotationPointY = 12.0f;
            this.head.rotationPointY = 0.0f;
        }
        final Cuboid rightArm4 = this.rightArm;
        rightArm4.roll += MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
        final Cuboid leftArm5 = this.leftArm;
        leftArm5.roll -= MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
        final Cuboid rightArm5 = this.rightArm;
        rightArm5.pitch += MathHelper.sin(age * 0.067f) * 0.05f;
        final Cuboid leftArm6 = this.leftArm;
        leftArm6.pitch -= MathHelper.sin(age * 0.067f) * 0.05f;
        if (this.rightArmPose == ArmPose.d) {
            this.rightArm.yaw = -0.1f + this.head.yaw;
            this.leftArm.yaw = 0.1f + this.head.yaw + 0.4f;
            this.rightArm.pitch = -1.5707964f + this.head.pitch;
            this.leftArm.pitch = -1.5707964f + this.head.pitch;
        }
        else if (this.leftArmPose == ArmPose.d && this.rightArmPose != ArmPose.e && this.rightArmPose != ArmPose.c) {
            this.rightArm.yaw = -0.1f + this.head.yaw - 0.4f;
            this.leftArm.yaw = 0.1f + this.head.yaw;
            this.rightArm.pitch = -1.5707964f + this.head.pitch;
            this.leftArm.pitch = -1.5707964f + this.head.pitch;
        }
        final float float14 = (float)CrossbowItem.getPullTime(entity.getActiveItem());
        if (this.rightArmPose == ArmPose.f) {
            this.rightArm.yaw = -0.8f;
            this.rightArm.pitch = -0.97079635f;
            this.leftArm.pitch = -0.97079635f;
            final float float15 = MathHelper.clamp(this.a, 0.0f, float14);
            this.leftArm.yaw = MathHelper.lerp(float15 / float14, 0.4f, 0.85f);
            this.leftArm.pitch = MathHelper.lerp(float15 / float14, this.leftArm.pitch, -1.5707964f);
        }
        else if (this.leftArmPose == ArmPose.f) {
            this.leftArm.yaw = 0.8f;
            this.rightArm.pitch = -0.97079635f;
            this.leftArm.pitch = -0.97079635f;
            final float float15 = MathHelper.clamp(this.a, 0.0f, float14);
            this.rightArm.yaw = MathHelper.lerp(float15 / float14, -0.4f, -0.85f);
            this.rightArm.pitch = MathHelper.lerp(float15 / float14, this.rightArm.pitch, -1.5707964f);
        }
        if (this.rightArmPose == ArmPose.g && this.handSwingProgress <= 0.0f) {
            this.rightArm.yaw = -0.3f + this.head.yaw;
            this.leftArm.yaw = 0.6f + this.head.yaw;
            this.rightArm.pitch = -1.5707964f + this.head.pitch + 0.1f;
            this.leftArm.pitch = -1.5f + this.head.pitch;
        }
        else if (this.leftArmPose == ArmPose.g) {
            this.rightArm.yaw = -0.6f + this.head.yaw;
            this.leftArm.yaw = 0.3f + this.head.yaw;
            this.rightArm.pitch = -1.5f + this.head.pitch;
            this.leftArm.pitch = -1.5707964f + this.head.pitch + 0.1f;
        }
        if (this.p > 0.0f) {
            final float float15 = limbAngle % 26.0f;
            final float float11 = (this.handSwingProgress > 0.0f) ? 0.0f : this.p;
            if (float15 < 14.0f) {
                this.leftArm.pitch = this.a(this.leftArm.pitch, 0.0f, this.p);
                this.rightArm.pitch = MathHelper.lerp(float11, this.rightArm.pitch, 0.0f);
                this.leftArm.yaw = this.a(this.leftArm.yaw, 3.1415927f, this.p);
                this.rightArm.yaw = MathHelper.lerp(float11, this.rightArm.yaw, 3.1415927f);
                this.leftArm.roll = this.a(this.leftArm.roll, 3.1415927f + 1.8707964f * this.a(float15) / this.a(14.0f), this.p);
                this.rightArm.roll = MathHelper.lerp(float11, this.rightArm.roll, 3.1415927f - 1.8707964f * this.a(float15) / this.a(14.0f));
            }
            else if (float15 >= 14.0f && float15 < 22.0f) {
                final float float12 = (float15 - 14.0f) / 8.0f;
                this.leftArm.pitch = this.a(this.leftArm.pitch, 1.5707964f * float12, this.p);
                this.rightArm.pitch = MathHelper.lerp(float11, this.rightArm.pitch, 1.5707964f * float12);
                this.leftArm.yaw = this.a(this.leftArm.yaw, 3.1415927f, this.p);
                this.rightArm.yaw = MathHelper.lerp(float11, this.rightArm.yaw, 3.1415927f);
                this.leftArm.roll = this.a(this.leftArm.roll, 5.012389f - 1.8707964f * float12, this.p);
                this.rightArm.roll = MathHelper.lerp(float11, this.rightArm.roll, 1.2707963f + 1.8707964f * float12);
            }
            else if (float15 >= 22.0f && float15 < 26.0f) {
                final float float12 = (float15 - 22.0f) / 4.0f;
                this.leftArm.pitch = this.a(this.leftArm.pitch, 1.5707964f - 1.5707964f * float12, this.p);
                this.rightArm.pitch = MathHelper.lerp(float11, this.rightArm.pitch, 1.5707964f - 1.5707964f * float12);
                this.leftArm.yaw = this.a(this.leftArm.yaw, 3.1415927f, this.p);
                this.rightArm.yaw = MathHelper.lerp(float11, this.rightArm.yaw, 3.1415927f);
                this.leftArm.roll = this.a(this.leftArm.roll, 3.1415927f, this.p);
                this.rightArm.roll = MathHelper.lerp(float11, this.rightArm.roll, 3.1415927f);
            }
            final float float12 = 0.3f;
            final float float13 = 0.33333334f;
            this.leftLeg.pitch = MathHelper.lerp(this.p, this.leftLeg.pitch, 0.3f * MathHelper.cos(limbAngle * 0.33333334f + 3.1415927f));
            this.rightLeg.pitch = MathHelper.lerp(this.p, this.rightLeg.pitch, 0.3f * MathHelper.cos(limbAngle * 0.33333334f));
        }
        this.headwear.copyRotation(this.head);
    }
    
    protected float a(final float float1, final float float2, final float float3) {
        float float4 = (float2 - float1) % 6.2831855f;
        if (float4 < -3.1415927f) {
            float4 += 6.2831855f;
        }
        if (float4 >= 3.1415927f) {
            float4 -= 6.2831855f;
        }
        return float1 + float3 * float4;
    }
    
    private float a(final float float1) {
        return -65.0f * float1 + float1 * float1;
    }
    
    public void setAttributes(final BipedEntityModel<T> bipedEntityModel) {
        super.copyStateTo(bipedEntityModel);
        bipedEntityModel.leftArmPose = this.leftArmPose;
        bipedEntityModel.rightArmPose = this.rightArmPose;
        bipedEntityModel.isSneaking = this.isSneaking;
    }
    
    public void setVisible(final boolean visible) {
        this.head.visible = visible;
        this.headwear.visible = visible;
        this.body.visible = visible;
        this.rightArm.visible = visible;
        this.leftArm.visible = visible;
        this.rightLeg.visible = visible;
        this.leftLeg.visible = visible;
    }
    
    @Override
    public void setArmAngle(final float float1, final AbsoluteHand absoluteHand) {
        this.getArm(absoluteHand).applyTransform(float1);
    }
    
    protected Cuboid getArm(final AbsoluteHand absoluteHand) {
        if (absoluteHand == AbsoluteHand.a) {
            return this.leftArm;
        }
        return this.rightArm;
    }
    
    @Override
    public Cuboid getHead() {
        return this.head;
    }
    
    protected AbsoluteHand getPreferedHand(final T livingEntity) {
        final AbsoluteHand absoluteHand2 = livingEntity.getMainHand();
        return (livingEntity.preferredHand == Hand.a) ? absoluteHand2 : absoluteHand2.getOpposite();
    }
    
    @Environment(EnvType.CLIENT)
    public enum ArmPose
    {
        a, 
        b, 
        c, 
        d, 
        e, 
        f, 
        g;
    }
}
