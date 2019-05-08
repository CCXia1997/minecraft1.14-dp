package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ChickenEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid head;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    private final Cuboid j;
    private final Cuboid k;
    
    public ChickenEntityModel() {
        final int integer1 = 16;
        (this.head = new Cuboid(this, 0, 0)).addBox(-2.0f, -6.0f, -2.0f, 4, 6, 3, 0.0f);
        this.head.setRotationPoint(0.0f, 15.0f, -4.0f);
        (this.j = new Cuboid(this, 14, 0)).addBox(-2.0f, -4.0f, -4.0f, 4, 2, 2, 0.0f);
        this.j.setRotationPoint(0.0f, 15.0f, -4.0f);
        (this.k = new Cuboid(this, 14, 4)).addBox(-1.0f, -2.0f, -3.0f, 2, 2, 2, 0.0f);
        this.k.setRotationPoint(0.0f, 15.0f, -4.0f);
        (this.b = new Cuboid(this, 0, 9)).addBox(-3.0f, -4.0f, -3.0f, 6, 8, 6, 0.0f);
        this.b.setRotationPoint(0.0f, 16.0f, 0.0f);
        (this.f = new Cuboid(this, 26, 0)).addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.f.setRotationPoint(-2.0f, 19.0f, 1.0f);
        (this.g = new Cuboid(this, 26, 0)).addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.g.setRotationPoint(1.0f, 19.0f, 1.0f);
        (this.h = new Cuboid(this, 24, 13)).addBox(0.0f, 0.0f, -3.0f, 1, 4, 6);
        this.h.setRotationPoint(-4.0f, 13.0f, 0.0f);
        (this.i = new Cuboid(this, 24, 13)).addBox(-1.0f, 0.0f, -3.0f, 1, 4, 6);
        this.i.setRotationPoint(4.0f, 13.0f, 0.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        if (this.isChild) {
            final float float8 = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 5.0f * scale, 2.0f * scale);
            this.head.render(scale);
            this.j.render(scale);
            this.k.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * scale, 0.0f);
            this.b.render(scale);
            this.f.render(scale);
            this.g.render(scale);
            this.h.render(scale);
            this.i.render(scale);
            GlStateManager.popMatrix();
        }
        else {
            this.head.render(scale);
            this.j.render(scale);
            this.k.render(scale);
            this.b.render(scale);
            this.f.render(scale);
            this.g.render(scale);
            this.h.render(scale);
            this.i.render(scale);
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.head.pitch = headPitch * 0.017453292f;
        this.head.yaw = headYaw * 0.017453292f;
        this.j.pitch = this.head.pitch;
        this.j.yaw = this.head.yaw;
        this.k.pitch = this.head.pitch;
        this.k.yaw = this.head.yaw;
        this.b.pitch = 1.5707964f;
        this.f.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.g.pitch = MathHelper.cos(limbAngle * 0.6662f + 3.1415927f) * 1.4f * limbDistance;
        this.h.roll = age;
        this.i.roll = -age;
    }
}
