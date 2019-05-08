package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HuskEntityRenderer extends ZombieEntityRenderer
{
    private static final Identifier SKIN;
    
    public HuskEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    protected void scale(final ZombieEntity entity, final float tickDelta) {
        final float float3 = 1.0625f;
        GlStateManager.scalef(1.0625f, 1.0625f, 1.0625f);
        super.scale(entity, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final ZombieEntity zombieEntity) {
        return HuskEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/zombie/husk.png");
    }
}
