package net.minecraft.client.render.chunk;

import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DisplayListChunkRenderer extends ChunkRenderer
{
    private final int displayListsStartIndex;
    
    public DisplayListChunkRenderer(final World world, final WorldRenderer worldRenderer) {
        super(world, worldRenderer);
        this.displayListsStartIndex = GlAllocationUtils.genLists(BlockRenderLayer.values().length);
    }
    
    public int a(final BlockRenderLayer blockRenderLayer, final ChunkRenderData chunkRenderData) {
        if (!chunkRenderData.b(blockRenderLayer)) {
            return this.displayListsStartIndex + blockRenderLayer.ordinal();
        }
        return -1;
    }
    
    @Override
    public void delete() {
        super.delete();
        GlAllocationUtils.deleteLists(this.displayListsStartIndex, BlockRenderLayer.values().length);
    }
}
