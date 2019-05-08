package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(EnvType.CLIENT)
public class TropicalFishSomethingFeatureRenderer extends FeatureRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>>
{
    private final TropicalFishEntityModelA<TropicalFishEntity> a;
    private final TropicalFishEntityModelB<TropicalFishEntity> b;
    
    public TropicalFishSomethingFeatureRenderer(final FeatureRendererContext<TropicalFishEntity, EntityModel<TropicalFishEntity>> context) {
        super(context);
        this.a = new TropicalFishEntityModelA<TropicalFishEntity>(0.008f);
        this.b = new TropicalFishEntityModelB<TropicalFishEntity>(0.008f);
    }
    
    @Override
    public void render(final TropicalFishEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (entity.isInvisible()) {
            return;
        }
        final EntityModel<TropicalFishEntity> entityModel9 = (EntityModel<TropicalFishEntity>)((entity.getShape() == 0) ? this.a : this.b);
        this.bindTexture(entity.getVarietyId());
        final float[] arr10 = entity.getPatternColorComponents();
        GlStateManager.color3f(arr10[0], arr10[1], arr10[2]);
        ((FeatureRenderer<T, EntityModel<TropicalFishEntity>>)this).getModel().copyStateTo(entityModel9);
        entityModel9.animateModel(entity, float2, float3, float4);
        entityModel9.render(entity, float2, float3, float5, float6, float7, float8);
    }
    
    @Override
    public boolean hasHurtOverlay() {
        return true;
    }
}
