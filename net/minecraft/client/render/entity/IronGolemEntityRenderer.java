package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(EnvType.CLIENT)
public class IronGolemEntityRenderer extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>>
{
    private static final Identifier SKIN;
    
    public IronGolemEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IronGolemEntityModel(), 0.7f);
        this.addFeature(new IronGolemFlowerFeatureRenderer(this));
    }
    
    protected Identifier getTexture(final IronGolemEntity ironGolemEntity) {
        return IronGolemEntityRenderer.SKIN;
    }
    
    @Override
    protected void setupTransforms(final IronGolemEntity entity, final float pitch, final float yaw, final float delta) {
        super.setupTransforms(entity, pitch, yaw, delta);
        if (entity.limbDistance < 0.01) {
            return;
        }
        final float float5 = 13.0f;
        final float float6 = entity.limbAngle - entity.limbDistance * (1.0f - delta) + 6.0f;
        final float float7 = (Math.abs(float6 % 13.0f - 6.5f) - 3.25f) / 3.25f;
        GlStateManager.rotatef(6.5f * float7, 0.0f, 0.0f, 1.0f);
    }
    
    static {
        SKIN = new Identifier("textures/entity/iron_golem.png");
    }
}
