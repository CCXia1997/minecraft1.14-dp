package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity> extends QuadrupedEntityModel<T>
{
    private final Cuboid l;
    private final Cuboid m;
    
    public LlamaEntityModel(final float float1) {
        super(15, float1);
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.head = new Cuboid(this, 0, 0)).addBox(-2.0f, -14.0f, -10.0f, 4, 4, 9, float1);
        this.head.setRotationPoint(0.0f, 7.0f, -6.0f);
        this.head.setTextureOffset(0, 14).addBox(-4.0f, -16.0f, -6.0f, 8, 18, 6, float1);
        this.head.setTextureOffset(17, 0).addBox(-4.0f, -19.0f, -4.0f, 3, 3, 2, float1);
        this.head.setTextureOffset(17, 0).addBox(1.0f, -19.0f, -4.0f, 3, 3, 2, float1);
        (this.body = new Cuboid(this, 29, 0)).addBox(-6.0f, -10.0f, -7.0f, 12, 18, 10, float1);
        this.body.setRotationPoint(0.0f, 5.0f, 2.0f);
        (this.l = new Cuboid(this, 45, 28)).addBox(-3.0f, 0.0f, 0.0f, 8, 8, 3, float1);
        this.l.setRotationPoint(-8.5f, 3.0f, 3.0f);
        this.l.yaw = 1.5707964f;
        (this.m = new Cuboid(this, 45, 41)).addBox(-3.0f, 0.0f, 0.0f, 8, 8, 3, float1);
        this.m.setRotationPoint(5.5f, 3.0f, 3.0f);
        this.m.yaw = 1.5707964f;
        final int integer2 = 4;
        final int integer3 = 14;
        (this.leg1 = new Cuboid(this, 29, 29)).addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, float1);
        this.leg1.setRotationPoint(-2.5f, 10.0f, 6.0f);
        (this.leg2 = new Cuboid(this, 29, 29)).addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, float1);
        this.leg2.setRotationPoint(2.5f, 10.0f, 6.0f);
        (this.leg3 = new Cuboid(this, 29, 29)).addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, float1);
        this.leg3.setRotationPoint(-2.5f, 10.0f, -4.0f);
        (this.leg4 = new Cuboid(this, 29, 29)).addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, float1);
        this.leg4.setRotationPoint(2.5f, 10.0f, -4.0f);
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
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        final boolean boolean8 = !entity.isChild() && entity.hasChest();
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            final float float9 = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, this.j * scale, this.k * scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            final float float10 = 0.7f;
            GlStateManager.scalef(0.71428573f, 0.64935064f, 0.7936508f);
            GlStateManager.translatef(0.0f, 21.0f * scale, 0.22f);
            this.head.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            final float float11 = 1.1f;
            GlStateManager.scalef(0.625f, 0.45454544f, 0.45454544f);
            GlStateManager.translatef(0.0f, 33.0f * scale, 0.0f);
            this.body.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.45454544f, 0.41322312f, 0.45454544f);
            GlStateManager.translatef(0.0f, 33.0f * scale, 0.0f);
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
        if (boolean8) {
            this.l.render(scale);
            this.m.render(scale);
        }
    }
}
