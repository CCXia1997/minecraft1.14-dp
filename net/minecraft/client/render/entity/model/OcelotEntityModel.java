package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity> extends EntityModel<T>
{
    protected final Cuboid frontLegLeft;
    protected final Cuboid frontLegRight;
    protected final Cuboid backLegLeft;
    protected final Cuboid backLegRight;
    protected final Cuboid tail1;
    protected final Cuboid tail2;
    protected final Cuboid head;
    protected final Cuboid body;
    protected int animationState;
    
    public OcelotEntityModel(final float float1) {
        this.animationState = 1;
        (this.head = new Cuboid(this, "head")).addBox("main", -2.5f, -2.0f, -3.0f, 5, 4, 5, float1, 0, 0);
        this.head.addBox("nose", -1.5f, 0.0f, -4.0f, 3, 2, 2, float1, 0, 24);
        this.head.addBox("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, float1, 0, 10);
        this.head.addBox("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, float1, 6, 10);
        this.head.setRotationPoint(0.0f, 15.0f, -9.0f);
        (this.body = new Cuboid(this, 20, 0)).addBox(-2.0f, 3.0f, -8.0f, 4, 16, 6, float1);
        this.body.setRotationPoint(0.0f, 12.0f, -10.0f);
        (this.tail1 = new Cuboid(this, 0, 15)).addBox(-0.5f, 0.0f, 0.0f, 1, 8, 1, float1);
        this.tail1.pitch = 0.9f;
        this.tail1.setRotationPoint(0.0f, 15.0f, 8.0f);
        (this.tail2 = new Cuboid(this, 4, 15)).addBox(-0.5f, 0.0f, 0.0f, 1, 8, 1, float1);
        this.tail2.setRotationPoint(0.0f, 20.0f, 14.0f);
        (this.frontLegLeft = new Cuboid(this, 8, 13)).addBox(-1.0f, 0.0f, 1.0f, 2, 6, 2, float1);
        this.frontLegLeft.setRotationPoint(1.1f, 18.0f, 5.0f);
        (this.frontLegRight = new Cuboid(this, 8, 13)).addBox(-1.0f, 0.0f, 1.0f, 2, 6, 2, float1);
        this.frontLegRight.setRotationPoint(-1.1f, 18.0f, 5.0f);
        (this.backLegLeft = new Cuboid(this, 40, 0)).addBox(-1.0f, 0.0f, 0.0f, 2, 10, 2, float1);
        this.backLegLeft.setRotationPoint(1.2f, 13.8f, -5.0f);
        (this.backLegRight = new Cuboid(this, 40, 0)).addBox(-1.0f, 0.0f, 0.0f, 2, 10, 2, float1);
        this.backLegRight.setRotationPoint(-1.2f, 13.8f, -5.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            final float float8 = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 10.0f * scale, 4.0f * scale);
            this.head.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
            this.body.render(scale);
            this.frontLegLeft.render(scale);
            this.frontLegRight.render(scale);
            this.backLegLeft.render(scale);
            this.backLegRight.render(scale);
            this.tail1.render(scale);
            this.tail2.render(scale);
            GlStateManager.popMatrix();
        }
        else {
            this.head.render(scale);
            this.body.render(scale);
            this.tail1.render(scale);
            this.tail2.render(scale);
            this.frontLegLeft.render(scale);
            this.frontLegRight.render(scale);
            this.backLegLeft.render(scale);
            this.backLegRight.render(scale);
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.head.pitch = headPitch * 0.017453292f;
        this.head.yaw = headYaw * 0.017453292f;
        if (this.animationState != 3) {
            this.body.pitch = 1.5707964f;
            if (this.animationState == 2) {
                this.frontLegLeft.pitch = MathHelper.cos(limbAngle * 0.6662f) * limbDistance;
                this.frontLegRight.pitch = MathHelper.cos(limbAngle * 0.6662f + 0.3f) * limbDistance;
                this.backLegLeft.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f + 0.3f) * limbDistance;
                this.backLegRight.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * limbDistance;
                this.tail2.pitch = 1.7278761f + 0.31415927f * MathHelper.cos(limbAngle) * limbDistance;
            }
            else {
                this.frontLegLeft.pitch = MathHelper.cos(limbAngle * 0.6662f) * limbDistance;
                this.frontLegRight.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * limbDistance;
                this.backLegLeft.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * limbDistance;
                this.backLegRight.pitch = MathHelper.cos(limbAngle * 0.6662f) * limbDistance;
                if (this.animationState == 1) {
                    this.tail2.pitch = 1.7278761f + 0.7853982f * MathHelper.cos(limbAngle) * limbDistance;
                }
                else {
                    this.tail2.pitch = 1.7278761f + 0.47123894f * MathHelper.cos(limbAngle) * limbDistance;
                }
            }
        }
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        this.body.rotationPointY = 12.0f;
        this.body.rotationPointZ = -10.0f;
        this.head.rotationPointY = 15.0f;
        this.head.rotationPointZ = -9.0f;
        this.tail1.rotationPointY = 15.0f;
        this.tail1.rotationPointZ = 8.0f;
        this.tail2.rotationPointY = 20.0f;
        this.tail2.rotationPointZ = 14.0f;
        this.backLegLeft.rotationPointY = 13.8f;
        this.backLegLeft.rotationPointZ = -5.0f;
        this.backLegRight.rotationPointY = 13.8f;
        this.backLegRight.rotationPointZ = -5.0f;
        this.frontLegLeft.rotationPointY = 18.0f;
        this.frontLegLeft.rotationPointZ = 5.0f;
        this.frontLegRight.rotationPointY = 18.0f;
        this.frontLegRight.rotationPointZ = 5.0f;
        this.tail1.pitch = 0.9f;
        if (entity.isSneaking()) {
            final Cuboid body = this.body;
            ++body.rotationPointY;
            final Cuboid head = this.head;
            head.rotationPointY += 2.0f;
            final Cuboid tail1 = this.tail1;
            ++tail1.rotationPointY;
            final Cuboid tail2 = this.tail2;
            tail2.rotationPointY -= 4.0f;
            final Cuboid tail3 = this.tail2;
            tail3.rotationPointZ += 2.0f;
            this.tail1.pitch = 1.5707964f;
            this.tail2.pitch = 1.5707964f;
            this.animationState = 0;
        }
        else if (entity.isSprinting()) {
            this.tail2.rotationPointY = this.tail1.rotationPointY;
            final Cuboid tail4 = this.tail2;
            tail4.rotationPointZ += 2.0f;
            this.tail1.pitch = 1.5707964f;
            this.tail2.pitch = 1.5707964f;
            this.animationState = 2;
        }
        else {
            this.animationState = 1;
        }
    }
}
