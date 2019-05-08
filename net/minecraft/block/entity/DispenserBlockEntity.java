package net.minecraft.block.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import java.util.Random;

public class DispenserBlockEntity extends LootableContainerBlockEntity
{
    private static final Random RANDOM;
    private DefaultedList<ItemStack> inventory;
    
    protected DispenserBlockEntity(final BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
        this.inventory = DefaultedList.<ItemStack>create(9, ItemStack.EMPTY);
    }
    
    public DispenserBlockEntity() {
        this(BlockEntityType.DISPENSER);
    }
    
    @Override
    public int getInvSize() {
        return 9;
    }
    
    @Override
    public boolean isInvEmpty() {
        for (final ItemStack itemStack2 : this.inventory) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public int chooseNonEmptySlot() {
        this.checkLootInteraction(null);
        int integer1 = -1;
        int integer2 = 1;
        for (int integer3 = 0; integer3 < this.inventory.size(); ++integer3) {
            if (!this.inventory.get(integer3).isEmpty() && DispenserBlockEntity.RANDOM.nextInt(integer2++) == 0) {
                integer1 = integer3;
            }
        }
        return integer1;
    }
    
    public int addToFirstFreeSlot(final ItemStack stack) {
        for (int integer2 = 0; integer2 < this.inventory.size(); ++integer2) {
            if (this.inventory.get(integer2).isEmpty()) {
                this.setInvStack(integer2, stack);
                return integer2;
            }
        }
        return -1;
    }
    
    @Override
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.dispenser", new Object[0]);
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.<ItemStack>create(this.getInvSize(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(compoundTag)) {
            Inventories.fromTag(compoundTag, this.inventory);
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (!this.serializeLootTable(compoundTag)) {
            Inventories.toTag(compoundTag, this.inventory);
        }
        return compoundTag;
    }
    
    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }
    
    @Override
    protected void setInvStackList(final DefaultedList<ItemStack> list) {
        this.inventory = list;
    }
    
    @Override
    protected Container createContainer(final int integer, final PlayerInventory playerInventory) {
        return new Generic3x3Container(integer, playerInventory, this);
    }
    
    static {
        RANDOM = new Random();
    }
}
