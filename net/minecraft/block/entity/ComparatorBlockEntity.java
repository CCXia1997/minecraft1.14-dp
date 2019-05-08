package net.minecraft.block.entity;

import net.minecraft.nbt.CompoundTag;

public class ComparatorBlockEntity extends BlockEntity
{
    private int outputSignal;
    
    public ComparatorBlockEntity() {
        super(BlockEntityType.COMPARATOR);
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putInt("OutputSignal", this.outputSignal);
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.outputSignal = compoundTag.getInt("OutputSignal");
    }
    
    public int getOutputSignal() {
        return this.outputSignal;
    }
    
    public void setOutputSignal(final int integer) {
        this.outputSignal = integer;
    }
}
