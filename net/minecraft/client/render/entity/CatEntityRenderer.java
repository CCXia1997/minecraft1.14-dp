package net.minecraft.client.render.entity;

import net.minecraft.entity.LivingEntity;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;

@Environment(EnvType.CLIENT)
public class CatEntityRenderer extends MobEntityRenderer<CatEntity, CatEntityModel<CatEntity>>
{
    public CatEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CatEntityModel(0.0f), 0.4f);
        this.addFeature(new CatCollarFeatureRenderer(this));
    }
    
    @Nullable
    protected Identifier getTexture(final CatEntity catEntity) {
        return catEntity.getTexture();
    }
    
    @Override
    protected void scale(final CatEntity entity, final float tickDelta) {
        super.scale(entity, tickDelta);
        GlStateManager.scalef(0.8f, 0.8f, 0.8f);
    }
    
    @Override
    protected void setupTransforms(final CatEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
        final float float5 = entity.getSleepAnimation(delta);
        if (float5 > 0.0f) {
            GlStateManager.translatef(0.4f * float5, 0.15f * float5, 0.1f * float5);
            GlStateManager.rotatef(MathHelper.lerpAngleDegrees(float5, 0.0f, 90.0f), 0.0f, 0.0f, 1.0f);
            final BlockPos blockPos6 = new BlockPos(entity);
            final List<PlayerEntity> list7 = entity.world.<PlayerEntity>getEntities(PlayerEntity.class, new BoundingBox(blockPos6).expand(2.0, 2.0, 2.0));
            for (final PlayerEntity playerEntity9 : list7) {
                if (playerEntity9.isSleeping()) {
                    GlStateManager.translatef(0.15f * float5, 0.0f, 0.0f);
                    break;
                }
            }
        }
    }
}
