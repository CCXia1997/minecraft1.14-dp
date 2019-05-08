package net.minecraft.item;

import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.entity.LivingEntity;
import java.util.function.Predicate;

public abstract class BaseBowItem extends Item
{
    public static final Predicate<ItemStack> IS_BOW_PROJECTILE;
    public static final Predicate<ItemStack> IS_CROSSBOW_PROJECTILE;
    
    public BaseBowItem(final Settings settings) {
        super(settings);
    }
    
    public Predicate<ItemStack> getHeldProjectilePredicate() {
        return this.getInventoryProjectilePredicate();
    }
    
    public abstract Predicate<ItemStack> getInventoryProjectilePredicate();
    
    public static ItemStack getItemHeld(final LivingEntity entity, final Predicate<ItemStack> predicate) {
        if (predicate.test(entity.getStackInHand(Hand.b))) {
            return entity.getStackInHand(Hand.b);
        }
        if (predicate.test(entity.getStackInHand(Hand.a))) {
            return entity.getStackInHand(Hand.a);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public int getEnchantability() {
        return 1;
    }
    
    static {
        IS_BOW_PROJECTILE = (itemStack -> itemStack.getItem().matches(ItemTags.M));
        IS_CROSSBOW_PROJECTILE = BaseBowItem.IS_BOW_PROJECTILE.or(itemStack -> itemStack.getItem() == Items.nX);
    }
}
