package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;

@Environment(EnvType.CLIENT)
public class VillagerEntityRenderer extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>
{
    private static final Identifier VILLAGER_SKIN;
    
    public VillagerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final ReloadableResourceManager reloadableResourceManager) {
        super(entityRenderDispatcher, new VillagerResemblingModel(0.0f), 0.5f);
        this.addFeature(new HeadFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this));
        this.addFeature(new VillagerClothingFeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>(this, reloadableResourceManager, "villager"));
        this.addFeature((FeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>>)new VillagerHeldItemFeatureRenderer((FeatureRendererContext<LivingEntity, VillagerResemblingModel<LivingEntity>>)this));
    }
    
    protected Identifier getTexture(final VillagerEntity villagerEntity) {
        return VillagerEntityRenderer.VILLAGER_SKIN;
    }
    
    @Override
    protected void scale(final VillagerEntity entity, final float tickDelta) {
        float float3 = 0.9375f;
        if (entity.isChild()) {
            float3 *= 0.5;
            this.c = 0.25f;
        }
        else {
            this.c = 0.5f;
        }
        GlStateManager.scalef(float3, float3, float3);
    }
    
    static {
        VILLAGER_SKIN = new Identifier("textures/entity/villager/villager.png");
    }
}
