package net.minecraft.client.render.chunk;

import net.minecraft.util.math.Direction;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.block.entity.BlockEntity;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChunkRenderData
{
    public static final ChunkRenderData EMPTY;
    private final boolean[] b;
    private final boolean[] initialized;
    private boolean empty;
    private final List<BlockEntity> blockEntities;
    private ChunkOcclusionGraph f;
    private BufferBuilder.State bufferState;
    
    public ChunkRenderData() {
        this.b = new boolean[BlockRenderLayer.values().length];
        this.initialized = new boolean[BlockRenderLayer.values().length];
        this.empty = true;
        this.blockEntities = Lists.newArrayList();
        this.f = new ChunkOcclusionGraph();
    }
    
    public boolean isEmpty() {
        return this.empty;
    }
    
    protected void a(final BlockRenderLayer blockRenderLayer) {
        this.empty = false;
        this.b[blockRenderLayer.ordinal()] = true;
    }
    
    public boolean b(final BlockRenderLayer blockRenderLayer) {
        return !this.b[blockRenderLayer.ordinal()];
    }
    
    public void markBufferInitialized(final BlockRenderLayer blockRenderLayer) {
        this.initialized[blockRenderLayer.ordinal()] = true;
    }
    
    public boolean isBufferInitialized(final BlockRenderLayer blockRenderLayer) {
        return this.initialized[blockRenderLayer.ordinal()];
    }
    
    public List<BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }
    
    public void addBlockEntity(final BlockEntity blockEntity) {
        this.blockEntities.add(blockEntity);
    }
    
    public boolean a(final Direction direction1, final Direction direction2) {
        return this.f.isVisibleThrough(direction1, direction2);
    }
    
    public void a(final ChunkOcclusionGraph chunkOcclusionGraph) {
        this.f = chunkOcclusionGraph;
    }
    
    public BufferBuilder.State getBufferState() {
        return this.bufferState;
    }
    
    public void setBufferState(final BufferBuilder.State state) {
        this.bufferState = state;
    }
    
    static {
        EMPTY = new ChunkRenderData() {
            @Override
            protected void a(final BlockRenderLayer blockRenderLayer) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void markBufferInitialized(final BlockRenderLayer blockRenderLayer) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean a(final Direction direction1, final Direction direction2) {
                return false;
            }
        };
    }
}
