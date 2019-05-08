package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SnowmanPumpkinFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.entity.passive.SnowmanEntity;

@Environment(EnvType.CLIENT)
public class SnowmanEntityRenderer extends MobEntityRenderer<SnowmanEntity, SnowmanEntityModel<SnowmanEntity>>
{
    private static final Identifier SKIN;
    
    public SnowmanEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SnowmanEntityModel(), 0.5f);
        this.addFeature(new SnowmanPumpkinFeatureRenderer(this));
    }
    
    protected Identifier getTexture(final SnowmanEntity snowmanEntity) {
        return SnowmanEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/snow_golem.png");
    }
}
