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
import net.minecraft.entity.mob.ZombiePigmanEntity;

@Environment(EnvType.CLIENT)
public class PigZombieEntityRenderer extends BipedEntityRenderer<ZombiePigmanEntity, ZombieEntityModel<ZombiePigmanEntity>>
{
    private static final Identifier SKIN;
    
    public PigZombieEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ZombieEntityModel(), 0.5f);
        this.addFeature((FeatureRenderer<ZombiePigmanEntity, ZombieEntityModel<ZombiePigmanEntity>>)new ArmorBipedFeatureRenderer((FeatureRendererContext<LivingEntity, BipedEntityModel>)this, new ZombieEntityModel(0.5f, true), new ZombieEntityModel(1.0f, true)));
    }
    
    @Override
    protected Identifier getTexture(final ZombiePigmanEntity zombiePigmanEntity) {
        return PigZombieEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/zombie_pigman.png");
    }
}
