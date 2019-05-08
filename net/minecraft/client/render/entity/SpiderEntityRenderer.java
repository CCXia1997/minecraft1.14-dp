package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SpiderEyesFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.mob.SpiderEntity;

@Environment(EnvType.CLIENT)
public class SpiderEntityRenderer<T extends SpiderEntity> extends MobEntityRenderer<T, SpiderEntityModel<T>>
{
    private static final Identifier SKIN;
    
    public SpiderEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SpiderEntityModel(), 0.8f);
        this.addFeature(new SpiderEyesFeatureRenderer<T, SpiderEntityModel<T>>(this));
    }
    
    @Override
    protected float getLyingAngle(final T spiderEntity) {
        return 180.0f;
    }
    
    protected Identifier getTexture(final T spiderEntity) {
        return SpiderEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/spider/spider.png");
    }
}
