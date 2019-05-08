package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.WolfEntity;

@Environment(EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity> extends EntityModel<T>
{
    private final Cuboid head;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    private final Cuboid j;
    private final Cuboid k;
    
    public WolfEntityModel() {
        final float float1 = 0.0f;
        final float float2 = 13.5f;
        (this.head = new Cuboid(this, 0, 0)).addBox(-2.0f, -3.0f, -2.0f, 6, 6, 4, 0.0f);
        this.head.setRotationPoint(-1.0f, 13.5f, -7.0f);
        (this.b = new Cuboid(this, 18, 14)).addBox(-3.0f, -2.0f, -3.0f, 6, 9, 6, 0.0f);
        this.b.setRotationPoint(0.0f, 14.0f, 2.0f);
        (this.k = new Cuboid(this, 21, 0)).addBox(-3.0f, -3.0f, -3.0f, 8, 6, 7, 0.0f);
        this.k.setRotationPoint(-1.0f, 14.0f, 2.0f);
        (this.f = new Cuboid(this, 0, 18)).addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.f.setRotationPoint(-2.5f, 16.0f, 7.0f);
        (this.g = new Cuboid(this, 0, 18)).addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.g.setRotationPoint(0.5f, 16.0f, 7.0f);
        (this.h = new Cuboid(this, 0, 18)).addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.h.setRotationPoint(-2.5f, 16.0f, -4.0f);
        (this.i = new Cuboid(this, 0, 18)).addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.i.setRotationPoint(0.5f, 16.0f, -4.0f);
        (this.j = new Cuboid(this, 9, 18)).addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.j.setRotationPoint(-1.0f, 12.0f, 8.0f);
        this.head.setTextureOffset(16, 14).addBox(-2.0f, -5.0f, 0.0f, 2, 2, 1, 0.0f);
        this.head.setTextureOffset(16, 14).addBox(2.0f, -5.0f, 0.0f, 2, 2, 1, 0.0f);
        this.head.setTextureOffset(0, 10).addBox(-0.5f, 0.0f, -5.0f, 3, 3, 4, 0.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            final float float8 = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 5.0f * scale, 2.0f * scale);
            this.head.b(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
            this.b.render(scale);
            this.f.render(scale);
            this.g.render(scale);
            this.h.render(scale);
            this.i.render(scale);
            this.j.b(scale);
            this.k.render(scale);
            GlStateManager.popMatrix();
        }
        else {
            this.head.b(scale);
            this.b.render(scale);
            this.f.render(scale);
            this.g.render(scale);
            this.h.render(scale);
            this.i.render(scale);
            this.j.b(scale);
            this.k.render(scale);
        }
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        if (entity.isAngry()) {
            this.j.yaw = 0.0f;
        }
        else {
            this.j.yaw = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        }
        if (entity.isSitting()) {
            this.k.setRotationPoint(-1.0f, 16.0f, -3.0f);
            this.k.pitch = 1.2566371f;
            this.k.yaw = 0.0f;
            this.b.setRotationPoint(0.0f, 18.0f, 0.0f);
            this.b.pitch = 0.7853982f;
            this.j.setRotationPoint(-1.0f, 21.0f, 6.0f);
            this.f.setRotationPoint(-2.5f, 22.0f, 2.0f);
            this.f.pitch = 4.712389f;
            this.g.setRotationPoint(0.5f, 22.0f, 2.0f);
            this.g.pitch = 4.712389f;
            this.h.pitch = 5.811947f;
            this.h.setRotationPoint(-2.49f, 17.0f, -4.0f);
            this.i.pitch = 5.811947f;
            this.i.setRotationPoint(0.51f, 17.0f, -4.0f);
        }
        else {
            this.b.setRotationPoint(0.0f, 14.0f, 2.0f);
            this.b.pitch = 1.5707964f;
            this.k.setRotationPoint(-1.0f, 14.0f, -3.0f);
            this.k.pitch = this.b.pitch;
            this.j.setRotationPoint(-1.0f, 12.0f, 8.0f);
            this.f.setRotationPoint(-2.5f, 16.0f, 7.0f);
            this.g.setRotationPoint(0.5f, 16.0f, 7.0f);
            this.h.setRotationPoint(-2.5f, 16.0f, -4.0f);
            this.i.setRotationPoint(0.5f, 16.0f, -4.0f);
            this.f.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
            this.g.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
            this.h.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
            this.i.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        }
        this.head.roll = entity.getBegAnimationProgress(tickDelta) + entity.getShakeAnimationProgress(tickDelta, 0.0f);
        this.k.roll = entity.getShakeAnimationProgress(tickDelta, -0.08f);
        this.b.roll = entity.getShakeAnimationProgress(tickDelta, -0.16f);
        this.j.roll = entity.getShakeAnimationProgress(tickDelta, -0.2f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.head.pitch = headPitch * 0.017453292f;
        this.head.yaw = headYaw * 0.017453292f;
        this.j.pitch = age;
    }
}
