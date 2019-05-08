package net.minecraft.client.render.chunk;

import net.minecraft.block.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class ChunkRendererList
{
    private double cameraX;
    private double cameraY;
    private double cameraZ;
    protected final List<ChunkRenderer> chunkRenderers;
    protected boolean isCameraPositionSet;
    
    public ChunkRendererList() {
        this.chunkRenderers = Lists.newArrayListWithCapacity(17424);
    }
    
    public void setCameraPosition(final double x, final double y, final double z) {
        this.isCameraPositionSet = true;
        this.chunkRenderers.clear();
        this.cameraX = x;
        this.cameraY = y;
        this.cameraZ = z;
    }
    
    public void translateToOrigin(final ChunkRenderer renderer) {
        final BlockPos blockPos2 = renderer.getOrigin();
        GlStateManager.translatef((float)(blockPos2.getX() - this.cameraX), (float)(blockPos2.getY() - this.cameraY), (float)(blockPos2.getZ() - this.cameraZ));
    }
    
    public void add(final ChunkRenderer chunkRenderer, final BlockRenderLayer layer) {
        this.chunkRenderers.add(chunkRenderer);
    }
    
    public abstract void render(final BlockRenderLayer arg1);
}
