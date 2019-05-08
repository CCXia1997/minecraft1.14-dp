package net.minecraft.client.render.entity.feature;

import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class FeatureRenderer<T extends Entity, M extends EntityModel<T>>
{
    private final FeatureRendererContext<T, M> context;
    
    public FeatureRenderer(final FeatureRendererContext<T, M> context) {
        this.context = context;
    }
    
    public M getModel() {
        return this.context.getModel();
    }
    
    public void bindTexture(final Identifier identifier) {
        this.context.bindTexture(identifier);
    }
    
    public void applyLightmapCoordinates(final T entity) {
        this.context.applyLightmapCoordinates(entity);
    }
    
    public abstract void render(final T arg1, final float arg2, final float arg3, final float arg4, final float arg5, final float arg6, final float arg7, final float arg8);
    
    public abstract boolean hasHurtOverlay();
}
