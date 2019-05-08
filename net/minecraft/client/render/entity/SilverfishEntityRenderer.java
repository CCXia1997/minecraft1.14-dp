package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SilverfishEntityModel;
import net.minecraft.entity.mob.SilverfishEntity;

@Environment(EnvType.CLIENT)
public class SilverfishEntityRenderer extends MobEntityRenderer<SilverfishEntity, SilverfishEntityModel<SilverfishEntity>>
{
    private static final Identifier SKIN;
    
    public SilverfishEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SilverfishEntityModel(), 0.3f);
    }
    
    @Override
    protected float getLyingAngle(final SilverfishEntity silverfishEntity) {
        return 180.0f;
    }
    
    protected Identifier getTexture(final SilverfishEntity silverfishEntity) {
        return SilverfishEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/silverfish.png");
    }
}
