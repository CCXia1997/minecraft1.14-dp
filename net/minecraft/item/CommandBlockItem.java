package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class CommandBlockItem extends BlockItem
{
    public CommandBlockItem(final Block block, final Settings settings) {
        super(block, settings);
    }
    
    @Nullable
    @Override
    protected BlockState getBlockState(final ItemPlacementContext itemPlacementContext) {
        final PlayerEntity playerEntity2 = itemPlacementContext.getPlayer();
        return (playerEntity2 == null || playerEntity2.isCreativeLevelTwoOp()) ? super.getBlockState(itemPlacementContext) : null;
    }
}
