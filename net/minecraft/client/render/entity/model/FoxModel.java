package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.FoxEntity;

@Environment(EnvType.CLIENT)
public class FoxModel<T extends FoxEntity> extends EntityModel<T>
{
    public final Cuboid head;
    private final Cuboid leftEar;
    private final Cuboid rightEar;
    private final Cuboid nose;
    private final Cuboid body;
    private final Cuboid frontLeftLeg;
    private final Cuboid frontRightLeg;
    private final Cuboid rearLeftLeg;
    private final Cuboid rearRightLeg;
    private final Cuboid tail;
    private float n;
    
    public FoxModel() {
        this.textureWidth = 48;
        this.textureHeight = 32;
        (this.head = new Cuboid(this, 1, 5)).addBox(-3.0f, -2.0f, -5.0f, 8, 6, 6);
        this.head.setRotationPoint(-1.0f, 16.5f, -3.0f);
        (this.leftEar = new Cuboid(this, 8, 1)).addBox(-3.0f, -4.0f, -4.0f, 2, 2, 1);
        (this.rightEar = new Cuboid(this, 15, 1)).addBox(3.0f, -4.0f, -4.0f, 2, 2, 1);
        (this.nose = new Cuboid(this, 6, 18)).addBox(-1.0f, 2.01f, -8.0f, 4, 2, 3);
        this.head.addChild(this.leftEar);
        this.head.addChild(this.rightEar);
        this.head.addChild(this.nose);
        (this.body = new Cuboid(this, 24, 15)).addBox(-3.0f, 3.999f, -3.5f, 6, 11, 6);
        this.body.setRotationPoint(0.0f, 16.0f, -6.0f);
        final float float1 = 0.001f;
        (this.frontLeftLeg = new Cuboid(this, 13, 24)).addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.frontLeftLeg.setRotationPoint(-5.0f, 17.5f, 7.0f);
        (this.frontRightLeg = new Cuboid(this, 4, 24)).addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.frontRightLeg.setRotationPoint(-1.0f, 17.5f, 7.0f);
        (this.rearLeftLeg = new Cuboid(this, 13, 24)).addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.rearLeftLeg.setRotationPoint(-5.0f, 17.5f, 0.0f);
        (this.rearRightLeg = new Cuboid(this, 4, 24)).addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.rearRightLeg.setRotationPoint(-1.0f, 17.5f, 0.0f);
        (this.tail = new Cuboid(this, 30, 0)).addBox(2.0f, 0.0f, -1.0f, 4, 9, 5);
        this.tail.setRotationPoint(-4.0f, 15.0f, -1.0f);
        this.body.addChild(this.tail);
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        this.body.pitch = 1.5707964f;
        this.tail.pitch = -0.05235988f;
        this.frontLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.frontRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
        this.rearLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
        this.rearRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.head.setRotationPoint(-1.0f, 16.5f, -3.0f);
        this.head.yaw = 0.0f;
        this.head.roll = entity.getHeadRoll(tickDelta);
        this.frontLeftLeg.visible = true;
        this.frontRightLeg.visible = true;
        this.rearLeftLeg.visible = true;
        this.rearRightLeg.visible = true;
        this.body.setRotationPoint(0.0f, 16.0f, -6.0f);
        this.body.roll = 0.0f;
        this.frontLeftLeg.setRotationPoint(-5.0f, 17.5f, 7.0f);
        this.frontRightLeg.setRotationPoint(-1.0f, 17.5f, 7.0f);
        if (entity.isCrouching()) {
            this.body.pitch = 1.6755161f;
            final float float5 = entity.getBodyRotationHeightOffset(tickDelta);
            this.body.setRotationPoint(0.0f, 16.0f + entity.getBodyRotationHeightOffset(tickDelta), -6.0f);
            this.head.setRotationPoint(-1.0f, 16.5f + float5, -3.0f);
            this.head.yaw = 0.0f;
        }
        else if (entity.isSleeping()) {
            this.body.roll = -1.5707964f;
            this.body.setRotationPoint(0.0f, 21.0f, -6.0f);
            this.tail.pitch = -2.6179938f;
            if (this.isChild) {
                this.tail.pitch = -2.1816616f;
                this.body.setRotationPoint(0.0f, 21.0f, -2.0f);
            }
            this.head.setRotationPoint(1.0f, 19.49f, -3.0f);
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = 0.0f;
            this.frontLeftLeg.visible = false;
            this.frontRightLeg.visible = false;
            this.rearLeftLeg.visible = false;
            this.rearRightLeg.visible = false;
        }
        else if (entity.isSitting()) {
            this.body.pitch = 0.5235988f;
            this.body.setRotationPoint(0.0f, 9.0f, -3.0f);
            this.tail.pitch = 0.7853982f;
            this.tail.setRotationPoint(-4.0f, 15.0f, -2.0f);
            this.head.setRotationPoint(-1.0f, 10.0f, -0.25f);
            this.head.pitch = 0.0f;
            this.head.yaw = 0.0f;
            if (this.isChild) {
                this.head.setRotationPoint(-1.0f, 13.0f, -3.75f);
            }
            this.frontLeftLeg.pitch = -1.3089969f;
            this.frontLeftLeg.setRotationPoint(-5.0f, 21.5f, 6.75f);
            this.frontRightLeg.pitch = -1.3089969f;
            this.frontRightLeg.setRotationPoint(-1.0f, 21.5f, 6.75f);
            this.rearLeftLeg.pitch = -0.2617994f;
            this.rearRightLeg.pitch = -0.2617994f;
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            GlStateManager.pushMatrix();
            final float float8 = 0.75f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 8.0f * scale, 3.35f * scale);
            this.head.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            final float float9 = 0.5f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
            this.body.render(scale);
            this.frontLeftLeg.render(scale);
            this.frontRightLeg.render(scale);
            this.rearLeftLeg.render(scale);
            this.rearRightLeg.render(scale);
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.pushMatrix();
            this.head.render(scale);
            this.body.render(scale);
            this.frontLeftLeg.render(scale);
            this.frontRightLeg.render(scale);
            this.rearLeftLeg.render(scale);
            this.rearRightLeg.render(scale);
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (!entity.isSleeping() && !entity.isWalking() && !entity.isCrouching()) {
            this.head.pitch = headPitch * 0.017453292f;
            this.head.yaw = headYaw * 0.017453292f;
        }
        if (entity.isSleeping()) {
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = MathHelper.cos(age * 0.027f) / 22.0f;
        }
        if (entity.isCrouching()) {
            final float float8 = MathHelper.cos(age) * 0.01f;
            this.body.yaw = float8;
            this.frontLeftLeg.roll = float8;
            this.frontRightLeg.roll = float8;
            this.rearLeftLeg.roll = float8 / 2.0f;
            this.rearRightLeg.roll = float8 / 2.0f;
        }
        if (entity.isWalking()) {
            final float float8 = 0.1f;
            this.n += 0.67f;
            this.frontLeftLeg.pitch = MathHelper.cos(this.n * 0.4662f) * 0.1f;
            this.frontRightLeg.pitch = MathHelper.cos(this.n * 0.4662f + 3.1415927f) * 0.1f;
            this.rearLeftLeg.pitch = MathHelper.cos(this.n * 0.4662f + 3.1415927f) * 0.1f;
            this.rearRightLeg.pitch = MathHelper.cos(this.n * 0.4662f) * 0.1f;
        }
    }
}
