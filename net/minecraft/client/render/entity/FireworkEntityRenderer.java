package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.FireworkEntity;

@Environment(EnvType.CLIENT)
public class FireworkEntityRenderer extends EntityRenderer<FireworkEntity>
{
    private final ItemRenderer itemRenderer;
    
    public FireworkEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final ItemRenderer itemRenderer) {
        super(entityRenderDispatcher);
        this.itemRenderer = itemRenderer;
    }
    
    @Override
    public void render(final FireworkEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotatef(-this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.renderManager.gameOptions.perspective == 2) ? -1 : 1) * this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        if (entity.wasShotAtAngle()) {
            GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
        }
        else {
            GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        }
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        this.itemRenderer.renderItem(entity.getStack(), ModelTransformation.Type.h);
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final FireworkEntity fireworkEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}
