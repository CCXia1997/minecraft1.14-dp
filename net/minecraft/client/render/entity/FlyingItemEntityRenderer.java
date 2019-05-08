package net.minecraft.client.render.entity;

import net.minecraft.util.Identifier;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.client.texture.SpriteAtlasTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity> extends EntityRenderer<T>
{
    private final ItemRenderer item;
    private final float scale;
    
    public FlyingItemEntityRenderer(final EntityRenderDispatcher renderManager, final ItemRenderer itemRenderer, final float scale) {
        super(renderManager);
        this.item = itemRenderer;
        this.scale = scale;
    }
    
    public FlyingItemEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final ItemRenderer itemRenderer) {
        this(entityRenderDispatcher, itemRenderer, 1.0f);
    }
    
    @Override
    public void render(final T entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(this.scale, this.scale, this.scale);
        GlStateManager.rotatef(-this.renderManager.cameraYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(((this.renderManager.gameOptions.perspective == 2) ? -1 : 1) * this.renderManager.cameraPitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        if (this.e) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(entity));
        }
        this.item.renderItem(((FlyingItemEntity)entity).getStack(), ModelTransformation.Type.h);
        if (this.e) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected Identifier getTexture(final Entity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}
