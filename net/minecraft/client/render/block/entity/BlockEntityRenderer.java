package net.minecraft.client.render.block.entity;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.world.World;
import net.minecraft.client.texture.TextureManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.Nameable;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity>
{
    public static final Identifier[] DESTROY_STAGE_TEXTURES;
    protected BlockEntityRenderDispatcher renderManager;
    
    public void render(final T entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        final HitResult hitResult10 = this.renderManager.hitResult;
        if (entity instanceof Nameable && hitResult10 != null && hitResult10.getType() == HitResult.Type.BLOCK && entity.getPos().equals(((BlockHitResult)hitResult10).getBlockPos())) {
            this.disableLightmap(true);
            this.renderName(entity, ((Nameable)entity).getDisplayName().getFormattedText(), xOffset, yOffset, zOffset, 12);
            this.disableLightmap(false);
        }
    }
    
    protected void disableLightmap(final boolean boolean1) {
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        if (boolean1) {
            GlStateManager.disableTexture();
        }
        else {
            GlStateManager.enableTexture();
        }
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    protected void bindTexture(final Identifier identifier) {
        final TextureManager textureManager2 = this.renderManager.textureManager;
        if (textureManager2 != null) {
            textureManager2.bindTexture(identifier);
        }
    }
    
    protected World getWorld() {
        return this.renderManager.world;
    }
    
    public void setRenderManager(final BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.renderManager = blockEntityRenderDispatcher;
    }
    
    public TextRenderer getFontRenderer() {
        return this.renderManager.getFontRenderer();
    }
    
    public boolean a(final T blockEntity) {
        return false;
    }
    
    protected void renderName(final T blockEntity, final String string, final double double3, final double double5, final double double7, final int integer9) {
        final Camera camera10 = this.renderManager.cameraEntity;
        final double double8 = blockEntity.getSquaredDistance(camera10.getPos().x, camera10.getPos().y, camera10.getPos().z);
        if (double8 > integer9 * integer9) {
            return;
        }
        final float float13 = camera10.getYaw();
        final float float14 = camera10.getPitch();
        GameRenderer.renderFloatingText(this.getFontRenderer(), string, (float)double3 + 0.5f, (float)double5 + 1.5f, (float)double7 + 0.5f, 0, float13, float14, false);
    }
    
    static {
        DESTROY_STAGE_TEXTURES = new Identifier[] { new Identifier("textures/" + ModelLoader.DESTROY_STAGE_0.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_1.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_2.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_3.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_4.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_5.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_6.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_7.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_8.getPath() + ".png"), new Identifier("textures/" + ModelLoader.DESTROY_STAGE_9.getPath() + ".png") };
    }
}
