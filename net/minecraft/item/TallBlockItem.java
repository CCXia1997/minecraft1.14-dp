package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class TallBlockItem extends BlockItem
{
    public TallBlockItem(final Block block, final Settings settings) {
        super(block, settings);
    }
    
    @Override
    protected boolean setBlockState(final ItemPlacementContext itemPlacementContext, final BlockState blockState) {
        itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos().up(), Blocks.AIR.getDefaultState(), 27);
        return super.setBlockState(itemPlacementContext, blockState);
    }
}
