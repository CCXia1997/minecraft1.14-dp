package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class BedItem extends BlockItem
{
    public BedItem(final Block block, final Settings settings) {
        super(block, settings);
    }
    
    @Override
    protected boolean setBlockState(final ItemPlacementContext itemPlacementContext, final BlockState blockState) {
        return itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos(), blockState, 26);
    }
}
