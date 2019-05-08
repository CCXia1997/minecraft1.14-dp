package net.minecraft.item;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.EntityType;

public class ItemFrameItem extends DecorationItem
{
    public ItemFrameItem(final Settings settings) {
        super(EntityType.ITEM_FRAME, settings);
    }
    
    @Override
    protected boolean canPlaceOn(final PlayerEntity player, final Direction facing, final ItemStack stack, final BlockPos pos) {
        return !World.isHeightInvalid(pos) && player.canPlaceOn(pos, facing, stack);
    }
}
