package net.minecraft.block;

import net.minecraft.util.DyeColor;
import net.minecraft.client.block.ColoredBlock;

public class StainedGlassBlock extends AbstractGlassBlock implements ColoredBlock
{
    private final DyeColor color;
    
    public StainedGlassBlock(final DyeColor dyeColor, final Settings settings) {
        super(settings);
        this.color = dyeColor;
    }
    
    @Override
    public DyeColor getColor() {
        return this.color;
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
