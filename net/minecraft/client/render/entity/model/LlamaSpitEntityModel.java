package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityModel<T extends Entity> extends EntityModel<T>
{
    private final Cuboid a;
    
    public LlamaSpitEntityModel() {
        this(0.0f);
    }
    
    public LlamaSpitEntityModel(final float float1) {
        this.a = new Cuboid(this);
        final int integer2 = 2;
        this.a.setTextureOffset(0, 0).addBox(-4.0f, 0.0f, 0.0f, 2, 2, 2, float1);
        this.a.setTextureOffset(0, 0).addBox(0.0f, -4.0f, 0.0f, 2, 2, 2, float1);
        this.a.setTextureOffset(0, 0).addBox(0.0f, 0.0f, -4.0f, 2, 2, 2, float1);
        this.a.setTextureOffset(0, 0).addBox(0.0f, 0.0f, 0.0f, 2, 2, 2, float1);
        this.a.setTextureOffset(0, 0).addBox(2.0f, 0.0f, 0.0f, 2, 2, 2, float1);
        this.a.setTextureOffset(0, 0).addBox(0.0f, 2.0f, 0.0f, 2, 2, 2, float1);
        this.a.setTextureOffset(0, 0).addBox(0.0f, 0.0f, 2.0f, 2, 2, 2, float1);
        this.a.setRotationPoint(0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
    }
}
