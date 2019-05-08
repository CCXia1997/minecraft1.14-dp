package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;

public class Slot
{
    private final int invSlot;
    public final Inventory inventory;
    public int id;
    public int xPosition;
    public int yPosition;
    
    public Slot(final Inventory inventory, final int invSlot, final int xPosition, final int yPosition) {
        this.inventory = inventory;
        this.invSlot = invSlot;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
    
    public void onStackChanged(final ItemStack originalItem, final ItemStack itemStack2) {
        final int integer3 = itemStack2.getAmount() - originalItem.getAmount();
        if (integer3 > 0) {
            this.onCrafted(itemStack2, integer3);
        }
    }
    
    protected void onCrafted(final ItemStack stack, final int amount) {
    }
    
    protected void onTake(final int amount) {
    }
    
    protected void onCrafted(final ItemStack stack) {
    }
    
    public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
        this.markDirty();
        return stack;
    }
    
    public boolean canInsert(final ItemStack stack) {
        return true;
    }
    
    public ItemStack getStack() {
        return this.inventory.getInvStack(this.invSlot);
    }
    
    public boolean hasStack() {
        return !this.getStack().isEmpty();
    }
    
    public void setStack(final ItemStack itemStack) {
        this.inventory.setInvStack(this.invSlot, itemStack);
        this.markDirty();
    }
    
    public void markDirty() {
        this.inventory.markDirty();
    }
    
    public int getMaxStackAmount() {
        return this.inventory.getInvMaxStackAmount();
    }
    
    public int getMaxStackAmount(final ItemStack itemStack) {
        return this.getMaxStackAmount();
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public String getBackgroundSprite() {
        return null;
    }
    
    public ItemStack takeStack(final int amount) {
        return this.inventory.takeInvStack(this.invSlot, amount);
    }
    
    public boolean canTakeItems(final PlayerEntity playerEntity) {
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean doDrawHoveringEffect() {
        return true;
    }
}
