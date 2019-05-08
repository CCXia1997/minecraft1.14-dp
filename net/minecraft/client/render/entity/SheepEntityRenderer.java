package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;

@Environment(EnvType.CLIENT)
public class SheepEntityRenderer extends MobEntityRenderer<SheepEntity, SheepWoolEntityModel<SheepEntity>>
{
    private static final Identifier SKIN;
    
    public SheepEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SheepWoolEntityModel(), 0.7f);
        this.addFeature(new SheepWoolFeatureRenderer(this));
    }
    
    protected Identifier getTexture(final SheepEntity sheepEntity) {
        return SheepEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/sheep/sheep.png");
    }
}
