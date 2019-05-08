package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.entity.mob.BlazeEntity;

@Environment(EnvType.CLIENT)
public class BlazeEntityRenderer extends MobEntityRenderer<BlazeEntity, BlazeEntityModel<BlazeEntity>>
{
    private static final Identifier SKIN;
    
    public BlazeEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BlazeEntityModel(), 0.5f);
    }
    
    protected Identifier getTexture(final BlazeEntity blazeEntity) {
        return BlazeEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/blaze.png");
    }
}
