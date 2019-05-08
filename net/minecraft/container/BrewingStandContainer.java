package net.minecraft.container;

import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.potion.PotionUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;

public class BrewingStandContainer extends Container
{
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final Slot ingredientSlot;
    
    public BrewingStandContainer(final int syncId, final PlayerInventory playerInventory) {
        this(syncId, playerInventory, new BasicInventory(5), new ArrayPropertyDelegate(2));
    }
    
    public BrewingStandContainer(final int integer, final PlayerInventory playerInventory, final Inventory inventory, final PropertyDelegate propertyDelegate) {
        super(ContainerType.BREWING_STAND, integer);
        Container.checkContainerSize(inventory, 5);
        Container.checkContainerDataCount(propertyDelegate, 2);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addSlot(new SlotPotion(inventory, 0, 56, 51));
        this.addSlot(new SlotPotion(inventory, 1, 79, 58));
        this.addSlot(new SlotPotion(inventory, 2, 102, 51));
        this.ingredientSlot = this.addSlot(new SlotIngredient(inventory, 3, 79, 17));
        this.addSlot(new SlotFuel(inventory, 4, 17, 17));
        this.addProperties(propertyDelegate);
        for (int integer2 = 0; integer2 < 3; ++integer2) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.addSlot(new Slot(playerInventory, integer3 + integer2 * 9 + 9, 8 + integer3 * 18, 84 + integer2 * 18));
            }
        }
        for (int integer2 = 0; integer2 < 9; ++integer2) {
            this.addSlot(new Slot(playerInventory, integer2, 8 + integer2 * 18, 142));
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
            if ((invSlot >= 0 && invSlot <= 2) || invSlot == 3 || invSlot == 4) {
                if (!this.insertItem(itemStack4, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack4, itemStack3);
            }
            else if (this.ingredientSlot.canInsert(itemStack4)) {
                if (!this.insertItem(itemStack4, 3, 4, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (SlotPotion.matches(itemStack3) && itemStack3.getAmount() == 1) {
                if (!this.insertItem(itemStack4, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (SlotFuel.matches(itemStack3)) {
                if (!this.insertItem(itemStack4, 4, 5, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 5 && invSlot < 32) {
                if (!this.insertItem(itemStack4, 32, 41, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 32 && invSlot < 41) {
                if (!this.insertItem(itemStack4, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(itemStack4, 5, 41, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack4.isEmpty()) {
                slot4.setStack(ItemStack.EMPTY);
            }
            else {
                slot4.markDirty();
            }
            if (itemStack4.getAmount() == itemStack3.getAmount()) {
                return ItemStack.EMPTY;
            }
            slot4.onTakeItem(player, itemStack4);
        }
        return itemStack3;
    }
    
    @Environment(EnvType.CLIENT)
    public int getFuel() {
        return this.propertyDelegate.get(1);
    }
    
    @Environment(EnvType.CLIENT)
    public int getBrewTime() {
        return this.propertyDelegate.get(0);
    }
    
    static class SlotPotion extends Slot
    {
        public SlotPotion(final Inventory inventory, final int invSlot, final int xPosition, final int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }
        
        @Override
        public boolean canInsert(final ItemStack stack) {
            return matches(stack);
        }
        
        @Override
        public int getMaxStackAmount() {
            return 1;
        }
        
        @Override
        public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
            final Potion potion3 = PotionUtil.getPotion(stack);
            if (player instanceof ServerPlayerEntity) {
                Criterions.BREWED_POTION.handle((ServerPlayerEntity)player, potion3);
            }
            super.onTakeItem(player, stack);
            return stack;
        }
        
        public static boolean matches(final ItemStack itemStack) {
            final Item item2 = itemStack.getItem();
            return item2 == Items.ml || item2 == Items.oS || item2 == Items.oV || item2 == Items.mm;
        }
    }
    
    static class SlotIngredient extends Slot
    {
        public SlotIngredient(final Inventory inventory, final int invSlot, final int xPosition, final int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }
        
        @Override
        public boolean canInsert(final ItemStack stack) {
            return BrewingRecipeRegistry.isValidIngredient(stack);
        }
        
        @Override
        public int getMaxStackAmount() {
            return 64;
        }
    }
    
    static class SlotFuel extends Slot
    {
        public SlotFuel(final Inventory inventory, final int invSlot, final int xPosition, final int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }
        
        @Override
        public boolean canInsert(final ItemStack stack) {
            return matches(stack);
        }
        
        public static boolean matches(final ItemStack itemStack) {
            return itemStack.getItem() == Items.mp;
        }
        
        @Override
        public int getMaxStackAmount() {
            return 64;
        }
    }
}
