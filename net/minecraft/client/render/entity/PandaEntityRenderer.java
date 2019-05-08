package net.minecraft.client.render.entity;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.entity.passive.PandaEntity;

@Environment(EnvType.CLIENT)
public class PandaEntityRenderer extends MobEntityRenderer<PandaEntity, PandaEntityModel<PandaEntity>>
{
    private static final Map<PandaEntity.Gene, Identifier> SKIN_MAP;
    
    public PandaEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PandaEntityModel(9, 0.0f), 0.9f);
        this.addFeature(new PandaHeldItemFeatureRenderer(this));
    }
    
    @Nullable
    protected Identifier getTexture(final PandaEntity pandaEntity) {
        return PandaEntityRenderer.SKIN_MAP.getOrDefault(pandaEntity.getProductGene(), PandaEntityRenderer.SKIN_MAP.get(PandaEntity.Gene.a));
    }
    
    @Override
    protected void setupTransforms(final PandaEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
        if (entity.playingTicks > 0) {
            final int integer5 = entity.playingTicks;
            final int integer6 = integer5 + 1;
            final float float7 = 7.0f;
            final float float8 = entity.isChild() ? 0.3f : 0.8f;
            if (integer5 < 8) {
                final float float9 = 90 * integer5 / 7.0f;
                final float float10 = 90 * integer6 / 7.0f;
                final float float11 = this.a(float9, float10, integer6, delta, 8.0f);
                GlStateManager.translatef(0.0f, (float8 + 0.2f) * (float11 / 90.0f), 0.0f);
                GlStateManager.rotatef(-float11, 1.0f, 0.0f, 0.0f);
            }
            else if (integer5 < 16) {
                final float float9 = (integer5 - 8.0f) / 7.0f;
                final float float10 = 90.0f + 90.0f * float9;
                final float float12 = 90.0f + 90.0f * (integer6 - 8.0f) / 7.0f;
                final float float11 = this.a(float10, float12, integer6, delta, 16.0f);
                GlStateManager.translatef(0.0f, float8 + 0.2f + (float8 - 0.2f) * (float11 - 90.0f) / 90.0f, 0.0f);
                GlStateManager.rotatef(-float11, 1.0f, 0.0f, 0.0f);
            }
            else if (integer5 < 24.0f) {
                final float float9 = (integer5 - 16.0f) / 7.0f;
                final float float10 = 180.0f + 90.0f * float9;
                final float float12 = 180.0f + 90.0f * (integer6 - 16.0f) / 7.0f;
                final float float11 = this.a(float10, float12, integer6, delta, 24.0f);
                GlStateManager.translatef(0.0f, float8 + float8 * (270.0f - float11) / 90.0f, 0.0f);
                GlStateManager.rotatef(-float11, 1.0f, 0.0f, 0.0f);
            }
            else if (integer5 < 32) {
                final float float9 = (integer5 - 24.0f) / 7.0f;
                final float float10 = 270.0f + 90.0f * float9;
                final float float12 = 270.0f + 90.0f * (integer6 - 24.0f) / 7.0f;
                final float float11 = this.a(float10, float12, integer6, delta, 32.0f);
                GlStateManager.translatef(0.0f, float8 * ((360.0f - float11) / 90.0f), 0.0f);
                GlStateManager.rotatef(-float11, 1.0f, 0.0f, 0.0f);
            }
        }
        else {
            GlStateManager.rotatef(0.0f, 1.0f, 0.0f, 0.0f);
        }
        final float float13 = entity.getScaredAnimationProgress(delta);
        if (float13 > 0.0f) {
            GlStateManager.translatef(0.0f, 0.8f * float13, 0.0f);
            GlStateManager.rotatef(MathHelper.lerp(float13, entity.pitch, entity.pitch + 90.0f), 1.0f, 0.0f, 0.0f);
            GlStateManager.translatef(0.0f, -1.0f * float13, 0.0f);
            if (entity.eo()) {
                final float float14 = (float)(Math.cos(entity.age * 1.25) * 3.141592653589793 * 0.05000000074505806);
                GlStateManager.rotatef(float14, 0.0f, 1.0f, 0.0f);
                if (entity.isChild()) {
                    GlStateManager.translatef(0.0f, 0.8f, 0.55f);
                }
            }
        }
        final float float14 = entity.getLieOnBackAnimationProgress(delta);
        if (float14 > 0.0f) {
            final float float7 = entity.isChild() ? 0.5f : 1.3f;
            GlStateManager.translatef(0.0f, float7 * float14, 0.0f);
            GlStateManager.rotatef(MathHelper.lerp(float14, entity.pitch, entity.pitch + 180.0f), 1.0f, 0.0f, 0.0f);
        }
    }
    
    private float a(final float float1, final float float2, final int integer, final float float4, final float float5) {
        if (integer < float5) {
            return MathHelper.lerp(float4, float1, float2);
        }
        return float1;
    }
    
    static {
        SKIN_MAP = SystemUtil.<Map<PandaEntity.Gene, Identifier>>consume(Maps.newEnumMap(PandaEntity.Gene.class), enumMap -> {
            enumMap.put(PandaEntity.Gene.a, new Identifier("textures/entity/panda/panda.png"));
            enumMap.put(PandaEntity.Gene.b, new Identifier("textures/entity/panda/lazy_panda.png"));
            enumMap.put(PandaEntity.Gene.c, new Identifier("textures/entity/panda/worried_panda.png"));
            enumMap.put(PandaEntity.Gene.d, new Identifier("textures/entity/panda/playful_panda.png"));
            enumMap.put(PandaEntity.Gene.e, new Identifier("textures/entity/panda/brown_panda.png"));
            enumMap.put(PandaEntity.Gene.f, new Identifier("textures/entity/panda/weak_panda.png"));
            enumMap.put(PandaEntity.Gene.g, new Identifier("textures/entity/panda/aggressive_panda.png"));
        });
    }
}
