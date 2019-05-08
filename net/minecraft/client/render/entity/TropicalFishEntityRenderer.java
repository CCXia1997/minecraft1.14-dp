package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.TropicalFishSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderer extends MobEntityRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>>
{
    private final TropicalFishEntityModelA<TropicalFishEntity> a;
    private final TropicalFishEntityModelB<TropicalFishEntity> j;
    
    public TropicalFishEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new TropicalFishEntityModelA(), 0.15f);
        this.a = new TropicalFishEntityModelA<TropicalFishEntity>();
        this.j = new TropicalFishEntityModelB<TropicalFishEntity>();
        this.addFeature(new TropicalFishSomethingFeatureRenderer(this));
    }
    
    @Nullable
    protected Identifier getTexture(final TropicalFishEntity tropicalFishEntity) {
        return tropicalFishEntity.getShapeId();
    }
    
    @Override
    public void render(final TropicalFishEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        this.model = (M)((entity.getShape() == 0) ? this.a : this.j);
        final float[] arr10 = entity.getBaseColorComponents();
        GlStateManager.color3f(arr10[0], arr10[1], arr10[2]);
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected void setupTransforms(final TropicalFishEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
        final float float5 = 4.3f * MathHelper.sin(0.6f * pitch);
        GlStateManager.rotatef(float5, 0.0f, 1.0f, 0.0f);
        if (!entity.isInsideWater()) {
            GlStateManager.translatef(0.2f, 0.1f, 0.0f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
}
