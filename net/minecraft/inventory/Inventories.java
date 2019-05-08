package net.minecraft.inventory;

import java.util.AbstractList;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import java.util.List;

public class Inventories
{
    public static ItemStack splitStack(final List<ItemStack> stacks, final int slot, final int amount) {
        if (slot < 0 || slot >= stacks.size() || stacks.get(slot).isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        return stacks.get(slot).split(amount);
    }
    
    public static ItemStack removeStack(final List<ItemStack> stacks, final int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            return ItemStack.EMPTY;
        }
        return stacks.set(slot, ItemStack.EMPTY);
    }
    
    public static CompoundTag toTag(final CompoundTag tag, final DefaultedList<ItemStack> stacks) {
        return toTag(tag, stacks, true);
    }
    
    public static CompoundTag toTag(final CompoundTag tag, final DefaultedList<ItemStack> stacks, final boolean setIfEmpty) {
        final ListTag listTag4 = new ListTag();
        for (int integer5 = 0; integer5 < stacks.size(); ++integer5) {
            final ItemStack itemStack6 = stacks.get(integer5);
            if (!itemStack6.isEmpty()) {
                final CompoundTag compoundTag7 = new CompoundTag();
                compoundTag7.putByte("Slot", (byte)integer5);
                itemStack6.toTag(compoundTag7);
                ((AbstractList<CompoundTag>)listTag4).add(compoundTag7);
            }
        }
        if (!listTag4.isEmpty() || setIfEmpty) {
            tag.put("Items", listTag4);
        }
        return tag;
    }
    
    public static void fromTag(final CompoundTag tag, final DefaultedList<ItemStack> stacks) {
        final ListTag listTag3 = tag.getList("Items", 10);
        for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
            final CompoundTag compoundTag5 = listTag3.getCompoundTag(integer4);
            final int integer5 = compoundTag5.getByte("Slot") & 0xFF;
            if (integer5 >= 0 && integer5 < stacks.size()) {
                stacks.set(integer5, ItemStack.fromTag(compoundTag5));
            }
        }
    }
}
