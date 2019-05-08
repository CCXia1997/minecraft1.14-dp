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
import net.minecraft.entity.mob.SpellcastingIllagerEntity;

@Environment(EnvType.CLIENT)
public class EvokerIllagerEntityRenderer<T extends SpellcastingIllagerEntity> extends IllagerEntityRenderer<T>
{
    private static final Identifier EVOKER_TEXTURE;
    
    public EvokerIllagerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EvilVillagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature((FeatureRenderer<T, EvilVillagerEntityModel<T>>)new HeldItemFeatureRenderer<T, EvilVillagerEntityModel<T>>(this) {
            @Override
            public void render(final T entity, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
                if (entity.isSpellcasting()) {
                    super.render(entity, float2, float3, float4, float5, float6, float7, float8);
                }
            }
        });
    }
    
    protected Identifier getTexture(final T spellcastingIllagerEntity) {
        return EvokerIllagerEntityRenderer.EVOKER_TEXTURE;
    }
    
    static {
        EVOKER_TEXTURE = new Identifier("textures/entity/illager/evoker.png");
    }
}
