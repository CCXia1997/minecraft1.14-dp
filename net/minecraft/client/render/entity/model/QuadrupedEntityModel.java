package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity> extends EntityModel<T>
{
    protected Cuboid head;
    protected Cuboid body;
    protected Cuboid leg1;
    protected Cuboid leg2;
    protected Cuboid leg3;
    protected Cuboid leg4;
    protected float j;
    protected float k;
    
    public QuadrupedEntityModel(final int integer, final float float2) {
        this.j = 8.0f;
        this.k = 4.0f;
        (this.head = new Cuboid(this, 0, 0)).addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, float2);
        this.head.setRotationPoint(0.0f, (float)(18 - integer), -6.0f);
        (this.body = new Cuboid(this, 28, 8)).addBox(-5.0f, -10.0f, -7.0f, 10, 16, 8, float2);
        this.body.setRotationPoint(0.0f, (float)(17 - integer), 2.0f);
        (this.leg1 = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, integer, 4, float2);
        this.leg1.setRotationPoint(-3.0f, (float)(24 - integer), 7.0f);
        (this.leg2 = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, integer, 4, float2);
        this.leg2.setRotationPoint(3.0f, (float)(24 - integer), 7.0f);
        (this.leg3 = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, integer, 4, float2);
        this.leg3.setRotationPoint(-3.0f, (float)(24 - integer), -5.0f);
        (this.leg4 = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, integer, 4, float2);
        this.leg4.setRotationPoint(3.0f, (float)(24 - integer), -5.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            final float float8 = 2.0f;
            GlStateManager.pushMatrix();
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
        this.head.pitch = headPitch * 0.017453292f;
        this.head.yaw = headYaw * 0.017453292f;
        this.body.pitch = 1.5707964f;
        this.leg1.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.leg2.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
        this.leg3.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
        this.leg4.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
    }
}
