package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class EntityModel<T extends Entity> extends Model
{
    public float handSwingProgress;
    public boolean isRiding;
    public boolean isChild;
    
    public EntityModel() {
        this.isChild = true;
    }
    
    public void render(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
    }
    
    public void setAngles(final T entity, final float limbAngle, final float limbDistance, final float age, final float headYaw, final float headPitch, final float scale) {
    }
    
    public void animateModel(final T entity, final float limbAngle, final float limbDistance, final float tickDelta) {
    }
    
    public void copyStateTo(final EntityModel<T> copy) {
        copy.handSwingProgress = this.handSwingProgress;
        copy.isRiding = this.isRiding;
        copy.isChild = this.isChild;
    }
}
