package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SlimeEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    private final Cuboid b;
    private final Cuboid f;
    private final Cuboid g;
    
    public SlimeEntityModel(final int integer) {
        if (integer > 0) {
            (this.a = new Cuboid(this, 0, integer)).addBox(-3.0f, 17.0f, -3.0f, 6, 6, 6);
            (this.b = new Cuboid(this, 32, 0)).addBox(-3.25f, 18.0f, -3.5f, 2, 2, 2);
            (this.f = new Cuboid(this, 32, 4)).addBox(1.25f, 18.0f, -3.5f, 2, 2, 2);
            (this.g = new Cuboid(this, 32, 8)).addBox(0.0f, 21.0f, -3.5f, 1, 1, 1);
        }
        else {
            (this.a = new Cuboid(this, 0, integer)).addBox(-4.0f, 16.0f, -4.0f, 8, 8, 8);
            this.b = null;
            this.f = null;
            this.g = null;
        }
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        GlStateManager.translatef(0.0f, 0.001f, 0.0f);
        this.a.render(scale);
        if (this.b != null) {
            this.b.render(scale);
            this.f.render(scale);
            this.g.render(scale);
        }
    }
}
