package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, M extends ZombieEntityModel<T>> extends BipedEntityRenderer<T, M>
{
    private static final Identifier SKIN;
    
    protected ZombieBaseEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final M zombieEntityModel2, final M zombieEntityModel3, final M zombieEntityModel4) {
        super(entityRenderDispatcher, zombieEntityModel2, 0.5f);
        this.addFeature((FeatureRenderer<T, M>)new ArmorBipedFeatureRenderer((FeatureRendererContext<LivingEntity, BipedEntityModel>)this, zombieEntityModel3, zombieEntityModel4));
    }
    
    @Override
    protected Identifier getTexture(final ZombieEntity zombieEntity) {
        return ZombieBaseEntityRenderer.SKIN;
    }
    
    @Override
    protected void setupTransforms(final T entity, final float pitch, float yaw, final float delta) {
        if (entity.isConvertingInWater()) {
            yaw += (float)(Math.cos(entity.age * 3.25) * 3.141592653589793 * 0.25);
        }
        super.setupTransforms(entity, pitch, yaw, delta);
    }
    
    static {
        SKIN = new Identifier("textures/entity/zombie/zombie.png");
    }
}
