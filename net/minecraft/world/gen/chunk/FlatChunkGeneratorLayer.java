package net.minecraft.world.gen.chunk;

import net.minecraft.util.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class FlatChunkGeneratorLayer
{
    private final BlockState blockState;
    private final int thickness;
    private int startY;
    
    public FlatChunkGeneratorLayer(final int thickness, final Block block) {
        this.thickness = thickness;
        this.blockState = block.getDefaultState();
    }
    
    public int getThickness() {
        return this.thickness;
    }
    
    public BlockState getBlockState() {
        return this.blockState;
    }
    
    public int getStartY() {
        return this.startY;
    }
    
    public void setStartY(final int startY) {
        this.startY = startY;
    }
    
    @Override
    public String toString() {
        return ((this.thickness > 1) ? (this.thickness + "*") : "") + Registry.BLOCK.getId(this.blockState.getBlock());
    }
}
