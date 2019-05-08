package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class PigEntityModel<T extends Entity> extends QuadrupedEntityModel<T>
{
    public PigEntityModel() {
        this(0.0f);
    }
    
    public PigEntityModel(final float float1) {
        super(6, float1);
        this.head.setTextureOffset(16, 16).addBox(-2.0f, 0.0f, -9.0f, 4, 3, 1, float1);
        this.j = 4.0f;
    }
}
