package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.GiantEntity;

@Environment(EnvType.CLIENT)
public class GiantEntityRenderer extends MobEntityRenderer<GiantEntity, BipedEntityModel<GiantEntity>>
{
    private static final Identifier SKIN;
    private final float scale;
    
    public GiantEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final float float2) {
        super(entityRenderDispatcher, new GiantEntityModel(), 0.5f * float2);
        this.scale = float2;
        this.addFeature(new HeldItemFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>>(this));
        this.addFeature((FeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>>)new ArmorBipedFeatureRenderer((FeatureRendererContext<LivingEntity, BipedEntityModel>)this, new GiantEntityModel(0.5f, true), new GiantEntityModel(1.0f, true)));
    }
    
    @Override
    protected void scale(final GiantEntity entity, final float tickDelta) {
        GlStateManager.scalef(this.scale, this.scale, this.scale);
    }
    
    protected Identifier getTexture(final GiantEntity giantEntity) {
        return GiantEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/zombie/zombie.png");
    }
}
