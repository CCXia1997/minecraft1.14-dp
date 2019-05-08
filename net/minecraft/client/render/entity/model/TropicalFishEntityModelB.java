package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityModelB<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    private final Cuboid h;
    private final Cuboid i;
    
    public TropicalFishEntityModelB() {
        this(0.0f);
    }
    
    public TropicalFishEntityModelB(final float float1) {
        this.textureWidth = 32;
        this.textureHeight = 32;
        final int integer2 = 19;
        (this.a = new Cuboid(this, 0, 20)).addBox(-1.0f, -3.0f, -3.0f, 2, 6, 6, float1);
        this.a.setRotationPoint(0.0f, 19.0f, 0.0f);
        (this.b = new Cuboid(this, 21, 16)).addBox(0.0f, -3.0f, 0.0f, 0, 6, 5, float1);
        this.b.setRotationPoint(0.0f, 19.0f, 3.0f);
        (this.f = new Cuboid(this, 2, 16)).addBox(-2.0f, 0.0f, 0.0f, 2, 2, 0, float1);
        this.f.setRotationPoint(-1.0f, 20.0f, 0.0f);
        this.f.yaw = 0.7853982f;
        (this.g = new Cuboid(this, 2, 12)).addBox(0.0f, 0.0f, 0.0f, 2, 2, 0, float1);
        this.g.setRotationPoint(1.0f, 20.0f, 0.0f);
        this.g.yaw = -0.7853982f;
        (this.h = new Cuboid(this, 20, 11)).addBox(0.0f, -4.0f, 0.0f, 0, 4, 6, float1);
        this.h.setRotationPoint(0.0f, 16.0f, -3.0f);
        (this.i = new Cuboid(this, 20, 21)).addBox(0.0f, 0.0f, 0.0f, 0, 4, 6, float1);
        this.i.setRotationPoint(0.0f, 22.0f, -3.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
        this.b.render(scale);
        this.f.render(scale);
        this.g.render(scale);
        this.h.render(scale);
        this.i.render(scale);
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        float float8 = 1.0f;
        if (!entity.isInsideWater()) {
            float8 = 1.5f;
        }
        this.b.yaw = -float8 * 0.45f * MathHelper.sin(0.6f * age);
    }
}
