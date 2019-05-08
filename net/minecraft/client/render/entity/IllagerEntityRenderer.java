package net.minecraft.client.render.entity;

import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.mob.IllagerEntity;

@Environment(EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity> extends MobEntityRenderer<T, EvilVillagerEntityModel<T>>
{
    protected IllagerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final EvilVillagerEntityModel<T> evilVillagerEntityModel, final float float3) {
        super(entityRenderDispatcher, evilVillagerEntityModel, float3);
        this.addFeature(new HeadFeatureRenderer<T, EvilVillagerEntityModel<T>>(this));
    }
    
    public IllagerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EvilVillagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature(new HeadFeatureRenderer<T, EvilVillagerEntityModel<T>>(this));
    }
    
    @Override
    protected void scale(final T entity, final float tickDelta) {
        final float float3 = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
}
