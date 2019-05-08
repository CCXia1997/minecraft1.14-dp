package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.entity.mob.EndermiteEntity;

@Environment(EnvType.CLIENT)
public class EndermiteEntityRenderer extends MobEntityRenderer<EndermiteEntity, EndermiteEntityModel<EndermiteEntity>>
{
    private static final Identifier SKIN;
    
    public EndermiteEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EndermiteEntityModel(), 0.3f);
    }
    
    @Override
    protected float getLyingAngle(final EndermiteEntity endermiteEntity) {
        return 180.0f;
    }
    
    protected Identifier getTexture(final EndermiteEntity endermiteEntity) {
        return EndermiteEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/endermite.png");
    }
}
