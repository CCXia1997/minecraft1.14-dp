package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class EndCrystalEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid cube;
    private final Cuboid glass;
    private final Cuboid base;
    
    public EndCrystalEntityModel(final float float1, final boolean boolean2) {
        this.glass = new Cuboid(this, "glass");
        this.glass.setTextureOffset(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        this.cube = new Cuboid(this, "cube");
        this.cube.setTextureOffset(32, 0).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        if (boolean2) {
            this.base = new Cuboid(this, "base");
            this.base.setTextureOffset(0, 16).addBox(-6.0f, 0.0f, -6.0f, 12, 4, 12);
        }
        else {
            this.base = null;
        }
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        GlStateManager.translatef(0.0f, -0.5f, 0.0f);
        if (this.base != null) {
            this.base.render(scale);
        }
        GlStateManager.rotatef(limbDistance, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.8f + age, 0.0f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        this.glass.render(scale);
        final float float8 = 0.875f;
        GlStateManager.scalef(0.875f, 0.875f, 0.875f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotatef(limbDistance, 0.0f, 1.0f, 0.0f);
        this.glass.render(scale);
        GlStateManager.scalef(0.875f, 0.875f, 0.875f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotatef(limbDistance, 0.0f, 1.0f, 0.0f);
        this.cube.render(scale);
        GlStateManager.popMatrix();
    }
}
