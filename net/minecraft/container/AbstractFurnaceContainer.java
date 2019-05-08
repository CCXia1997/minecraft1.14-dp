package net.minecraft.container;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.FurnaceInputSlotFiller;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import net.minecraft.inventory.Inventory;

public abstract class AbstractFurnaceContainer extends CraftingContainer<Inventory>
{
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;
    private final RecipeType<? extends CookingRecipe> recipeType;
    
    protected AbstractFurnaceContainer(final ContainerType<?> containerType, final RecipeType<? extends CookingRecipe> recipeType, final int syncId, final PlayerInventory playerInventory) {
        this(containerType, recipeType, syncId, playerInventory, new BasicInventory(3), new ArrayPropertyDelegate(4));
    }
    
    protected AbstractFurnaceContainer(final ContainerType<?> containerType, final RecipeType<? extends CookingRecipe> recipeType, final int syncId, final PlayerInventory playerInventory, final Inventory inventory, final PropertyDelegate propertyDelegate) {
        super(containerType, syncId);
        this.recipeType = recipeType;
        Container.checkContainerSize(inventory, 3);
        Container.checkContainerDataCount(propertyDelegate, 4);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.world;
        this.addSlot(new Slot(inventory, 0, 56, 17));
        this.addSlot(new FurnaceFuelSlot(this, inventory, 1, 56, 53));
        this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 2, 116, 35));
        for (int integer7 = 0; integer7 < 3; ++integer7) {
            for (int integer8 = 0; integer8 < 9; ++integer8) {
                this.addSlot(new Slot(playerInventory, integer8 + integer7 * 9 + 9, 8 + integer8 * 18, 84 + integer7 * 18));
            }
        }
        for (int integer7 = 0; integer7 < 9; ++integer7) {
            this.addSlot(new Slot(playerInventory, integer7, 8 + integer7 * 18, 142));
        }
        this.addProperties(propertyDelegate);
    }
    
    @Override
    public void populateRecipeFinder(final RecipeFinder recipeFinder) {
        if (this.inventory instanceof RecipeInputProvider) {
            ((RecipeInputProvider)this.inventory).provideRecipeInputs(recipeFinder);
        }
    }
    
    @Override
    public void clearCraftingSlots() {
        this.inventory.clear();
    }
    
    @Override
    public void fillInputSlots(final boolean boolean1, final Recipe<?> recipe, final ServerPlayerEntity serverPlayerEntity) {
        new FurnaceInputSlotFiller<>(this).fillInputSlots(serverPlayerEntity, recipe, boolean1);
    }
    
    @Override
    public boolean matches(final Recipe<? super Inventory> recipe) {
        return recipe.matches(this.inventory, this.world);
    }
    
    @Override
    public int getCraftingResultSlotIndex() {
        return 2;
    }
    
    @Override
    public int getCraftingWidth() {
        return 1;
    }
    
    @Override
    public int getCraftingHeight() {
        return 1;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getCraftingSlotCount() {
        return 3;
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
            if (invSlot == 2) {
                if (!this.insertItem(itemStack4, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot4.onStackChanged(itemStack4, itemStack3);
            }
            else if (invSlot == 1 || invSlot == 0) {
                if (!this.insertItem(itemStack4, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.isSmeltable(itemStack4)) {
                if (!this.insertItem(itemStack4, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.isFuel(itemStack4)) {
                if (!this.insertItem(itemStack4, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 3 && invSlot < 30) {
                if (!this.insertItem(itemStack4, 30, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (invSlot >= 30 && invSlot < 39 && !this.insertItem(itemStack4, 3, 30, false)) {
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
    
    protected boolean isSmeltable(final ItemStack itemStack) {
        return this.world.getRecipeManager().<BasicInventory, Recipe>getFirstMatch((RecipeType<Recipe>)this.recipeType, new BasicInventory(new ItemStack[] { itemStack }), this.world).isPresent();
    }
    
    protected boolean isFuel(final ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
    }
    
    @Environment(EnvType.CLIENT)
    public int getCookProgress() {
        final int integer1 = this.propertyDelegate.get(2);
        final int integer2 = this.propertyDelegate.get(3);
        if (integer2 == 0 || integer1 == 0) {
            return 0;
        }
        return integer1 * 24 / integer2;
    }
    
    @Environment(EnvType.CLIENT)
    public int getFuelProgress() {
        int integer1 = this.propertyDelegate.get(1);
        if (integer1 == 0) {
            integer1 = 200;
        }
        return this.propertyDelegate.get(0) * 13 / integer1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isBurning() {
        return this.propertyDelegate.get(0) > 0;
    }
}
