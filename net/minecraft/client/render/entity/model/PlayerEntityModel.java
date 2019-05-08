package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.AbsoluteHand;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity> extends BipedEntityModel<T>
{
    public final Cuboid leftArmOverlay;
    public final Cuboid rightArmOverlay;
    public final Cuboid leftLegOverlay;
    public final Cuboid rightLegOverlay;
    public final Cuboid bodyOverlay;
    private final Cuboid cape;
    private final Cuboid ears;
    private final boolean thinArms;
    
    public PlayerEntityModel(final float scale, final boolean thinArms) {
        super(scale, 0.0f, 64, 64);
        this.thinArms = thinArms;
        (this.ears = new Cuboid(this, 24, 0)).addBox(-3.0f, -6.0f, -1.0f, 6, 6, 1, scale);
        (this.cape = new Cuboid(this, 0, 0)).setTextureSize(64, 32);
        this.cape.addBox(-5.0f, 0.0f, -1.0f, 10, 16, 1, scale);
        if (thinArms) {
            (this.leftArm = new Cuboid(this, 32, 48)).addBox(-1.0f, -2.0f, -2.0f, 3, 12, 4, scale);
            this.leftArm.setRotationPoint(5.0f, 2.5f, 0.0f);
            (this.rightArm = new Cuboid(this, 40, 16)).addBox(-2.0f, -2.0f, -2.0f, 3, 12, 4, scale);
            this.rightArm.setRotationPoint(-5.0f, 2.5f, 0.0f);
            (this.leftArmOverlay = new Cuboid(this, 48, 48)).addBox(-1.0f, -2.0f, -2.0f, 3, 12, 4, scale + 0.25f);
            this.leftArmOverlay.setRotationPoint(5.0f, 2.5f, 0.0f);
            (this.rightArmOverlay = new Cuboid(this, 40, 32)).addBox(-2.0f, -2.0f, -2.0f, 3, 12, 4, scale + 0.25f);
            this.rightArmOverlay.setRotationPoint(-5.0f, 2.5f, 10.0f);
        }
        else {
            (this.leftArm = new Cuboid(this, 32, 48)).addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, scale);
            this.leftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
            (this.leftArmOverlay = new Cuboid(this, 48, 48)).addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, scale + 0.25f);
            this.leftArmOverlay.setRotationPoint(5.0f, 2.0f, 0.0f);
            (this.rightArmOverlay = new Cuboid(this, 40, 32)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, scale + 0.25f);
            this.rightArmOverlay.setRotationPoint(-5.0f, 2.0f, 10.0f);
        }
        (this.leftLeg = new Cuboid(this, 16, 48)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale);
        this.leftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        (this.leftLegOverlay = new Cuboid(this, 0, 48)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale + 0.25f);
        this.leftLegOverlay.setRotationPoint(1.9f, 12.0f, 0.0f);
        (this.rightLegOverlay = new Cuboid(this, 0, 32)).addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale + 0.25f);
        this.rightLegOverlay.setRotationPoint(-1.9f, 12.0f, 0.0f);
        (this.bodyOverlay = new Cuboid(this, 16, 32)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, scale + 0.25f);
        this.bodyOverlay.setRotationPoint(0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        GlStateManager.pushMatrix();
        if (this.isChild) {
            final float float8 = 2.0f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
            this.leftLegOverlay.render(scale);
            this.rightLegOverlay.render(scale);
            this.leftArmOverlay.render(scale);
            this.rightArmOverlay.render(scale);
            this.bodyOverlay.render(scale);
        }
        else {
            if (entity.isInSneakingPose()) {
                GlStateManager.translatef(0.0f, 0.2f, 0.0f);
            }
            this.leftLegOverlay.render(scale);
            this.rightLegOverlay.render(scale);
            this.leftArmOverlay.render(scale);
            this.rightArmOverlay.render(scale);
            this.bodyOverlay.render(scale);
        }
        GlStateManager.popMatrix();
    }
    
    public void renderEars(final float scale) {
        this.ears.copyRotation(this.head);
        this.ears.rotationPointX = 0.0f;
        this.ears.rotationPointY = 0.0f;
        this.ears.render(scale);
    }
    
    public void renderCape(final float scale) {
        this.cape.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.leftLegOverlay.copyRotation(this.leftLeg);
        this.rightLegOverlay.copyRotation(this.rightLeg);
        this.leftArmOverlay.copyRotation(this.leftArm);
        this.rightArmOverlay.copyRotation(this.rightArm);
        this.bodyOverlay.copyRotation(this.body);
        if (entity.isInSneakingPose()) {
            this.cape.rotationPointY = 2.0f;
        }
        else {
            this.cape.rotationPointY = 0.0f;
        }
    }
    
    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
        this.leftArmOverlay.visible = visible;
        this.rightArmOverlay.visible = visible;
        this.leftLegOverlay.visible = visible;
        this.rightLegOverlay.visible = visible;
        this.bodyOverlay.visible = visible;
        this.cape.visible = visible;
        this.ears.visible = visible;
    }
    
    @Override
    public void setArmAngle(final float float1, final AbsoluteHand absoluteHand) {
        final Cuboid cuboid3 = this.getArm(absoluteHand);
        if (this.thinArms) {
            final float float2 = 0.5f * ((absoluteHand == AbsoluteHand.b) ? 1 : -1);
            final Cuboid cuboid4 = cuboid3;
            cuboid4.rotationPointX += float2;
            cuboid3.applyTransform(float1);
            final Cuboid cuboid5 = cuboid3;
            cuboid5.rotationPointX -= float2;
        }
        else {
            cuboid3.applyTransform(float1);
        }
    }
}
