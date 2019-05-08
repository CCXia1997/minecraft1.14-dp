package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.Identifier;

public class ElytraItem extends Item
{
    public ElytraItem(final Settings settings) {
        super(settings);
        this.addProperty(new Identifier("broken"), (itemStack, world, livingEntity) -> isUsable(itemStack) ? 0.0f : 1.0f);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }
    
    public static boolean isUsable(final ItemStack stack) {
        return stack.getDamage() < stack.getDurability() - 1;
    }
    
    @Override
    public boolean canRepair(final ItemStack tool, final ItemStack ingredient) {
        return ingredient.getItem() == Items.pv;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack4 = player.getStackInHand(hand);
        final EquipmentSlot equipmentSlot5 = MobEntity.getPreferredEquipmentSlot(itemStack4);
        final ItemStack itemStack5 = player.getEquippedStack(equipmentSlot5);
        if (itemStack5.isEmpty()) {
            player.setEquippedStack(equipmentSlot5, itemStack4.copy());
            itemStack4.setAmount(0);
            return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
        }
        return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
    }
}
