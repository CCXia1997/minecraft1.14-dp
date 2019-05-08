package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class EvokerFangsEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    
    public EvokerFangsEntityModel() {
        (this.a = new Cuboid(this, 0, 0)).setRotationPoint(-5.0f, 22.0f, -5.0f);
        this.a.addBox(0.0f, 0.0f, 0.0f, 10, 12, 10);
        (this.b = new Cuboid(this, 40, 0)).setRotationPoint(1.5f, 22.0f, -4.0f);
        this.b.addBox(0.0f, 0.0f, 0.0f, 4, 14, 8);
        (this.f = new Cuboid(this, 40, 0)).setRotationPoint(-1.5f, 22.0f, 4.0f);
        this.f.addBox(0.0f, 0.0f, 0.0f, 4, 14, 8);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        float float8 = limbAngle * 2.0f;
        if (float8 > 1.0f) {
            float8 = 1.0f;
        }
        float8 = 1.0f - float8 * float8 * float8;
        this.b.roll = 3.1415927f - float8 * 0.35f * 3.1415927f;
        this.f.roll = 3.1415927f + float8 * 0.35f * 3.1415927f;
        this.f.yaw = 3.1415927f;
        final float float9 = (limbAngle + MathHelper.sin(limbAngle * 2.7f)) * 0.6f * 12.0f;
        this.b.rotationPointY = 24.0f - float9;
        this.f.rotationPointY = this.b.rotationPointY;
        this.a.rotationPointY = this.b.rotationPointY;
        this.a.render(scale);
        this.b.render(scale);
        this.f.render(scale);
    }
}
