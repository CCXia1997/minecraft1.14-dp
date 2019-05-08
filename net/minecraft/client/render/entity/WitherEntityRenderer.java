package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;

@Environment(EnvType.CLIENT)
public class WitherEntityRenderer extends MobEntityRenderer<WitherEntity, WitherEntityModel<WitherEntity>>
{
    private static final Identifier INVINCIBLE_SKIN;
    private static final Identifier SKIN;
    
    public WitherEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WitherEntityModel(0.0f), 1.0f);
        this.addFeature(new WitherArmorFeatureRenderer(this));
    }
    
    protected Identifier getTexture(final WitherEntity witherEntity) {
        final int integer2 = witherEntity.getInvulTimer();
        if (integer2 <= 0 || (integer2 <= 80 && integer2 / 5 % 2 == 1)) {
            return WitherEntityRenderer.SKIN;
        }
        return WitherEntityRenderer.INVINCIBLE_SKIN;
    }
    
    @Override
    protected void scale(final WitherEntity entity, final float tickDelta) {
        float float3 = 2.0f;
        final int integer4 = entity.getInvulTimer();
        if (integer4 > 0) {
            float3 -= (integer4 - tickDelta) / 220.0f * 0.5f;
        }
        GlStateManager.scalef(float3, float3, float3);
    }
    
    static {
        INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
        SKIN = new Identifier("textures/entity/wither/wither.png");
    }
}
