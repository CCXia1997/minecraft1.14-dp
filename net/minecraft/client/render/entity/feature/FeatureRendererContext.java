package net.minecraft.client.render.entity.feature;

import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public interface FeatureRendererContext<T extends Entity, M extends EntityModel<T>>
{
    M getModel();
    
    void bindTexture(final Identifier arg1);
    
    void applyLightmapCoordinates(final T arg1);
}
