package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DoubleInventory implements Inventory
{
    private final Inventory first;
    private final Inventory second;
    
    public DoubleInventory(Inventory inventory1, Inventory inventory2) {
        if (inventory1 == null) {
            inventory1 = inventory2;
        }
        if (inventory2 == null) {
            inventory2 = inventory1;
        }
        this.first = inventory1;
        this.second = inventory2;
    }
    
    @Override
    public int getInvSize() {
        return this.first.getInvSize() + this.second.getInvSize();
    }
    
    @Override
    public boolean isInvEmpty() {
        return this.first.isInvEmpty() && this.second.isInvEmpty();
    }
    
    public boolean isPart(final Inventory inventory) {
        return this.first == inventory || this.second == inventory;
    }
    
    @Override
    public ItemStack getInvStack(final int slot) {
        if (slot >= this.first.getInvSize()) {
            return this.second.getInvStack(slot - this.first.getInvSize());
        }
        return this.first.getInvStack(slot);
    }
    
    @Override
    public ItemStack takeInvStack(final int slot, final int integer2) {
        if (slot >= this.first.getInvSize()) {
            return this.second.takeInvStack(slot - this.first.getInvSize(), integer2);
        }
        return this.first.takeInvStack(slot, integer2);
    }
    
    @Override
    public ItemStack removeInvStack(final int slot) {
        if (slot >= this.first.getInvSize()) {
            return this.second.removeInvStack(slot - this.first.getInvSize());
        }
        return this.first.removeInvStack(slot);
    }
    
    @Override
    public void setInvStack(final int slot, final ItemStack itemStack) {
        if (slot >= this.first.getInvSize()) {
            this.second.setInvStack(slot - this.first.getInvSize(), itemStack);
        }
        else {
            this.first.setInvStack(slot, itemStack);
        }
    }
    
    @Override
    public int getInvMaxStackAmount() {
        return this.first.getInvMaxStackAmount();
    }
    
    @Override
    public void markDirty() {
        this.first.markDirty();
        this.second.markDirty();
    }
    
    @Override
    public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
        return this.first.canPlayerUseInv(playerEntity) && this.second.canPlayerUseInv(playerEntity);
    }
    
    @Override
    public void onInvOpen(final PlayerEntity playerEntity) {
        this.first.onInvOpen(playerEntity);
        this.second.onInvOpen(playerEntity);
    }
    
    @Override
    public void onInvClose(final PlayerEntity playerEntity) {
        this.first.onInvClose(playerEntity);
        this.second.onInvClose(playerEntity);
    }
    
    @Override
    public boolean isValidInvStack(final int slot, final ItemStack itemStack) {
        if (slot >= this.first.getInvSize()) {
            return this.second.isValidInvStack(slot - this.first.getInvSize(), itemStack);
        }
        return this.first.isValidInvStack(slot, itemStack);
    }
    
    @Override
    public void clear() {
        this.first.clear();
        this.second.clear();
    }
}
