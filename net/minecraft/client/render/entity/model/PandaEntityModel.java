package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.PandaEntity;

@Environment(EnvType.CLIENT)
public class PandaEntityModel<T extends PandaEntity> extends QuadrupedEntityModel<T>
{
    private float scaredAnimationProgress;
    private float lieOnBackAnimationProgress;
    private float playAnimationProgress;
    
    public PandaEntityModel(final int integer, final float float2) {
        super(integer, float2);
        this.textureWidth = 64;
        this.textureHeight = 64;
        (this.head = new Cuboid(this, 0, 6)).addBox(-6.5f, -5.0f, -4.0f, 13, 10, 9);
        this.head.setRotationPoint(0.0f, 11.5f, -17.0f);
        this.head.setTextureOffset(45, 16).addBox(-3.5f, 0.0f, -6.0f, 7, 5, 2);
        this.head.setTextureOffset(52, 25).addBox(-8.5f, -8.0f, -1.0f, 5, 4, 1);
        this.head.setTextureOffset(52, 25).addBox(3.5f, -8.0f, -1.0f, 5, 4, 1);
        (this.body = new Cuboid(this, 0, 25)).addBox(-9.5f, -13.0f, -6.5f, 19, 26, 13);
        this.body.setRotationPoint(0.0f, 10.0f, 0.0f);
        final int integer2 = 9;
        final int integer3 = 6;
        (this.leg1 = new Cuboid(this, 40, 0)).addBox(-3.0f, 0.0f, -3.0f, 6, 9, 6);
        this.leg1.setRotationPoint(-5.5f, 15.0f, 9.0f);
        (this.leg2 = new Cuboid(this, 40, 0)).addBox(-3.0f, 0.0f, -3.0f, 6, 9, 6);
        this.leg2.setRotationPoint(5.5f, 15.0f, 9.0f);
        (this.leg3 = new Cuboid(this, 40, 0)).addBox(-3.0f, 0.0f, -3.0f, 6, 9, 6);
        this.leg3.setRotationPoint(-5.5f, 15.0f, -9.0f);
        (this.leg4 = new Cuboid(this, 40, 0)).addBox(-3.0f, 0.0f, -3.0f, 6, 9, 6);
        this.leg4.setRotationPoint(5.5f, 15.0f, -9.0f);
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
        this.scaredAnimationProgress = entity.getScaredAnimationProgress(tickDelta);
        this.lieOnBackAnimationProgress = entity.getLieOnBackAnimationProgress(tickDelta);
        this.playAnimationProgress = (entity.isChild() ? 0.0f : entity.getRollOverAnimationProgress(tickDelta));
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        final boolean boolean8 = entity.getAskForBambooTicks() > 0;
        final boolean boolean9 = entity.isSneezing();
        final int integer10 = entity.getSneezeProgress();
        final boolean boolean10 = entity.isEating();
        final boolean boolean11 = entity.eo();
        if (boolean8) {
            this.head.yaw = 0.35f * MathHelper.sin(0.6f * age);
            this.head.roll = 0.35f * MathHelper.sin(0.6f * age);
            this.leg3.pitch = -0.75f * MathHelper.sin(0.3f * age);
            this.leg4.pitch = 0.75f * MathHelper.sin(0.3f * age);
        }
        else {
            this.head.roll = 0.0f;
        }
        if (boolean9) {
            if (integer10 < 15) {
                this.head.pitch = -0.7853982f * integer10 / 14.0f;
            }
            else if (integer10 < 20) {
                final float float13 = (float)((integer10 - 15) / 5);
                this.head.pitch = -0.7853982f + 0.7853982f * float13;
            }
        }
        if (this.scaredAnimationProgress > 0.0f) {
            this.body.pitch = this.interpolateAngle(this.body.pitch, 1.7407963f, this.scaredAnimationProgress);
            this.head.pitch = this.interpolateAngle(this.head.pitch, 1.5707964f, this.scaredAnimationProgress);
            this.leg3.roll = -0.27079642f;
            this.leg4.roll = 0.27079642f;
            this.leg1.roll = 0.5707964f;
            this.leg2.roll = -0.5707964f;
            if (boolean10) {
                this.head.pitch = 1.5707964f + 0.2f * MathHelper.sin(age * 0.6f);
                this.leg3.pitch = -0.4f - 0.2f * MathHelper.sin(age * 0.6f);
                this.leg4.pitch = -0.4f - 0.2f * MathHelper.sin(age * 0.6f);
            }
            if (boolean11) {
                this.head.pitch = 2.1707964f;
                this.leg3.pitch = -0.9f;
                this.leg4.pitch = -0.9f;
            }
        }
        else {
            this.leg1.roll = 0.0f;
            this.leg2.roll = 0.0f;
            this.leg3.roll = 0.0f;
            this.leg4.roll = 0.0f;
        }
        if (this.lieOnBackAnimationProgress > 0.0f) {
            this.leg1.pitch = -0.6f * MathHelper.sin(age * 0.15f);
            this.leg2.pitch = 0.6f * MathHelper.sin(age * 0.15f);
            this.leg3.pitch = 0.3f * MathHelper.sin(age * 0.25f);
            this.leg4.pitch = -0.3f * MathHelper.sin(age * 0.25f);
            this.head.pitch = this.interpolateAngle(this.head.pitch, 1.5707964f, this.lieOnBackAnimationProgress);
        }
        if (this.playAnimationProgress > 0.0f) {
            this.head.pitch = this.interpolateAngle(this.head.pitch, 2.0561945f, this.playAnimationProgress);
            this.leg1.pitch = -0.5f * MathHelper.sin(age * 0.5f);
            this.leg2.pitch = 0.5f * MathHelper.sin(age * 0.5f);
            this.leg3.pitch = 0.5f * MathHelper.sin(age * 0.5f);
            this.leg4.pitch = -0.5f * MathHelper.sin(age * 0.5f);
        }
    }
    
    protected float interpolateAngle(final float angle1, final float angle2, final float progress) {
        float float4;
        for (float4 = angle2 - angle1; float4 < -3.1415927f; float4 += 6.2831855f) {}
        while (float4 >= 3.1415927f) {
            float4 -= 6.2831855f;
        }
        return angle1 + progress * float4;
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            final float float8 = 3.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, this.j * scale, this.k * scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            final float float9 = 0.6f;
            GlStateManager.scalef(0.5555555f, 0.5555555f, 0.5555555f);
            GlStateManager.translatef(0.0f, 23.0f * scale, 0.3f);
            this.head.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.33333334f, 0.33333334f, 0.33333334f);
            GlStateManager.translatef(0.0f, 49.0f * scale, 0.0f);
            this.body.render(scale);
            this.leg1.render(scale);
            this.leg2.render(scale);
            this.leg3.render(scale);
            this.leg4.render(scale);
            GlStateManager.popMatrix();
        }
        else {
            this.head.render(scale);
            this.body.render(scale);
            this.leg1.render(scale);
            this.leg2.render(scale);
            this.leg3.render(scale);
            this.leg4.render(scale);
        }
    }
}
