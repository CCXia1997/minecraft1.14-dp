package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;

@Environment(EnvType.CLIENT)
public class WolfEntityRenderer extends MobEntityRenderer<WolfEntity, WolfEntityModel<WolfEntity>>
{
    private static final Identifier WILD_SKIN;
    private static final Identifier TAMED_SKIN;
    private static final Identifier ANGRY_SKIN;
    
    public WolfEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WolfEntityModel(), 0.5f);
        this.addFeature(new WolfCollarFeatureRenderer(this));
    }
    
    @Override
    protected float getAge(final WolfEntity entity, final float tickDelta) {
        return entity.ef();
    }
    
    @Override
    public void render(final WolfEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        if (entity.isWet()) {
            final float float10 = entity.getBrightnessAtEyes() * entity.getWetBrightnessMultiplier(tickDelta);
            GlStateManager.color3f(float10, float10, float10);
        }
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    protected Identifier getTexture(final WolfEntity wolfEntity) {
        if (wolfEntity.isTamed()) {
            return WolfEntityRenderer.TAMED_SKIN;
        }
        if (wolfEntity.isAngry()) {
            return WolfEntityRenderer.ANGRY_SKIN;
        }
        return WolfEntityRenderer.WILD_SKIN;
    }
    
    static {
        WILD_SKIN = new Identifier("textures/entity/wolf/wolf.png");
        TAMED_SKIN = new Identifier("textures/entity/wolf/wolf_tame.png");
        ANGRY_SKIN = new Identifier("textures/entity/wolf/wolf_angry.png");
    }
}
