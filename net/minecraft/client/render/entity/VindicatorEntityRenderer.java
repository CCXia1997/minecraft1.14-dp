package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.VindicatorEntity;

@Environment(EnvType.CLIENT)
public class VindicatorEntityRenderer extends IllagerEntityRenderer<VindicatorEntity>
{
    private static final Identifier SKIN;
    
    public VindicatorEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EvilVillagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature((FeatureRenderer<T, EvilVillagerEntityModel<T>>)new HeldItemFeatureRenderer<VindicatorEntity, EvilVillagerEntityModel<VindicatorEntity>>(this) {
            @Override
            public void render(final VindicatorEntity entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
                if (entity.isAttacking()) {
                    super.render(entity, float2, float3, float4, float5, float6, float7, float8);
                }
            }
        });
    }
    
    protected Identifier getTexture(final VindicatorEntity vindicatorEntity) {
        return VindicatorEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/illager/vindicator.png");
    }
}
