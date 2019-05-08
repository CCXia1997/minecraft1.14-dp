package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.TurtleEntity;

@Environment(EnvType.CLIENT)
public class EntityModelTurtle<T extends TurtleEntity> extends QuadrupedEntityModel<T>
{
    private final Cuboid l;
    
    public EntityModelTurtle(final float float1) {
        super(12, float1);
        this.textureWidth = 128;
        this.textureHeight = 64;
        (this.head = new Cuboid(this, 3, 0)).addBox(-3.0f, -1.0f, -3.0f, 6, 5, 6, 0.0f);
        this.head.setRotationPoint(0.0f, 19.0f, -10.0f);
        this.body = new Cuboid(this);
        this.body.setTextureOffset(7, 37).addBox(-9.5f, 3.0f, -10.0f, 19, 20, 6, 0.0f);
        this.body.setTextureOffset(31, 1).addBox(-5.5f, 3.0f, -13.0f, 11, 18, 3, 0.0f);
        this.body.setRotationPoint(0.0f, 11.0f, -10.0f);
        this.l = new Cuboid(this);
        this.l.setTextureOffset(70, 33).addBox(-4.5f, 3.0f, -14.0f, 9, 18, 1, 0.0f);
        this.l.setRotationPoint(0.0f, 11.0f, -10.0f);
        final int integer2 = 1;
        (this.leg1 = new Cuboid(this, 1, 23)).addBox(-2.0f, 0.0f, 0.0f, 4, 1, 10, 0.0f);
        this.leg1.setRotationPoint(-3.5f, 22.0f, 11.0f);
        (this.leg2 = new Cuboid(this, 1, 12)).addBox(-2.0f, 0.0f, 0.0f, 4, 1, 10, 0.0f);
        this.leg2.setRotationPoint(3.5f, 22.0f, 11.0f);
        (this.leg3 = new Cuboid(this, 27, 30)).addBox(-13.0f, 0.0f, -2.0f, 13, 1, 5, 0.0f);
        this.leg3.setRotationPoint(-5.0f, 21.0f, -4.0f);
        (this.leg4 = new Cuboid(this, 27, 24)).addBox(0.0f, 0.0f, -2.0f, 13, 1, 5, 0.0f);
        this.leg4.setRotationPoint(5.0f, 21.0f, -4.0f);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            final float float8 = 6.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.16666667f, 0.16666667f, 0.16666667f);
            GlStateManager.translatef(0.0f, 120.0f * scale, 0.0f);
            this.head.render(scale);
            this.body.render(scale);
            this.leg1.render(scale);
            this.leg2.render(scale);
            this.leg3.render(scale);
            this.leg4.render(scale);
            GlStateManager.popMatrix();
        }
        else {
            GlStateManager.pushMatrix();
            if (entity.hasEgg()) {
                GlStateManager.translatef(0.0f, -0.08f, 0.0f);
            }
            this.head.render(scale);
            this.body.render(scale);
            GlStateManager.pushMatrix();
            this.leg1.render(scale);
            this.leg2.render(scale);
            GlStateManager.popMatrix();
            this.leg3.render(scale);
            this.leg4.render(scale);
            if (entity.hasEgg()) {
                this.l.render(scale);
            }
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.leg1.pitch = MathHelper.cos(limbAngle * 0.6662f * 0.6f) * 0.5f * limbDistance;
        this.leg2.pitch = MathHelper.cos(limbAngle * 0.6662f * 0.6f + 3.1415927f) * 0.5f * limbDistance;
        this.leg3.roll = MathHelper.cos(limbAngle * 0.6662f * 0.6f + 3.1415927f) * 0.5f * limbDistance;
        this.leg4.roll = MathHelper.cos(limbAngle * 0.6662f * 0.6f) * 0.5f * limbDistance;
        this.leg3.pitch = 0.0f;
        this.leg4.pitch = 0.0f;
        this.leg3.yaw = 0.0f;
        this.leg4.yaw = 0.0f;
        this.leg1.yaw = 0.0f;
        this.leg2.yaw = 0.0f;
        this.l.pitch = 1.5707964f;
        if (!entity.isInsideWater() && entity.onGround) {
            final float float8 = entity.isDiggingSand() ? 4.0f : 1.0f;
            final float float9 = entity.isDiggingSand() ? 2.0f : 1.0f;
            final float float10 = 5.0f;
            this.leg3.yaw = MathHelper.cos(float8 * limbAngle * 5.0f + 3.1415927f) * 8.0f * limbDistance * float9;
            this.leg3.roll = 0.0f;
            this.leg4.yaw = MathHelper.cos(float8 * limbAngle * 5.0f) * 8.0f * limbDistance * float9;
            this.leg4.roll = 0.0f;
            this.leg1.yaw = MathHelper.cos(limbAngle * 5.0f + 3.1415927f) * 3.0f * limbDistance;
            this.leg1.pitch = 0.0f;
            this.leg2.yaw = MathHelper.cos(limbAngle * 5.0f) * 3.0f * limbDistance;
            this.leg2.pitch = 0.0f;
        }
    }
}
