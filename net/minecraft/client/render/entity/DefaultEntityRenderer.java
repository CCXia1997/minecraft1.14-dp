package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class DefaultEntityRenderer extends EntityRenderer<Entity>
{
    public DefaultEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    public void render(final Entity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        EntityRenderer.renderBox(entity.getBoundingBox(), x - entity.prevRenderX, y - entity.prevRenderY, z - entity.prevRenderZ);
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Nullable
    @Override
    protected Identifier getTexture(final Entity entity) {
        return null;
    }
}
