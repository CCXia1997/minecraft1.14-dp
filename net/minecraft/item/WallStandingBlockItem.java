package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class WallStandingBlockItem extends BlockItem
{
    protected final Block wallBlock;
    
    public WallStandingBlockItem(final Block standingBlock, final Block wallBlock, final Settings settings) {
        super(standingBlock, settings);
        this.wallBlock = wallBlock;
    }
    
    @Nullable
    @Override
    protected BlockState getBlockState(final ItemPlacementContext itemPlacementContext) {
        final BlockState blockState2 = this.wallBlock.getPlacementState(itemPlacementContext);
        BlockState blockState3 = null;
        final ViewableWorld viewableWorld4 = itemPlacementContext.getWorld();
        final BlockPos blockPos5 = itemPlacementContext.getBlockPos();
        for (final Direction direction9 : itemPlacementContext.getPlacementFacings()) {
            if (direction9 != Direction.UP) {
                final BlockState blockState4 = (direction9 == Direction.DOWN) ? this.getBlock().getPlacementState(itemPlacementContext) : blockState2;
                if (blockState4 != null && blockState4.canPlaceAt(viewableWorld4, blockPos5)) {
                    blockState3 = blockState4;
                    break;
                }
            }
        }
        return (blockState3 != null && viewableWorld4.canPlace(blockState3, blockPos5, VerticalEntityPosition.minValue())) ? blockState3 : null;
    }
    
    @Override
    public void registerBlockItemMap(final Map<Block, Item> map, final Item item) {
        super.registerBlockItemMap(map, item);
        map.put(this.wallBlock, item);
    }
}
