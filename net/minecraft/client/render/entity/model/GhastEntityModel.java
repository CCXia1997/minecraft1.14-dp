package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class GhastEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid[] b;
    
    public GhastEntityModel() {
        this.b = new Cuboid[9];
        final int integer1 = -16;
        (this.a = new Cuboid(this, 0, 0)).addBox(-8.0f, -8.0f, -8.0f, 16, 16, 16);
        final Cuboid a = this.a;
        a.rotationPointY += 8.0f;
        final Random random2 = new Random(1660L);
        for (int integer2 = 0; integer2 < this.b.length; ++integer2) {
            this.b[integer2] = new Cuboid(this, 0, 0);
            final float float4 = ((integer2 % 3 - integer2 / 3 % 2 * 0.5f + 0.25f) / 2.0f * 2.0f - 1.0f) * 5.0f;
            final float float5 = (integer2 / 3 / 2.0f * 2.0f - 1.0f) * 5.0f;
            final int integer3 = random2.nextInt(7) + 8;
            this.b[integer2].addBox(-1.0f, 0.0f, -1.0f, 2, integer3, 2);
            this.b[integer2].rotationPointX = float4;
            this.b[integer2].rotationPointZ = float5;
            this.b[integer2].rotationPointY = 15.0f;
        }
    }
    
    @Override
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        for (int integer8 = 0; integer8 < this.b.length; ++integer8) {
            this.b[integer8].pitch = 0.2f * MathHelper.sin(age * 0.3f + integer8) + 0.4f;
        }
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.6f, 0.0f);
        this.a.render(scale);
        for (final Cuboid cuboid11 : this.b) {
            cuboid11.render(scale);
        }
        GlStateManager.popMatrix();
    }
}
