package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.projectile.DragonFireballEntity;

@Environment(EnvType.CLIENT)
public class DragonFireballEntityRenderer extends EntityRenderer<DragonFireballEntity>
{
    private static final Identifier SKIN;
    
    public DragonFireballEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }
    
    @Override
    public void render(final DragonFireballEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        final Tessellator tessellator10 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder11 = tessellator10.getBufferBuilder();
        final float float12 = 1.0f;
        final float float13 = 0.5f;
        final float float14 = 0.25f;
        GlStateManager.rotatef(180.0f - this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.renderManager.gameOptions.perspective == 2) ? -1 : 1) * -this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        bufferBuilder11.begin(7, VertexFormats.POSITION_UV_NORMAL);
        bufferBuilder11.vertex(-0.5, -0.25, 0.0).texture(0.0, 1.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder11.vertex(0.5, -0.25, 0.0).texture(1.0, 1.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder11.vertex(0.5, 0.75, 0.0).texture(1.0, 0.0).normal(0.0f, 1.0f, 0.0f).next();
        bufferBuilder11.vertex(-0.5, 0.75, 0.0).texture(0.0, 0.0).normal(0.0f, 1.0f, 0.0f).next();
        tessellator10.draw();
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final DragonFireballEntity dragonFireballEntity) {
        return DragonFireballEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/enderdragon/dragon_fireball.png");
    }
}
