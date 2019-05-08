package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SpiderEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.CaveSpiderEntity;

@Environment(EnvType.CLIENT)
public class CaveSpiderEntityRenderer extends SpiderEntityRenderer<CaveSpiderEntity>
{
    private static final Identifier SKIN;
    
    public CaveSpiderEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.c *= 0.7f;
    }
    
    protected void scale(final CaveSpiderEntity entity, final float tickDelta) {
        GlStateManager.scalef(0.7f, 0.7f, 0.7f);
    }
    
    @Override
    protected Identifier getTexture(final CaveSpiderEntity caveSpiderEntity) {
        return CaveSpiderEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/spider/cave_spider.png");
    }
}
