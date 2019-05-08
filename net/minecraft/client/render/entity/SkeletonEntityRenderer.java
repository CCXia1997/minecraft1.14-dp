package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.entity.mob.AbstractSkeletonEntity;

@Environment(EnvType.CLIENT)
public class SkeletonEntityRenderer extends BipedEntityRenderer<AbstractSkeletonEntity, StrayEntityModel<AbstractSkeletonEntity>>
{
    private static final Identifier SKIN;
    
    public SkeletonEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new StrayEntityModel(), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<AbstractSkeletonEntity, StrayEntityModel<AbstractSkeletonEntity>>(this));
        this.addFeature((FeatureRenderer<AbstractSkeletonEntity, StrayEntityModel<AbstractSkeletonEntity>>)new ArmorBipedFeatureRenderer((FeatureRendererContext<LivingEntity, BipedEntityModel>)this, new StrayEntityModel(0.5f, true), new StrayEntityModel(1.0f, true)));
    }
    
    @Override
    protected Identifier getTexture(final AbstractSkeletonEntity abstractSkeletonEntity) {
        return SkeletonEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/skeleton/skeleton.png");
    }
}
