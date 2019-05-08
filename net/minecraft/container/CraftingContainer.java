package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.inventory.Inventory;

public abstract class CraftingContainer<C extends Inventory> extends Container
{
    public CraftingContainer(final ContainerType<?> type, final int syncId) {
        super(type, syncId);
    }
    
    public void fillInputSlots(final boolean boolean1, final Recipe<?> recipe, final ServerPlayerEntity serverPlayerEntity) {
        new InputSlotFiller<>(this).fillInputSlots(serverPlayerEntity, recipe, boolean1);
    }
    
    public abstract void populateRecipeFinder(final RecipeFinder arg1);
    
    public abstract void clearCraftingSlots();
    
    public abstract boolean matches(final Recipe<? super C> arg1);
    
    public abstract int getCraftingResultSlotIndex();
    
    public abstract int getCraftingWidth();
    
    public abstract int getCraftingHeight();
    
    @Environment(EnvType.CLIENT)
    public abstract int getCraftingSlotCount();
}
