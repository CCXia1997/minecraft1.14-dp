package net.minecraft.client.options;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.DefaultedList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import com.google.common.collect.ForwardingList;

@Environment(EnvType.CLIENT)
public class HotbarStorageEntry extends ForwardingList<ItemStack>
{
    private final DefaultedList<ItemStack> delegate;
    
    public HotbarStorageEntry() {
        this.delegate = DefaultedList.<ItemStack>create(PlayerInventory.getHotbarSize(), ItemStack.EMPTY);
    }
    
    @Override
    protected List<ItemStack> delegate() {
        return this.delegate;
    }
    
    public ListTag toListTag() {
        final ListTag listTag1 = new ListTag();
        for (final ItemStack itemStack3 : this.delegate()) {
            ((AbstractList<CompoundTag>)listTag1).add(itemStack3.toTag(new CompoundTag()));
        }
        return listTag1;
    }
    
    public void fromListTag(final ListTag listTag) {
        final List<ItemStack> list2 = this.delegate();
        for (int integer3 = 0; integer3 < list2.size(); ++integer3) {
            list2.set(integer3, ItemStack.fromTag(listTag.getCompoundTag(integer3)));
        }
    }
    
    @Override
    public boolean isEmpty() {
        for (final ItemStack itemStack2 : this.delegate()) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
