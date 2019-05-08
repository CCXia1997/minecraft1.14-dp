package net.minecraft.item;

import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class CarrotOnAStickItem extends Item
{
    public CarrotOnAStickItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (world.isClient) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
        }
        if (player.hasVehicle() && player.getVehicle() instanceof PigEntity) {
            final PigEntity pigEntity5 = (PigEntity)player.getVehicle();
            if (itemStack4.getDurability() - itemStack4.getDamage() >= 7 && pigEntity5.dW()) {
                itemStack4.<PlayerEntity>applyDamage(7, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
                if (itemStack4.isEmpty()) {
                    final ItemStack itemStack5 = new ItemStack(Items.kY);
                    itemStack5.setTag(itemStack4.getTag());
                    return new TypedActionResult<ItemStack>(ActionResult.a, itemStack5);
                }
                return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
            }
        }
        player.incrementStat(Stats.c.getOrCreateStat(this));
        return new TypedActionResult<ItemStack>(ActionResult.PASS, itemStack4);
    }
}
