package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity> extends EntityModel<T> implements ModelWithHead, ModelWithHat
{
    protected final Cuboid head;
    protected Cuboid headOverlay;
    protected final Cuboid hat;
    protected final Cuboid body;
    protected final Cuboid robe;
    protected final Cuboid arms;
    protected final Cuboid leftLeg;
    protected final Cuboid rightLeg;
    protected final Cuboid nose;
    
    public VillagerResemblingModel(final float scale) {
        this(scale, 64, 64);
    }
    
    public VillagerResemblingModel(final float scale, final int textureWidth, final int textureHeight) {
        final float float4 = 0.5f;
        (this.head = new Cuboid(this).setTextureSize(textureWidth, textureHeight)).setRotationPoint(0.0f, 0.0f, 0.0f);
        this.head.setTextureOffset(0, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, scale);
        (this.headOverlay = new Cuboid(this).setTextureSize(textureWidth, textureHeight)).setRotationPoint(0.0f, 0.0f, 0.0f);
        this.headOverlay.setTextureOffset(32, 0).addBox(-4.0f, -10.0f, -4.0f, 8, 10, 8, scale + 0.5f);
        this.head.addChild(this.headOverlay);
        (this.hat = new Cuboid(this).setTextureSize(textureWidth, textureHeight)).setRotationPoint(0.0f, 0.0f, 0.0f);
        this.hat.setTextureOffset(30, 47).addBox(-8.0f, -8.0f, -6.0f, 16, 16, 1, scale);
        this.hat.pitch = -1.5707964f;
        this.headOverlay.addChild(this.hat);
        (this.nose = new Cuboid(this).setTextureSize(textureWidth, textureHeight)).setRotationPoint(0.0f, -2.0f, 0.0f);
        this.nose.setTextureOffset(24, 0).addBox(-1.0f, -1.0f, -6.0f, 2, 4, 2, scale);
        this.head.addChild(this.nose);
        (this.body = new Cuboid(this).setTextureSize(textureWidth, textureHeight)).setRotationPoint(0.0f, 0.0f, 0.0f);
        this.body.setTextureOffset(16, 20).addBox(-4.0f, 0.0f, -3.0f, 8, 12, 6, scale);
        (this.robe = new Cuboid(this).setTextureSize(textureWidth, textureHeight)).setRotationPoint(0.0f, 0.0f, 0.0f);
        this.robe.setTextureOffset(0, 38).addBox(-4.0f, 0.0f, -3.0f, 8, 18, 6, scale + 0.5f);
        this.body.addChild(this.robe);
        (this.arms = new Cuboid(this).setTextureSize(textureWidth, textureHeight)).setRotationPoint(0.0f, 2.0f, 0.0f);
        this.arms.setTextureOffset(44, 22).addBox(-8.0f, -2.0f, -2.0f, 4, 8, 4, scale);
        this.arms.setTextureOffset(44, 22).addBox(4.0f, -2.0f, -2.0f, 4, 8, 4, scale, true);
        this.arms.setTextureOffset(40, 38).addBox(-4.0f, 2.0f, -2.0f, 8, 4, 4, scale);
        (this.leftLeg = new Cuboid(this, 0, 22).setTextureSize(textureWidth, textureHeight)).setRotationPoint(-2.0f, 12.0f, 0.0f);
        this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale);
        this.rightLeg = new Cuboid(this, 0, 22).setTextureSize(textureWidth, textureHeight);
        this.rightLeg.mirror = true;
        this.rightLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
        this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, scale);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.head.render(scale);
        this.body.render(scale);
        this.leftLeg.render(scale);
        this.rightLeg.render(scale);
        this.arms.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        boolean boolean8 = false;
        if (entity instanceof AbstractTraderEntity) {
            boolean8 = (((AbstractTraderEntity)entity).getHeadRollingTimeLeft() > 0);
        }
        this.head.yaw = headYaw * 0.017453292f;
        this.head.pitch = headPitch * 0.017453292f;
        if (boolean8) {
            this.head.roll = 0.3f * MathHelper.sin(0.45f * age);
            this.head.pitch = 0.4f;
        }
        else {
            this.head.roll = 0.0f;
        }
        this.arms.rotationPointY = 3.0f;
        this.arms.rotationPointZ = -1.0f;
        this.arms.pitch = -0.75f;
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance * 0.5f;
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance * 0.5f;
        this.leftLeg.yaw = 0.0f;
        this.rightLeg.yaw = 0.0f;
    }
    
    @Override
    public Cuboid getHead() {
        return this.head;
    }
    
    @Override
    public void setHatVisible(final boolean visible) {
        this.head.visible = visible;
        this.headOverlay.visible = visible;
        this.hat.visible = visible;
    }
}
