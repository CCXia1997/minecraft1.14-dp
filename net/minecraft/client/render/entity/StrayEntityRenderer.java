package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.client.render.entity.feature.StrayOverlayFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StrayEntityRenderer extends SkeletonEntityRenderer
{
    private static final Identifier SKIN;
    
    public StrayEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.addFeature(new StrayOverlayFeatureRenderer<AbstractSkeletonEntity, StrayEntityModel<AbstractSkeletonEntity>>(this));
    }
    
    @Override
    protected Identifier getTexture(final AbstractSkeletonEntity abstractSkeletonEntity) {
        return StrayEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/skeleton/stray.png");
    }
}
