package net.minecraft.client.render.chunk;

import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.BufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockLayeredBufferBuilder
{
    private final BufferBuilder[] layerBuilders;
    
    public BlockLayeredBufferBuilder() {
        (this.layerBuilders = new BufferBuilder[BlockRenderLayer.values().length])[BlockRenderLayer.SOLID.ordinal()] = new BufferBuilder(2097152);
        this.layerBuilders[BlockRenderLayer.CUTOUT.ordinal()] = new BufferBuilder(131072);
        this.layerBuilders[BlockRenderLayer.MIPPED_CUTOUT.ordinal()] = new BufferBuilder(131072);
        this.layerBuilders[BlockRenderLayer.TRANSLUCENT.ordinal()] = new BufferBuilder(262144);
    }
    
    public BufferBuilder get(final BlockRenderLayer blockRenderLayer) {
        return this.layerBuilders[blockRenderLayer.ordinal()];
    }
    
    public BufferBuilder get(final int integer) {
        return this.layerBuilders[integer];
    }
}
