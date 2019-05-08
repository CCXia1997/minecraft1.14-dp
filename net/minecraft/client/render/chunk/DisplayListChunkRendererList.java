package net.minecraft.client.render.chunk;

import java.util.Iterator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockRenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DisplayListChunkRendererList extends ChunkRendererList
{
    @Override
    public void render(final BlockRenderLayer layer) {
        if (!this.isCameraPositionSet) {
            return;
        }
        for (final ChunkRenderer chunkRenderer3 : this.chunkRenderers) {
            final DisplayListChunkRenderer displayListChunkRenderer4 = (DisplayListChunkRenderer)chunkRenderer3;
            GlStateManager.pushMatrix();
            this.translateToOrigin(chunkRenderer3);
            GlStateManager.callList(displayListChunkRenderer4.a(layer, displayListChunkRenderer4.getChunkRenderData()));
            GlStateManager.popMatrix();
        }
        GlStateManager.clearCurrentColor();
        this.chunkRenderers.clear();
    }
}
