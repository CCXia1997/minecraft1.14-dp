package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class WitchEntityModel<T extends Entity> extends VillagerResemblingModel<T>
{
    private boolean m;
    private final Cuboid mole;
    
    public WitchEntityModel(final float scale) {
        super(scale, 64, 128);
        (this.mole = new Cuboid(this).setTextureSize(64, 128)).setRotationPoint(0.0f, -2.0f, 0.0f);
        this.mole.setTextureOffset(0, 0).addBox(0.0f, 3.0f, -6.75f, 1, 1, 1, -0.25f);
        this.nose.addChild(this.mole);
        this.head.removeChild(this.headOverlay);
        (this.headOverlay = new Cuboid(this).setTextureSize(64, 128)).setRotationPoint(-5.0f, -10.03125f, -5.0f);
        this.headOverlay.setTextureOffset(0, 64).addBox(0.0f, 0.0f, 0.0f, 10, 2, 10);
        this.head.addChild(this.headOverlay);
        final Cuboid cuboid2 = new Cuboid(this).setTextureSize(64, 128);
        cuboid2.setRotationPoint(1.75f, -4.0f, 2.0f);
        cuboid2.setTextureOffset(0, 76).addBox(0.0f, 0.0f, 0.0f, 7, 4, 7);
        cuboid2.pitch = -0.05235988f;
        cuboid2.roll = 0.02617994f;
        this.headOverlay.addChild(cuboid2);
        final Cuboid cuboid3 = new Cuboid(this).setTextureSize(64, 128);
        cuboid3.setRotationPoint(1.75f, -4.0f, 2.0f);
        cuboid3.setTextureOffset(0, 87).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4);
        cuboid3.pitch = -0.10471976f;
        cuboid3.roll = 0.05235988f;
        cuboid2.addChild(cuboid3);
        final Cuboid cuboid4 = new Cuboid(this).setTextureSize(64, 128);
        cuboid4.setRotationPoint(1.75f, -2.0f, 2.0f);
        cuboid4.setTextureOffset(0, 95).addBox(0.0f, 0.0f, 0.0f, 1, 2, 1, 0.25f);
        cuboid4.pitch = -0.20943952f;
        cuboid4.roll = 0.10471976f;
        cuboid3.addChild(cuboid4);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.nose.x = 0.0f;
        this.nose.y = 0.0f;
        this.nose.z = 0.0f;
        final float float8 = 0.01f * (entity.getEntityId() % 10);
        this.nose.pitch = MathHelper.sin(entity.age * float8) * 4.5f * 0.017453292f;
        this.nose.yaw = 0.0f;
        this.nose.roll = MathHelper.cos(entity.age * float8) * 2.5f * 0.017453292f;
        if (this.m) {
            this.nose.pitch = -0.9f;
            this.nose.z = -0.09375f;
            this.nose.y = 0.1875f;
        }
    }
    
    public Cuboid b() {
        return this.nose;
    }
    
    public void b(final boolean boolean1) {
        this.m = boolean1;
    }
}
