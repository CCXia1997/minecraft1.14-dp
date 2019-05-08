package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.PigSaddleFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;

@Environment(EnvType.CLIENT)
public class PigEntityRenderer extends MobEntityRenderer<PigEntity, PigEntityModel<PigEntity>>
{
    private static final Identifier SKIN;
    
    public PigEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PigEntityModel(), 0.7f);
        this.addFeature(new PigSaddleFeatureRenderer(this));
    }
    
    protected Identifier getTexture(final PigEntity pigEntity) {
        return PigEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/pig/pig.png");
    }
}
