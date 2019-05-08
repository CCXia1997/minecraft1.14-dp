package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ElytraEntityModel<T extends LivingEntity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    
    public ElytraEntityModel() {
        (this.b = new Cuboid(this, 22, 0)).addBox(-10.0f, 0.0f, 0.0f, 10, 20, 2, 1.0f);
        this.a = new Cuboid(this, 22, 0);
        this.a.mirror = true;
        this.a.addBox(0.0f, 0.0f, 0.0f, 10, 20, 2, 1.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();
        if (entity.isChild()) {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 1.5f, -0.1f);
            this.b.render(scale);
            this.a.render(scale);
            GlStateManager.popMatrix();
        }
        else {
            this.b.render(scale);
            this.a.render(scale);
        }
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        float float8 = 0.2617994f;
        float float9 = -0.2617994f;
        float float10 = 0.0f;
        float float11 = 0.0f;
        if (entity.isFallFlying()) {
            float float12 = 1.0f;
            final Vec3d vec3d13 = entity.getVelocity();
            if (vec3d13.y < 0.0) {
                final Vec3d vec3d14 = vec3d13.normalize();
                float12 = 1.0f - (float)Math.pow(-vec3d14.y, 1.5);
            }
            float8 = float12 * 0.34906584f + (1.0f - float12) * float8;
            float9 = float12 * -1.5707964f + (1.0f - float12) * float9;
        }
        else if (entity.isInSneakingPose()) {
            float8 = 0.6981317f;
            float9 = -0.7853982f;
            float10 = 3.0f;
            float11 = 0.08726646f;
        }
        this.b.rotationPointX = 5.0f;
        this.b.rotationPointY = float10;
        if (entity instanceof AbstractClientPlayerEntity) {
            final AbstractClientPlayerEntity abstractClientPlayerEntity13;
            final AbstractClientPlayerEntity abstractClientPlayerEntity12 = abstractClientPlayerEntity13 = (AbstractClientPlayerEntity)entity;
            abstractClientPlayerEntity13.elytraPitch += (float)((float8 - abstractClientPlayerEntity12.elytraPitch) * 0.1);
            final AbstractClientPlayerEntity abstractClientPlayerEntity14 = abstractClientPlayerEntity12;
            abstractClientPlayerEntity14.elytraYaw += (float)((float11 - abstractClientPlayerEntity12.elytraYaw) * 0.1);
            final AbstractClientPlayerEntity abstractClientPlayerEntity15 = abstractClientPlayerEntity12;
            abstractClientPlayerEntity15.elytraRoll += (float)((float9 - abstractClientPlayerEntity12.elytraRoll) * 0.1);
            this.b.pitch = abstractClientPlayerEntity12.elytraPitch;
            this.b.yaw = abstractClientPlayerEntity12.elytraYaw;
            this.b.roll = abstractClientPlayerEntity12.elytraRoll;
        }
        else {
            this.b.pitch = float8;
            this.b.roll = float9;
            this.b.yaw = float11;
        }
        this.a.rotationPointX = -this.b.rotationPointX;
        this.a.yaw = -this.b.yaw;
        this.a.rotationPointY = this.b.rotationPointY;
        this.a.pitch = this.b.pitch;
        this.a.roll = -this.b.roll;
    }
}
