package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.CatEntity;

@Environment(EnvType.CLIENT)
public class CatEntityModel<T extends CatEntity> extends OcelotEntityModel<T>
{
    private float sleepAnimation;
    private float tailCurlAnimation;
    private float headDownAnimation;
    
    public CatEntityModel(final float float1) {
        super(float1);
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        this.sleepAnimation = entity.getSleepAnimation(tickDelta);
        this.tailCurlAnimation = entity.getTailCurlAnimation(tickDelta);
        this.headDownAnimation = entity.getHeadDownAnimation(tickDelta);
        if (this.sleepAnimation <= 0.0f) {
            this.head.pitch = 0.0f;
            this.head.roll = 0.0f;
            this.backLegLeft.pitch = 0.0f;
            this.backLegLeft.roll = 0.0f;
            this.backLegRight.pitch = 0.0f;
            this.backLegRight.roll = 0.0f;
            this.backLegRight.rotationPointX = -1.2f;
            this.frontLegLeft.pitch = 0.0f;
            this.frontLegRight.pitch = 0.0f;
            this.frontLegRight.roll = 0.0f;
            this.frontLegRight.rotationPointX = -1.1f;
            this.frontLegRight.rotationPointY = 18.0f;
        }
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
        if (entity.isSitting()) {
            this.body.pitch = 0.7853982f;
            final Cuboid body = this.body;
            body.rotationPointY -= 4.0f;
            final Cuboid body2 = this.body;
            body2.rotationPointZ += 5.0f;
            final Cuboid head = this.head;
            head.rotationPointY -= 3.3f;
            final Cuboid head2 = this.head;
            ++head2.rotationPointZ;
            final Cuboid tail1 = this.tail1;
            tail1.rotationPointY += 8.0f;
            final Cuboid tail2 = this.tail1;
            tail2.rotationPointZ -= 2.0f;
            final Cuboid tail3 = this.tail2;
            tail3.rotationPointY += 2.0f;
            final Cuboid tail4 = this.tail2;
            tail4.rotationPointZ -= 0.8f;
            this.tail1.pitch = 1.7278761f;
            this.tail2.pitch = 2.670354f;
            this.backLegLeft.pitch = -0.15707964f;
            this.backLegLeft.rotationPointY = 15.8f;
            this.backLegLeft.rotationPointZ = -7.0f;
            this.backLegRight.pitch = -0.15707964f;
            this.backLegRight.rotationPointY = 15.8f;
            this.backLegRight.rotationPointZ = -7.0f;
            this.frontLegLeft.pitch = -1.5707964f;
            this.frontLegLeft.rotationPointY = 21.0f;
            this.frontLegLeft.rotationPointZ = 1.0f;
            this.frontLegRight.pitch = -1.5707964f;
            this.frontLegRight.rotationPointY = 21.0f;
            this.frontLegRight.rotationPointZ = 1.0f;
            this.animationState = 3;
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.sleepAnimation > 0.0f) {
            this.head.roll = this.lerpAngle(this.head.roll, -1.2707963f, this.sleepAnimation);
            this.head.yaw = this.lerpAngle(this.head.yaw, 1.2707963f, this.sleepAnimation);
            this.backLegLeft.pitch = -1.2707963f;
            this.backLegRight.pitch = -0.47079635f;
            this.backLegRight.roll = -0.2f;
            this.backLegRight.rotationPointX = -0.2f;
            this.frontLegLeft.pitch = -0.4f;
            this.frontLegRight.pitch = 0.5f;
            this.frontLegRight.roll = -0.5f;
            this.frontLegRight.rotationPointX = -0.3f;
            this.frontLegRight.rotationPointY = 20.0f;
            this.tail1.pitch = this.lerpAngle(this.tail1.pitch, 0.8f, this.tailCurlAnimation);
            this.tail2.pitch = this.lerpAngle(this.tail2.pitch, -0.4f, this.tailCurlAnimation);
        }
        if (this.headDownAnimation > 0.0f) {
            this.head.pitch = this.lerpAngle(this.head.pitch, -0.58177644f, this.headDownAnimation);
        }
    }
    
    protected float lerpAngle(final float from, final float to, final float intermediate) {
        float float4;
        for (float4 = to - from; float4 < -3.1415927f; float4 += 6.2831855f) {}
        while (float4 >= 3.1415927f) {
            float4 -= 6.2831855f;
        }
        return from + intermediate * float4;
    }
}
