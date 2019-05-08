package net.minecraft.container;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;

public class FurnaceOutputSlot extends Slot
{
    private final PlayerEntity player;
    private int amount;
    
    public FurnaceOutputSlot(final PlayerEntity player, final Inventory inventory, final int invSlot, final int xPosition, final int yPosition) {
        super(inventory, invSlot, xPosition, yPosition);
        this.player = player;
    }
    
    @Override
    public boolean canInsert(final ItemStack stack) {
        return false;
    }
    
    @Override
    public ItemStack takeStack(final int amount) {
        if (this.hasStack()) {
            this.amount += Math.min(amount, this.getStack().getAmount());
        }
        return super.takeStack(amount);
    }
    
    @Override
    public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
        this.onCrafted(stack);
        super.onTakeItem(player, stack);
        return stack;
    }
    
    @Override
    protected void onCrafted(final ItemStack stack, final int amount) {
        this.amount += amount;
        this.onCrafted(stack);
    }
    
    @Override
    protected void onCrafted(final ItemStack stack) {
        stack.onCrafted(this.player.world, this.player, this.amount);
        if (!this.player.world.isClient && this.inventory instanceof AbstractFurnaceBlockEntity) {
            ((AbstractFurnaceBlockEntity)this.inventory).dropExperience(this.player);
        }
        this.amount = 0;
    }
}
