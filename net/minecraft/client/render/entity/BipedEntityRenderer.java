package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.MobEntity;

@Environment(EnvType.CLIENT)
public class BipedEntityRenderer<T extends MobEntity, M extends BipedEntityModel<T>> extends MobEntityRenderer<T, M>
{
    private static final Identifier SKIN;
    
    public BipedEntityRenderer(final EntityRenderDispatcher renderManager, final M model, final float float3) {
        super(renderManager, model, float3);
        this.addFeature(new HeadFeatureRenderer<T, M>(this));
        this.addFeature(new ElytraFeatureRenderer<T, M>(this));
        this.addFeature(new HeldItemFeatureRenderer<T, M>(this));
    }
    
    protected Identifier getTexture(final T mobEntity) {
        return BipedEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/steve.png");
    }
}
