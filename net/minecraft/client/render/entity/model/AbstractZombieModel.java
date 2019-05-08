package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.model.Cuboid;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.HostileEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractZombieModel<T extends HostileEntity> extends BipedEntityModel<T>
{
    protected AbstractZombieModel(final float scale, final float float2, final int textureWidth, final int integer4) {
        super(scale, float2, textureWidth, integer4);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        final boolean boolean8 = this.a(entity);
        final float float9 = MathHelper.sin(this.handSwingProgress * 3.1415927f);
        final float float10 = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * 3.1415927f);
        this.rightArm.roll = 0.0f;
        this.leftArm.roll = 0.0f;
        this.rightArm.yaw = -(0.1f - float9 * 0.6f);
        this.leftArm.yaw = 0.1f - float9 * 0.6f;
        final float float11 = -3.1415927f / (boolean8 ? 1.5f : 2.25f);
        this.rightArm.pitch = float11;
        this.leftArm.pitch = float11;
        final Cuboid rightArm = this.rightArm;
        rightArm.pitch += float9 * 1.2f - float10 * 0.4f;
        final Cuboid leftArm = this.leftArm;
        leftArm.pitch += float9 * 1.2f - float10 * 0.4f;
        final Cuboid rightArm2 = this.rightArm;
        rightArm2.roll += MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
        final Cuboid leftArm2 = this.leftArm;
        leftArm2.roll -= MathHelper.cos(age * 0.09f) * 0.05f + 0.05f;
        final Cuboid rightArm3 = this.rightArm;
        rightArm3.pitch += MathHelper.sin(age * 0.067f) * 0.05f;
        final Cuboid leftArm3 = this.leftArm;
        leftArm3.pitch -= MathHelper.sin(age * 0.067f) * 0.05f;
    }
    
    public abstract boolean a(final T arg1);
}
