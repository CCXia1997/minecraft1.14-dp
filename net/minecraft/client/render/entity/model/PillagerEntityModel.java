package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.IllagerEntity;

@Environment(EnvType.CLIENT)
public class PillagerEntityModel<T extends IllagerEntity> extends EvilVillagerEntityModel<T>
{
    public PillagerEntityModel(final float float1, final float float2, final int integer3, final int integer4) {
        super(float1, float2, integer3, integer4);
    }
    
    @Override
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
        this.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
        this.a.render(scale);
        this.b.render(scale);
        this.g.render(scale);
        this.h.render(scale);
        this.i.render(scale);
        this.j.render(scale);
    }
}
