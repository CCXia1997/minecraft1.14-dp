package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

public class GenericContainer extends Container
{
    private final Inventory inventory;
    private final int rows;
    
    private GenericContainer(final ContainerType<?> containerType, final int integer2, final PlayerInventory playerInventory, final int integer4) {
        this(containerType, integer2, playerInventory, new BasicInventory(9 * integer4), integer4);
    }
    
    public static GenericContainer createGeneric9x1(final int syncId, final PlayerInventory playerInventory) {
        return new GenericContainer(ContainerType.a, syncId, playerInventory, 1);
    }
    
    public static GenericContainer createGeneric9x2(final int syncId, final PlayerInventory playerInventory) {
        return new GenericContainer(ContainerType.b, syncId, playerInventory, 2);
    }
    
    public static GenericContainer createGeneric9x3(final int syncId, final PlayerInventory playerInventory) {
        return new GenericContainer(ContainerType.GENERIC_9X3, syncId, playerInventory, 3);
    }
    
    public static GenericContainer createGeneric9x4(final int syncId, final PlayerInventory playerInventory) {
        return new GenericContainer(ContainerType.d, syncId, playerInventory, 4);
    }
    
    public static GenericContainer createGeneric9x5(final int syncId, final PlayerInventory playerInventory) {
        return new GenericContainer(ContainerType.e, syncId, playerInventory, 5);
    }
    
    public static GenericContainer createGeneric9x6(final int syncId, final PlayerInventory playerInventory) {
        return new GenericContainer(ContainerType.GENERIC_9X6, syncId, playerInventory, 6);
    }
    
    public static GenericContainer createGeneric9x3(final int syncId, final PlayerInventory playerInventory, final Inventory inventory) {
        return new GenericContainer(ContainerType.GENERIC_9X3, syncId, playerInventory, inventory, 3);
    }
    
    public static GenericContainer createGeneric9x6(final int syncId, final PlayerInventory playerInventory, final Inventory inventory) {
        return new GenericContainer(ContainerType.GENERIC_9X6, syncId, playerInventory, inventory, 6);
    }
    
    public GenericContainer(final ContainerType<?> containerType, final int syncId, final PlayerInventory playerInventory, final Inventory inventory, final int rows) {
        super(containerType, syncId);
        Container.checkContainerSize(inventory, rows * 9);
        this.inventory = inventory;
        this.rows = rows;
        inventory.onInvOpen(playerInventory.player);
        final int integer6 = (this.rows - 4) * 18;
        for (int integer7 = 0; integer7 < this.rows; ++integer7) {
            for (int integer8 = 0; integer8 < 9; ++integer8) {
                this.addSlot(new Slot(inventory, integer8 + integer7 * 9, 8 + integer8 * 18, 18 + integer7 * 18));
            }
        }
        for (int integer7 = 0; integer7 < 3; ++integer7) {
            for (int integer8 = 0; integer8 < 9; ++integer8) {
                this.addSlot(new Slot(playerInventory, integer8 + integer7 * 9 + 9, 8 + integer8 * 18, 103 + integer7 * 18 + integer6));
            }
        }
        for (int integer7 = 0; integer7 < 9; ++integer7) {
            this.addSlot(new Slot(playerInventory, integer7, 8 + integer7 * 18, 161 + integer6));
        }
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return this.inventory.canPlayerUseInv(player);
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            if (invSlot < this.rows * 9) {
                if (!this.insertItem(itemStack4, this.rows * 9, this.slotList.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(itemStack4, 0, this.rows * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack4.isEmpty()) {
                slot4.setStack(ItemStack.EMPTY);
            }
            else {
                slot4.markDirty();
            }
        }
        return itemStack3;
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.inventory.onInvClose(player);
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
    
    @Environment(EnvType.CLIENT)
    public int getRows() {
        return this.rows;
    }
}
