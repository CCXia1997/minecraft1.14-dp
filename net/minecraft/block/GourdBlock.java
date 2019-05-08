package net.minecraft.block;

public abstract class GourdBlock extends Block
{
    public GourdBlock(final Settings settings) {
        super(settings);
    }
    
    public abstract StemBlock getStem();
    
    public abstract AttachedStemBlock getAttachedStem();
}
