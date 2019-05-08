package net.minecraft.block;

public class GlassBlock extends AbstractGlassBlock
{
    public GlassBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
