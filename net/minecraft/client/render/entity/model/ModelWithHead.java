package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ModelWithHead
{
    Cuboid getHead();
    
    default void setHeadAngle(final float float1) {
        this.getHead().applyTransform(float1);
    }
}
