package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.mob.ZombieVillagerEntity;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityRenderer extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>>
{
    private static final Identifier SKIN;
    
    public ZombieVillagerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final ReloadableResourceManager reloadableResourceManager) {
        super(entityRenderDispatcher, new ZombieVillagerEntityModel(), 0.5f);
        this.addFeature((FeatureRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>>)new ArmorBipedFeatureRenderer((FeatureRendererContext<LivingEntity, BipedEntityModel>)this, new ZombieVillagerEntityModel(0.5f, true), new ZombieVillagerEntityModel(1.0f, true)));
        this.addFeature(new VillagerClothingFeatureRenderer<ZombieVillagerEntity, ZombieVillagerEntityModel<ZombieVillagerEntity>>(this, reloadableResourceManager, "zombie_villager"));
    }
    
    @Override
    protected Identifier getTexture(final ZombieVillagerEntity zombieVillagerEntity) {
        return ZombieVillagerEntityRenderer.SKIN;
    }
    
    @Override
    protected void setupTransforms(final ZombieVillagerEntity entity, final float pitch, float yaw, final float delta) {
        if (entity.isConverting()) {
            yaw += (float)(Math.cos(entity.age * 3.25) * 3.141592653589793 * 0.25);
        }
        super.setupTransforms(entity, pitch, yaw, delta);
    }
    
    static {
        SKIN = new Identifier("textures/entity/zombie_villager/zombie_villager.png");
    }
}
