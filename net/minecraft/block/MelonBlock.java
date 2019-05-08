package net.minecraft.block;

public class MelonBlock extends GourdBlock
{
    protected MelonBlock(final Settings settings) {
        super(settings);
    }
    
    @Override
    public StemBlock getStem() {
        return (StemBlock)Blocks.dG;
    }
    
    @Override
    public AttachedStemBlock getAttachedStem() {
        return (AttachedStemBlock)Blocks.dE;
    }
}
