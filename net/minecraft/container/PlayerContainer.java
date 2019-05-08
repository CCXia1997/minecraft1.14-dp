package net.minecraft.container;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.CraftingInventory;

public class PlayerContainer extends CraftingContainer<CraftingInventory>
{
    private static final String[] EMPTY_ARMOR_SLOT_IDS;
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER;
    private final CraftingInventory invCrafting;
    private final CraftingResultInventory invCraftingResult;
    public final boolean local;
    private final PlayerEntity owner;
    
    public PlayerContainer(final PlayerInventory inventory, final boolean local, final PlayerEntity playerEntity) {
        super(null, 0);
        this.invCrafting = new CraftingInventory(this, 2, 2);
        this.invCraftingResult = new CraftingResultInventory();
        this.local = local;
        this.owner = playerEntity;
        this.addSlot(new CraftingResultSlot(inventory.player, this.invCrafting, this.invCraftingResult, 0, 154, 28));
        for (int integer4 = 0; integer4 < 2; ++integer4) {
            for (int integer5 = 0; integer5 < 2; ++integer5) {
                this.addSlot(new Slot(this.invCrafting, integer5 + integer4 * 2, 98 + integer5 * 18, 18 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 4; ++integer4) {
            final EquipmentSlot equipmentSlot5 = PlayerContainer.EQUIPMENT_SLOT_ORDER[integer4];
            this.addSlot(new Slot(inventory, 39 - integer4, 8, 8 + integer4 * 18) {
                @Override
                public int getMaxStackAmount() {
                    return 1;
                }
                
                @Override
                public boolean canInsert(final ItemStack stack) {
                    return equipmentSlot5 == MobEntity.getPreferredEquipmentSlot(stack);
                }
                
                @Override
                public boolean canTakeItems(final PlayerEntity playerEntity) {
                    final ItemStack itemStack2 = this.getStack();
                    return (itemStack2.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack2)) && super.canTakeItems(playerEntity);
                }
                
                @Nullable
                @Environment(EnvType.CLIENT)
                @Override
                public String getBackgroundSprite() {
                    return PlayerContainer.j()[equipmentSlot5.getEntitySlotId()];
                }
            });
        }
        for (int integer4 = 0; integer4 < 3; ++integer4) {
            for (int integer5 = 0; integer5 < 9; ++integer5) {
                this.addSlot(new Slot(inventory, integer5 + (integer4 + 1) * 9, 8 + integer5 * 18, 84 + integer4 * 18));
            }
        }
        for (int integer4 = 0; integer4 < 9; ++integer4) {
            this.addSlot(new Slot(inventory, integer4, 8 + integer4 * 18, 142));
        }
        this.addSlot(new Slot(inventory, 40, 77, 62) {
            @Nullable
            @Environment(EnvType.CLIENT)
            @Override
            public String getBackgroundSprite() {
                return "item/empty_armor_slot_shield";
            }
        });
    }
    
    @Override
    public void populateRecipeFinder(final RecipeFinder recipeFinder) {
        this.invCrafting.provideRecipeInputs(recipeFinder);
    }
    
    @Override
    public void clearCraftingSlots() {
        this.invCraftingResult.clear();
        this.invCrafting.clear();
    }
    
    @Override
    public boolean matches(final Recipe<? super CraftingInventory> recipe) {
        return recipe.matches(this.invCrafting, this.owner.world);
    }
    
    @Override
    public void onContentChanged(final Inventory inventory) {
        CraftingTableContainer.updateResult(this.syncId, this.owner.world, this.owner, this.invCrafting, this.invCraftingResult);
    }
    
    @Override
    public void close(final PlayerEntity player) {
        super.close(player);
        this.invCraftingResult.clear();
        if (player.world.isClient) {
            return;
        }
        this.dropInventory(player, player.world, this.invCrafting);
    }
    
    @Override
    public boolean canUse(final PlayerEntity player) {
        return true;
    }
    
    @Override
    public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
        ItemStack itemStack3 = ItemStack.EMPTY;
        final Slot slot4 = this.slotList.get(invSlot);
        if (slot4 != null && slot4.hasStack()) {
            final ItemStack itemStack4 = slot4.getStack();
            itemStack3 = itemStack4.copy();
            final EquipmentSlot equipmentSlot6 = MobEntity.getPreferredEquipmentSlot(itemStack3);
            if (invSlot == 0) {
                if (!this.insertItem(itemStack4, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack4, itemStack3);
            }
            else if (invSlot >= 1 && invSlot < 5) {
                if (!this.insertItem(itemStack4, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 5 && invSlot < 9) {
                if (!this.insertItem(itemStack4, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (equipmentSlot6.getType() == EquipmentSlot.Type.ARMOR && !this.slotList.get(8 - equipmentSlot6.getEntitySlotId()).hasStack()) {
                final int integer7 = 8 - equipmentSlot6.getEntitySlotId();
                if (!this.insertItem(itemStack4, integer7, integer7 + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (equipmentSlot6 == EquipmentSlot.HAND_OFF && !this.slotList.get(45).hasStack()) {
                if (!this.insertItem(itemStack4, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 9 && invSlot < 36) {
                if (!this.insertItem(itemStack4, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 36 && invSlot < 45) {
                if (!this.insertItem(itemStack4, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(itemStack4, 9, 45, false)) {
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
            final ItemStack itemStack5 = slot4.onTakeItem(player, itemStack4);
            if (invSlot == 0) {
                player.dropItem(itemStack5, false);
            }
        }
        return itemStack3;
    }
    
    @Override
    public boolean canInsertIntoSlot(final ItemStack stack, final Slot slot) {
        return slot.inventory != this.invCraftingResult && super.canInsertIntoSlot(stack, slot);
    }
    
    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }
    
    @Override
    public int getCraftingWidth() {
        return this.invCrafting.getWidth();
    }
    
    @Override
    public int getCraftingHeight() {
        return this.invCrafting.getHeight();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getCraftingSlotCount() {
        return 5;
    }
    
    @Environment(EnvType.CLIENT)
    static /* synthetic */ String[] j() {
        return PlayerContainer.EMPTY_ARMOR_SLOT_IDS;
    }
    
    static {
        EMPTY_ARMOR_SLOT_IDS = new String[] { "item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet" };
        EQUIPMENT_SLOT_ORDER = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    }
}
