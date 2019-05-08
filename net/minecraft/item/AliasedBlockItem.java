package net.minecraft.item;

import net.minecraft.block.Block;

public class AliasedBlockItem extends BlockItem
{
    public AliasedBlockItem(final Block block, final Settings settings) {
        super(block, settings);
    }
    
    @Override
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }
}
