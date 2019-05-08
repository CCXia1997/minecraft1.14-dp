package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;

public interface DyeableItem
{
    default boolean hasColor(final ItemStack itemStack) {
        final CompoundTag compoundTag2 = itemStack.getSubCompoundTag("display");
        return compoundTag2 != null && compoundTag2.containsKey("color", 99);
    }
    
    default int getColor(final ItemStack itemStack) {
        final CompoundTag compoundTag2 = itemStack.getSubCompoundTag("display");
        if (compoundTag2 != null && compoundTag2.containsKey("color", 99)) {
            return compoundTag2.getInt("color");
        }
        return 10511680;
    }
    
    default void removeColor(final ItemStack itemStack) {
        final CompoundTag compoundTag2 = itemStack.getSubCompoundTag("display");
        if (compoundTag2 != null && compoundTag2.containsKey("color")) {
            compoundTag2.remove("color");
        }
    }
    
    default void setColor(final ItemStack itemStack, final int integer) {
        itemStack.getOrCreateSubCompoundTag("display").putInt("color", integer);
    }
    
    default ItemStack applyDyes(final ItemStack itemStack, final List<DyeItem> list) {
        ItemStack itemStack2 = ItemStack.EMPTY;
        final int[] arr4 = new int[3];
        int integer5 = 0;
        int integer6 = 0;
        DyeableItem dyeableItem7 = null;
        final Item item8 = itemStack.getItem();
        if (item8 instanceof DyeableItem) {
            dyeableItem7 = (DyeableItem)item8;
            itemStack2 = itemStack.copy();
            itemStack2.setAmount(1);
            if (dyeableItem7.hasColor(itemStack)) {
                final int integer7 = dyeableItem7.getColor(itemStack2);
                final float float10 = (integer7 >> 16 & 0xFF) / 255.0f;
                final float float11 = (integer7 >> 8 & 0xFF) / 255.0f;
                final float float12 = (integer7 & 0xFF) / 255.0f;
                integer5 += (int)(Math.max(float10, Math.max(float11, float12)) * 255.0f);
                final int[] array = arr4;
                final int n = 0;
                array[n] += (int)(float10 * 255.0f);
                final int[] array2 = arr4;
                final int n2 = 1;
                array2[n2] += (int)(float11 * 255.0f);
                final int[] array3 = arr4;
                final int n3 = 2;
                array3[n3] += (int)(float12 * 255.0f);
                ++integer6;
            }
            for (final DyeItem dyeItem10 : list) {
                final float[] arr5 = dyeItem10.getColor().getColorComponents();
                final int integer8 = (int)(arr5[0] * 255.0f);
                final int integer9 = (int)(arr5[1] * 255.0f);
                final int integer10 = (int)(arr5[2] * 255.0f);
                integer5 += Math.max(integer8, Math.max(integer9, integer10));
                final int[] array4 = arr4;
                final int n4 = 0;
                array4[n4] += integer8;
                final int[] array5 = arr4;
                final int n5 = 1;
                array5[n5] += integer9;
                final int[] array6 = arr4;
                final int n6 = 2;
                array6[n6] += integer10;
                ++integer6;
            }
        }
        if (dyeableItem7 == null) {
            return ItemStack.EMPTY;
        }
        int integer7 = arr4[0] / integer6;
        int integer11 = arr4[1] / integer6;
        int integer12 = arr4[2] / integer6;
        final float float12 = integer5 / (float)integer6;
        final float float13 = (float)Math.max(integer7, Math.max(integer11, integer12));
        integer7 = (int)(integer7 * float12 / float13);
        integer11 = (int)(integer11 * float12 / float13);
        integer12 = (int)(integer12 * float12 / float13);
        int integer10 = integer7;
        integer10 = (integer10 << 8) + integer11;
        integer10 = (integer10 << 8) + integer12;
        dyeableItem7.setColor(itemStack2, integer10);
        return itemStack2;
    }
}
