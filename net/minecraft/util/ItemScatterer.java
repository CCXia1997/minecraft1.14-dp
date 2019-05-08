package net.minecraft.util;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;

public class ItemScatterer
{
    private static final Random RANDOM;
    
    public static void spawn(final World world, final BlockPos blockPos, final Inventory inventory) {
        spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), inventory);
    }
    
    public static void spawn(final World world, final Entity entity, final Inventory inventory) {
        spawn(world, entity.x, entity.y, entity.z, inventory);
    }
    
    private static void spawn(final World world, final double x, final double y, final double z, final Inventory inventory) {
        for (int integer9 = 0; integer9 < inventory.getInvSize(); ++integer9) {
            spawn(world, x, y, z, inventory.getInvStack(integer9));
        }
    }
    
    public static void spawn(final World world, final BlockPos pos, final DefaultedList<ItemStack> items) {
        items.forEach(itemStack -> spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack));
    }
    
    public static void spawn(final World world, final double x, final double y, final double z, final ItemStack item) {
        final double double9 = EntityType.ITEM.getWidth();
        final double double10 = 1.0 - double9;
        final double double11 = double9 / 2.0;
        final double double12 = Math.floor(x) + ItemScatterer.RANDOM.nextDouble() * double10 + double11;
        final double double13 = Math.floor(y) + ItemScatterer.RANDOM.nextDouble() * double10;
        final double double14 = Math.floor(z) + ItemScatterer.RANDOM.nextDouble() * double10 + double11;
        while (!item.isEmpty()) {
            final ItemEntity itemEntity21 = new ItemEntity(world, double12, double13, double14, item.split(ItemScatterer.RANDOM.nextInt(21) + 10));
            final float float22 = 0.05f;
            itemEntity21.setVelocity(ItemScatterer.RANDOM.nextGaussian() * 0.05000000074505806, ItemScatterer.RANDOM.nextGaussian() * 0.05000000074505806 + 0.20000000298023224, ItemScatterer.RANDOM.nextGaussian() * 0.05000000074505806);
            world.spawnEntity(itemEntity21);
        }
    }
    
    static {
        RANDOM = new Random();
    }
}
