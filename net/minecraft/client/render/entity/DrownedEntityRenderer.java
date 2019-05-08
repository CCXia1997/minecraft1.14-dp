package net.minecraft.client.render.entity;

import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.mob.DrownedEntity;

@Environment(EnvType.CLIENT)
public class DrownedEntityRenderer extends ZombieBaseEntityRenderer<DrownedEntity, DrownedEntityModel<DrownedEntity>>
{
    private static final Identifier SKIN;
    
    public DrownedEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new DrownedEntityModel(0.0f, 0.0f, 64, 64), new DrownedEntityModel(0.5f, true), new DrownedEntityModel(1.0f, true));
        this.addFeature((FeatureRenderer<DrownedEntity, DrownedEntityModel<DrownedEntity>>)new DrownedOverlayFeatureRenderer((FeatureRendererContext<ZombieEntity, DrownedEntityModel<ZombieEntity>>)this));
    }
    
    @Nullable
    @Override
    protected Identifier getTexture(final ZombieEntity zombieEntity) {
        return DrownedEntityRenderer.SKIN;
    }
    
    @Override
    protected void setupTransforms(final DrownedEntity entity, final float pitch, final float yaw, final float delta) {
        final float float5 = entity.a(delta);
        super.setupTransforms(entity, pitch, yaw, delta);
        if (float5 > 0.0f) {
            GlStateManager.rotatef(MathHelper.lerp(float5, entity.pitch, -10.0f - entity.pitch), 1.0f, 0.0f, 0.0f);
        }
    }
    
    static {
        SKIN = new Identifier("textures/entity/zombie/drowned.png");
    }
}
