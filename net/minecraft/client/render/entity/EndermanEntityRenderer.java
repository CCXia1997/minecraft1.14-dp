package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import java.util.Random;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.mob.EndermanEntity;

@Environment(EnvType.CLIENT)
public class EndermanEntityRenderer extends MobEntityRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>>
{
    private static final Identifier SKIN;
    private final Random random;
    
    public EndermanEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EndermanEntityModel(0.0f), 0.5f);
        this.random = new Random();
        this.addFeature((FeatureRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>>)new EndermanEyesFeatureRenderer((FeatureRendererContext<LivingEntity, EndermanEntityModel<LivingEntity>>)this));
        this.addFeature(new EndermanBlockFeatureRenderer(this));
    }
    
    @Override
    public void render(final EndermanEntity entity, double x, final double y, double z, final float yaw, final float tickDelta) {
        final BlockState blockState10 = entity.getCarriedBlock();
        final EndermanEntityModel<EndermanEntity> endermanEntityModel11 = ((LivingEntityRenderer<T, EndermanEntityModel<EndermanEntity>>)this).getModel();
        endermanEntityModel11.carryingBlock = (blockState10 != null);
        endermanEntityModel11.angry = entity.isAngry();
        if (entity.isAngry()) {
            final double double12 = 0.02;
            x += this.random.nextGaussian() * 0.02;
            z += this.random.nextGaussian() * 0.02;
        }
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    protected Identifier getTexture(final EndermanEntity endermanEntity) {
        return EndermanEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/enderman/enderman.png");
    }
}
