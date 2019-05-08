package net.minecraft.item;

import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class EmptyMapItem extends MapItem
{
    public EmptyMapItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = FilledMapItem.createMap(world, MathHelper.floor(player.x), MathHelper.floor(player.z), (byte)0, true, false);
        final ItemStack itemStack5 = player.getStackInHand(hand);
        if (!player.abilities.creativeMode) {
            itemStack5.subtractAmount(1);
        }
        if (itemStack5.isEmpty()) {
            return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
        }
        if (!player.inventory.insertStack(itemStack4.copy())) {
            player.dropItem(itemStack4, false);
        }
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.a, itemStack5);
    }
}
