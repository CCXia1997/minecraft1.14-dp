package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;

@Environment(EnvType.CLIENT)
public class CreeperEntityRenderer extends MobEntityRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>>
{
    private static final Identifier SKIN;
    
    public CreeperEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CreeperEntityModel(), 0.5f);
        this.addFeature(new CreeperChargeFeatureRenderer(this));
    }
    
    @Override
    protected void scale(final CreeperEntity entity, final float tickDelta) {
        float float3 = entity.getClientFuseTime(tickDelta);
        final float float4 = 1.0f + MathHelper.sin(float3 * 100.0f) * float3 * 0.01f;
        float3 = MathHelper.clamp(float3, 0.0f, 1.0f);
        float3 *= float3;
        float3 *= float3;
        final float float5 = (1.0f + float3 * 0.4f) * float4;
        final float float6 = (1.0f + float3 * 0.1f) / float4;
        GlStateManager.scalef(float5, float6, float5);
    }
    
    @Override
    protected int getOverlayColor(final CreeperEntity creeperEntity, final float float2, final float float3) {
        final float float4 = creeperEntity.getClientFuseTime(float3);
        if ((int)(float4 * 10.0f) % 2 == 0) {
            return 0;
        }
        int integer5 = (int)(float4 * 0.2f * 255.0f);
        integer5 = MathHelper.clamp(integer5, 0, 255);
        return integer5 << 24 | 0x30FFFFFF;
    }
    
    protected Identifier getTexture(final CreeperEntity creeperEntity) {
        return CreeperEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/creeper/creeper.png");
    }
}
