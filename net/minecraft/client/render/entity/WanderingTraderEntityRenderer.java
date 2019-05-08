package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.WanderingTraderEntity;

@Environment(EnvType.CLIENT)
public class WanderingTraderEntityRenderer extends MobEntityRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>>
{
    private static final Identifier TEXTURE;
    
    public WanderingTraderEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new VillagerResemblingModel(0.0f), 0.5f);
        this.addFeature(new HeadFeatureRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>>(this));
        this.addFeature((FeatureRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>>)new VillagerHeldItemFeatureRenderer((FeatureRendererContext<LivingEntity, VillagerResemblingModel<LivingEntity>>)this));
    }
    
    protected Identifier getTexture(final WanderingTraderEntity wanderingTraderEntity) {
        return WanderingTraderEntityRenderer.TEXTURE;
    }
    
    @Override
    protected void scale(final WanderingTraderEntity entity, final float tickDelta) {
        final float float3 = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }
    
    static {
        TEXTURE = new Identifier("textures/entity/wandering_trader.png");
    }
}
