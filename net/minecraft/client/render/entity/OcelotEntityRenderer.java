package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.OcelotEntity;

@Environment(EnvType.CLIENT)
public class OcelotEntityRenderer extends MobEntityRenderer<OcelotEntity, OcelotEntityModel<OcelotEntity>>
{
    private static final Identifier SKIN;
    
    public OcelotEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new OcelotEntityModel(0.0f), 0.4f);
    }
    
    protected Identifier getTexture(final OcelotEntity ocelotEntity) {
        return OcelotEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/cat/ocelot.png");
    }
}
