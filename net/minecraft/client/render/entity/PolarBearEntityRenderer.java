package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(EnvType.CLIENT)
public class PolarBearEntityRenderer extends MobEntityRenderer<PolarBearEntity, PolarBearEntityModel<PolarBearEntity>>
{
    private static final Identifier SKIN;
    
    public PolarBearEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PolarBearEntityModel(), 0.9f);
    }
    
    protected Identifier getTexture(final PolarBearEntity polarBearEntity) {
        return PolarBearEntityRenderer.SKIN;
    }
    
    @Override
    protected void scale(final PolarBearEntity entity, final float tickDelta) {
        GlStateManager.scalef(1.2f, 1.2f, 1.2f);
        super.scale(entity, tickDelta);
    }
    
    static {
        SKIN = new Identifier("textures/entity/bear/polarbear.png");
    }
}
