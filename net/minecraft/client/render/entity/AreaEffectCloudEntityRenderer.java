package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AreaEffectCloudEntity;

@Environment(EnvType.CLIENT)
public class AreaEffectCloudEntityRenderer extends EntityRenderer<AreaEffectCloudEntity>
{
    public AreaEffectCloudEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Nullable
    @Override
    protected Identifier getTexture(final AreaEffectCloudEntity areaEffectCloudEntity) {
        return null;
    }
}
