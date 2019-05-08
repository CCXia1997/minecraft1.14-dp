package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.SlimeEntity;

@Environment(EnvType.CLIENT)
public class MagmaCubeEntityModel<T extends SlimeEntity> extends EntityModel<T>
{
    private final Cuboid[] a;
    private final Cuboid b;
    
    public MagmaCubeEntityModel() {
        this.a = new Cuboid[8];
        for (int integer1 = 0; integer1 < this.a.length; ++integer1) {
            int integer2 = 0;
            int integer3;
            if ((integer3 = integer1) == 2) {
                integer2 = 24;
                integer3 = 10;
            }
            else if (integer1 == 3) {
                integer2 = 24;
                integer3 = 19;
            }
            (this.a[integer1] = new Cuboid(this, integer2, integer3)).addBox(-4.0f, (float)(16 + integer1), -4.0f, 8, 1, 8);
        }
        (this.b = new Cuboid(this, 0, 16)).addBox(-2.0f, 18.0f, -2.0f, 4, 4, 4);
    }
    
    @Override
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
        float float5 = MathHelper.lerp(tickDelta, entity.lastStretch, entity.stretch);
        if (float5 < 0.0f) {
            float5 = 0.0f;
        }
        for (int integer6 = 0; integer6 < this.a.length; ++integer6) {
            this.a[integer6].rotationPointY = -(4 - integer6) * float5 * 1.7f;
        }
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.b.render(scale);
        for (final Cuboid cuboid11 : this.a) {
            cuboid11.render(scale);
        }
    }
}
