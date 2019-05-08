package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.AbsoluteHand;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityModel extends ArmorStandArmorEntityModel
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid t;
    private final Cuboid u;
    
    public ArmorStandEntityModel() {
        this(0.0f);
    }
    
    public ArmorStandEntityModel(final float float1) {
        super(float1, 64, 64);
        (this.head = new Cuboid(this, 0, 0)).addBox(-1.0f, -7.0f, -1.0f, 2, 7, 2, float1);
        this.head.setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.body = new Cuboid(this, 0, 26)).addBox(-6.0f, 0.0f, -1.5f, 12, 3, 3, float1);
        this.body.setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.rightArm = new Cuboid(this, 24, 0)).addBox(-2.0f, -2.0f, -1.0f, 2, 12, 2, float1);
        this.rightArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
        this.leftArm = new Cuboid(this, 32, 16);
        this.leftArm.mirror = true;
        this.leftArm.addBox(0.0f, -2.0f, -1.0f, 2, 12, 2, float1);
        this.leftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
        (this.rightLeg = new Cuboid(this, 8, 0)).addBox(-1.0f, 0.0f, -1.0f, 2, 11, 2, float1);
        this.rightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.leftLeg = new Cuboid(this, 40, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addBox(-1.0f, 0.0f, -1.0f, 2, 11, 2, float1);
        this.leftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        (this.a = new Cuboid(this, 16, 0)).addBox(-3.0f, 3.0f, -1.0f, 2, 7, 2, float1);
        this.a.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.a.visible = true;
        (this.b = new Cuboid(this, 48, 16)).addBox(1.0f, 3.0f, -1.0f, 2, 7, 2, float1);
        this.b.setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.t = new Cuboid(this, 0, 48)).addBox(-4.0f, 10.0f, -1.0f, 8, 2, 2, float1);
        this.t.setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.u = new Cuboid(this, 0, 32)).addBox(-6.0f, 11.0f, -6.0f, 12, 1, 12, float1);
        this.u.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.headwear.visible = false;
    }
    
    @Override
    public void setAngles(final ArmorStandEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.leftArm.visible = entity.shouldShowArms();
        this.rightArm.visible = entity.shouldShowArms();
        this.u.visible = !entity.shouldHideBasePlate();
        this.leftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.rightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.a.pitch = 0.017453292f * entity.getBodyRotation().getX();
        this.a.yaw = 0.017453292f * entity.getBodyRotation().getY();
        this.a.roll = 0.017453292f * entity.getBodyRotation().getZ();
        this.b.pitch = 0.017453292f * entity.getBodyRotation().getX();
        this.b.yaw = 0.017453292f * entity.getBodyRotation().getY();
        this.b.roll = 0.017453292f * entity.getBodyRotation().getZ();
        this.t.pitch = 0.017453292f * entity.getBodyRotation().getX();
        this.t.yaw = 0.017453292f * entity.getBodyRotation().getY();
        this.t.roll = 0.017453292f * entity.getBodyRotation().getZ();
        this.u.pitch = 0.0f;
        this.u.yaw = 0.017453292f * -entity.yaw;
        this.u.roll = 0.0f;
    }
    
    @Override
    public void render(final ArmorStandEntity entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        GlStateManager.pushMatrix();
        if (this.isChild) {
            final float float8 = 2.0f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
            this.a.render(scale);
            this.b.render(scale);
            this.t.render(scale);
            this.u.render(scale);
        }
        else {
            if (entity.isSneaking()) {
                GlStateManager.translatef(0.0f, 0.2f, 0.0f);
            }
            this.a.render(scale);
            this.b.render(scale);
            this.t.render(scale);
            this.u.render(scale);
        }
        GlStateManager.popMatrix();
    }
    
    @Override
    public void setArmAngle(final float float1, final AbsoluteHand absoluteHand) {
        final Cuboid cuboid3 = this.getArm(absoluteHand);
        final boolean boolean4 = cuboid3.visible;
        cuboid3.visible = true;
        super.setArmAngle(float1, absoluteHand);
        cuboid3.visible = boolean4;
    }
}
