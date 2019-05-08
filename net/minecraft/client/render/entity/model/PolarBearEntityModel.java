package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(EnvType.CLIENT)
public class PolarBearEntityModel<T extends PolarBearEntity> extends QuadrupedEntityModel<T>
{
    public PolarBearEntityModel() {
        super(12, 0.0f);
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.head = new Cuboid(this, 0, 0)).addBox(-3.5f, -3.0f, -3.0f, 7, 7, 7, 0.0f);
        this.head.setRotationPoint(0.0f, 10.0f, -16.0f);
        this.head.setTextureOffset(0, 44).addBox(-2.5f, 1.0f, -6.0f, 5, 3, 3, 0.0f);
        this.head.setTextureOffset(26, 0).addBox(-4.5f, -4.0f, -1.0f, 2, 2, 1, 0.0f);
        final Cuboid cuboid1 = this.head.setTextureOffset(26, 0);
        cuboid1.mirror = true;
        cuboid1.addBox(2.5f, -4.0f, -1.0f, 2, 2, 1, 0.0f);
        this.body = new Cuboid(this);
        this.body.setTextureOffset(0, 19).addBox(-5.0f, -13.0f, -7.0f, 14, 14, 11, 0.0f);
        this.body.setTextureOffset(39, 0).addBox(-4.0f, -25.0f, -7.0f, 12, 12, 10, 0.0f);
        this.body.setRotationPoint(-2.0f, 9.0f, 12.0f);
        final int integer2 = 10;
        (this.leg1 = new Cuboid(this, 50, 22)).addBox(-2.0f, 0.0f, -2.0f, 4, 10, 8, 0.0f);
        this.leg1.setRotationPoint(-3.5f, 14.0f, 6.0f);
        (this.leg2 = new Cuboid(this, 50, 22)).addBox(-2.0f, 0.0f, -2.0f, 4, 10, 8, 0.0f);
        this.leg2.setRotationPoint(3.5f, 14.0f, 6.0f);
        (this.leg3 = new Cuboid(this, 50, 40)).addBox(-2.0f, 0.0f, -2.0f, 4, 10, 6, 0.0f);
        this.leg3.setRotationPoint(-2.5f, 14.0f, -7.0f);
        (this.leg4 = new Cuboid(this, 50, 40)).addBox(-2.0f, 0.0f, -2.0f, 4, 10, 6, 0.0f);
        this.leg4.setRotationPoint(2.5f, 14.0f, -7.0f);
        final Cuboid leg1 = this.leg1;
        --leg1.rotationPointX;
        final Cuboid leg2 = this.leg2;
        ++leg2.rotationPointX;
        final Cuboid leg3 = this.leg1;
        leg3.rotationPointZ += 0.0f;
        final Cuboid leg4 = this.leg2;
        leg4.rotationPointZ += 0.0f;
        final Cuboid leg5 = this.leg3;
        --leg5.rotationPointX;
        final Cuboid leg6 = this.leg4;
        ++leg6.rotationPointX;
        final Cuboid leg7 = this.leg3;
        --leg7.rotationPointZ;
        final Cuboid leg8 = this.leg4;
        --leg8.rotationPointZ;
        this.k += 2.0f;
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            final float float8 = 2.0f;
            this.j = 16.0f;
            this.k = 4.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.6666667f, 0.6666667f, 0.6666667f);
            GlStateManager.translatef(0.0f, this.j * scale, this.k * scale);
            this.head.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
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
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        final float float8 = age - entity.age;
        float float9 = entity.getWarningAnimationProgress(float8);
        float9 *= float9;
        final float float10 = 1.0f - float9;
        this.body.pitch = 1.5707964f - float9 * 3.1415927f * 0.35f;
        this.body.rotationPointY = 9.0f * float10 + 11.0f * float9;
        this.leg3.rotationPointY = 14.0f * float10 - 6.0f * float9;
        this.leg3.rotationPointZ = -8.0f * float10 - 4.0f * float9;
        final Cuboid leg3 = this.leg3;
        leg3.pitch -= float9 * 3.1415927f * 0.45f;
        this.leg4.rotationPointY = this.leg3.rotationPointY;
        this.leg4.rotationPointZ = this.leg3.rotationPointZ;
        final Cuboid leg4 = this.leg4;
        leg4.pitch -= float9 * 3.1415927f * 0.45f;
        if (this.isChild) {
            this.head.rotationPointY = 10.0f * float10 - 9.0f * float9;
            this.head.rotationPointZ = -16.0f * float10 - 7.0f * float9;
        }
        else {
            this.head.rotationPointY = 10.0f * float10 - 14.0f * float9;
            this.head.rotationPointZ = -16.0f * float10 - 3.0f * float9;
        }
        final Cuboid head = this.head;
        head.pitch += float9 * 3.1415927f * 0.15f;
    }
}
