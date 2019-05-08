package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.IllagerBeastEntityModel;
import net.minecraft.entity.mob.RavagerEntity;

@Environment(EnvType.CLIENT)
public class IllagerBeastEntityRenderer extends MobEntityRenderer<RavagerEntity, IllagerBeastEntityModel>
{
    private static final Identifier SKIN;
    
    public IllagerBeastEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IllagerBeastEntityModel(), 1.1f);
    }
    
    protected Identifier getTexture(final RavagerEntity ravagerEntity) {
        return IllagerBeastEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/illager/ravager.png");
    }
}
