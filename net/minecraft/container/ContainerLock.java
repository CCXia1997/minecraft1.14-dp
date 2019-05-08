package net.minecraft.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ContainerLock
{
    public static final ContainerLock NONE;
    private final String key;
    
    public ContainerLock(final String string) {
        this.key = string;
    }
    
    public boolean isEmpty(final ItemStack itemStack) {
        return this.key.isEmpty() || (!itemStack.isEmpty() && itemStack.hasDisplayName() && this.key.equals(itemStack.getDisplayName().getString()));
    }
    
    public void serialize(final CompoundTag compoundTag) {
        if (!this.key.isEmpty()) {
            compoundTag.putString("Lock", this.key);
        }
    }
    
    public static ContainerLock deserialize(final CompoundTag tag) {
        if (tag.containsKey("Lock", 8)) {
            return new ContainerLock(tag.getString("Lock"));
        }
        return ContainerLock.NONE;
    }
    
    static {
        NONE = new ContainerLock("");
    }
}
