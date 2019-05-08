package net.minecraft.block.entity;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;

public class JukeboxBlockEntity extends BlockEntity implements Clearable
{
    private ItemStack record;
    
    public JukeboxBlockEntity() {
        super(BlockEntityType.JUKEBOX);
        this.record = ItemStack.EMPTY;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        if (compoundTag.containsKey("RecordItem", 10)) {
            this.setRecord(ItemStack.fromTag(compoundTag.getCompound("RecordItem")));
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (!this.getRecord().isEmpty()) {
            compoundTag.put("RecordItem", this.getRecord().toTag(new CompoundTag()));
        }
        return compoundTag;
    }
    
    public ItemStack getRecord() {
        return this.record;
    }
    
    public void setRecord(final ItemStack itemStack) {
        this.record = itemStack;
        this.markDirty();
    }
    
    @Override
    public void clear() {
        this.setRecord(ItemStack.EMPTY);
    }
}
