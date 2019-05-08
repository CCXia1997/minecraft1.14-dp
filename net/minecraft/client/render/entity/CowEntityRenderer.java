package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.CowEntity;

@Environment(EnvType.CLIENT)
public class CowEntityRenderer extends MobEntityRenderer<CowEntity, CowEntityModel<CowEntity>>
{
    private static final Identifier SKIN;
    
    public CowEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CowEntityModel(), 0.7f);
    }
    
    protected Identifier getTexture(final CowEntity cowEntity) {
        return CowEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/cow/cow.png");
    }
}
