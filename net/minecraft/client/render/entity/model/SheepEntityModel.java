package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.SheepEntity;

@Environment(EnvType.CLIENT)
public class SheepEntityModel<T extends SheepEntity> extends QuadrupedEntityModel<T>
{
    private float l;
    
    public SheepEntityModel() {
        super(12, 0.0f);
        (this.head = new Cuboid(this, 0, 0)).addBox(-3.0f, -4.0f, -4.0f, 6, 6, 6, 0.6f);
        this.head.setRotationPoint(0.0f, 6.0f, -8.0f);
        (this.body = new Cuboid(this, 28, 8)).addBox(-4.0f, -10.0f, -7.0f, 8, 16, 6, 1.75f);
        this.body.setRotationPoint(0.0f, 5.0f, 2.0f);
        final float float1 = 0.5f;
        (this.leg1 = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, 0.5f);
        this.leg1.setRotationPoint(-3.0f, 12.0f, 7.0f);
        (this.leg2 = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, 0.5f);
        this.leg2.setRotationPoint(3.0f, 12.0f, 7.0f);
        (this.leg3 = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, 0.5f);
        this.leg3.setRotationPoint(-3.0f, 12.0f, -5.0f);
        (this.leg4 = new Cuboid(this, 0, 16)).addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, 0.5f);
        this.leg4.setRotationPoint(3.0f, 12.0f, -5.0f);
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        super.animateModel(entity, limbAngle, limbDistance, tickDelta);
        this.head.rotationPointY = 6.0f + entity.v(tickDelta) * 9.0f;
        this.l = entity.w(tickDelta);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.head.pitch = this.l;
    }
}
