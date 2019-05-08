package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityModel<T extends ZombieEntity> extends BipedEntityModel<T> implements ModelWithHat
{
    private Cuboid hat;
    
    public ZombieVillagerEntityModel() {
        this(0.0f, false);
    }
    
    public ZombieVillagerEntityModel(final float float1, final boolean boolean2) {
        super(float1, 0.0f, 64, boolean2 ? 32 : 64);
        if (boolean2) {
            (this.head = new Cuboid(this, 0, 0)).addBox(-4.0f, -10.0f, -4.0f, 8, 8, 8, float1);
            (this.body = new Cuboid(this, 16, 16)).addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, float1 + 0.1f);
            (this.rightLeg = new Cuboid(this, 0, 16)).setRotationPoint(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1 + 0.1f);
            this.leftLeg = new Cuboid(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
            this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1 + 0.1f);
        }
        else {
            this.head = new Cuboid(this, 0, 0);
            this.head.setTextureOffset(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, float1);
            this.head.setTextureOffset(24, 0).addBox(-1.0f, -3.0f, -6.0f, 2, 4, 2, float1);
            (this.headwear = new Cuboid(this, 32, 0)).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, float1 + 0.5f);
            this.hat = new Cuboid(this);
            this.hat.setTextureOffset(30, 47).addBox(-8.0f, -8.0f, -6.0f, 16, 16, 1, float1);
            this.hat.pitch = -1.5707964f;
            this.headwear.addChild(this.hat);
            (this.body = new Cuboid(this, 16, 20)).addBox(-4.0f, 0.0f, -3.0f, 8, 12, 6, float1);
            this.body.setTextureOffset(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8, 18, 6, float1 + 0.05f);
            (this.rightArm = new Cuboid(this, 44, 22)).addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, float1);
            this.rightArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
            this.leftArm = new Cuboid(this, 44, 22);
            this.leftArm.mirror = true;
            this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, float1);
            this.leftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
            (this.rightLeg = new Cuboid(this, 0, 22)).setRotationPoint(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
            this.leftLeg = new Cuboid(this, 0, 22);
            this.leftLeg.mirror = true;
            this.leftLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
            this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, float1);
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        final float float8 = MathHelper.sin(this.handSwingProgress * 3.1415927f);
        final float float9 = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * 3.1415927f);
        this.rightArm.roll = 0.0f;
        this.leftArm.roll = 0.0f;
        this.rightArm.yaw = -(0.1f - float8 * 0.6f);
        this.leftArm.yaw = 0.1f - float8 * 0.6f;
        final float float10 = -3.1415927f / (entity.isAttacking() ? 1.5f : 2.25f);
        this.rightArm.pitch = float10;
        this.leftArm.pitch = float10;
        final Cuboid rightArm = this.rightArm;
        rightArm.pitch += float8 * 1.2f - float9 * 0.4f;
        final Cuboid leftArm = this.leftArm;
        leftArm.pitch += float8 * 1.2f - float9 * 0.4f;
        final Cuboid rightArm2 = this.rightArm;
        rightArm2.roll += MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
        final Cuboid leftArm2 = this.leftArm;
        leftArm2.roll -= MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
        final Cuboid rightArm3 = this.rightArm;
        rightArm3.pitch += MathHelper.sin(age * 0.067f) * 0.05f;
        final Cuboid leftArm3 = this.leftArm;
        leftArm3.pitch -= MathHelper.sin(age * 0.067f) * 0.05f;
    }
    
    @Override
    public void setHatVisible(final boolean visible) {
        this.head.visible = visible;
        this.headwear.visible = visible;
        this.hat.visible = visible;
    }
}
