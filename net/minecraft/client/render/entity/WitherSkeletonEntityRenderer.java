package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitherSkeletonEntityRenderer extends SkeletonEntityRenderer
{
    private static final Identifier SKIN;
    
    public WitherSkeletonEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    protected Identifier getTexture(final AbstractSkeletonEntity abstractSkeletonEntity) {
        return WitherSkeletonEntityRenderer.SKIN;
    }
    
    @Override
    protected void scale(final AbstractSkeletonEntity entity, final float tickDelta) {
        GlStateManager.scalef(1.2f, 1.2f, 1.2f);
    }
    
    static {
        SKIN = new Identifier("textures/entity/skeleton/wither_skeleton.png");
    }
}
