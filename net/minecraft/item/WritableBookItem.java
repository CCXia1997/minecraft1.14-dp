package net.minecraft.item;

import net.minecraft.nbt.ListTag;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;

public class WritableBookItem extends Item
{
    public WritableBookItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        final BlockState blockState4 = world2.getBlockState(blockPos3);
        if (blockState4.getBlock() == Blocks.lQ) {
            return LecternBlock.putBookIfAbsent(world2, blockPos3, blockState4, usageContext.getItemStack()) ? ActionResult.a : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        player.openEditBookScreen(itemStack4, hand);
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
    }
    
    public static boolean isValidBook(@Nullable final CompoundTag tag) {
        if (tag == null) {
            return false;
        }
        if (!tag.containsKey("pages", 9)) {
            return false;
        }
        final ListTag listTag2 = tag.getList("pages", 8);
        for (int integer3 = 0; integer3 < listTag2.size(); ++integer3) {
            final String string4 = listTag2.getString(integer3);
            if (string4.length() > 32767) {
                return false;
            }
        }
        return true;
    }
}
