package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.platform.GLX;
import java.util.Iterator;
import net.minecraft.client.gl.GlBuffer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockRenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VboChunkRendererList extends ChunkRendererList
{
    @Override
    public void render(final BlockRenderLayer layer) {
        if (!this.isCameraPositionSet) {
            return;
        }
        for (final ChunkRenderer chunkRenderer3 : this.chunkRenderers) {
            final GlBuffer glBuffer4 = chunkRenderer3.getGlBuffer(layer.ordinal());
            GlStateManager.pushMatrix();
            this.translateToOrigin(chunkRenderer3);
            glBuffer4.bind();
            this.a();
            glBuffer4.draw(7);
            GlStateManager.popMatrix();
        }
        GlBuffer.unbind();
        GlStateManager.clearCurrentColor();
        this.chunkRenderers.clear();
    }
    
    private void a() {
        GlStateManager.vertexPointer(3, 5126, 28, 0);
        GlStateManager.colorPointer(4, 5121, 28, 12);
        GlStateManager.texCoordPointer(2, 5126, 28, 16);
        GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
        GlStateManager.texCoordPointer(2, 5122, 28, 24);
        GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
    }
}
