package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.client.render.entity.model.PillagerEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.PillagerEntity;

@Environment(EnvType.CLIENT)
public class PillagerEntityRenderer extends IllagerEntityRenderer<PillagerEntity>
{
    private static final Identifier SKIN;
    
    public PillagerEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PillagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature((FeatureRenderer<T, EvilVillagerEntityModel<T>>)new HeldItemFeatureRenderer<IllagerEntity, EvilVillagerEntityModel<T>>(this));
    }
    
    protected Identifier getTexture(final PillagerEntity pillagerEntity) {
        return PillagerEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/illager/pillager.png");
    }
}
