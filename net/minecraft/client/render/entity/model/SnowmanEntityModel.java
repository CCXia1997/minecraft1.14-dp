package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SnowmanEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    
    public SnowmanEntityModel() {
        final float float1 = 4.0f;
        final float float2 = 0.0f;
        (this.f = new Cuboid(this, 0, 0).setTextureSize(64, 64)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, -0.5f);
        this.f.setRotationPoint(0.0f, 4.0f, 0.0f);
        (this.g = new Cuboid(this, 32, 0).setTextureSize(64, 64)).addBox(-1.0f, 0.0f, -1.0f, 12, 2, 2, -0.5f);
        this.g.setRotationPoint(0.0f, 6.0f, 0.0f);
        (this.h = new Cuboid(this, 32, 0).setTextureSize(64, 64)).addBox(-1.0f, 0.0f, -1.0f, 12, 2, 2, -0.5f);
        this.h.setRotationPoint(0.0f, 6.0f, 0.0f);
        (this.a = new Cuboid(this, 0, 16).setTextureSize(64, 64)).addBox(-5.0f, -10.0f, -5.0f, 10, 10, 10, -0.5f);
        this.a.setRotationPoint(0.0f, 13.0f, 0.0f);
        (this.b = new Cuboid(this, 0, 36).setTextureSize(64, 64)).addBox(-6.0f, -12.0f, -6.0f, 12, 12, 12, -0.5f);
        this.b.setRotationPoint(0.0f, 24.0f, 0.0f);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.f.yaw = headYaw * 0.017453292f;
        this.f.pitch = headPitch * 0.017453292f;
        this.a.yaw = headYaw * 0.017453292f * 0.25f;
        final float float8 = MathHelper.sin(this.a.yaw);
        final float float9 = MathHelper.cos(this.a.yaw);
        this.g.roll = 1.0f;
        this.h.roll = -1.0f;
        this.g.yaw = 0.0f + this.a.yaw;
        this.h.yaw = 3.1415927f + this.a.yaw;
        this.g.rotationPointX = float9 * 5.0f;
        this.g.rotationPointZ = -float8 * 5.0f;
        this.h.rotationPointX = -float9 * 5.0f;
        this.h.rotationPointZ = float8 * 5.0f;
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
        this.b.render(scale);
        this.f.render(scale);
        this.g.render(scale);
        this.h.render(scale);
    }
    
    public Cuboid a() {
        return this.f;
    }
}
