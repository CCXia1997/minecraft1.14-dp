package net.minecraft.block.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.recipe.RecipeType;

public class FurnaceBlockEntity extends AbstractFurnaceBlockEntity
{
    public FurnaceBlockEntity() {
        super(BlockEntityType.FURNACE, RecipeType.SMELTING);
    }
    
    @Override
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.furnace", new Object[0]);
    }
    
    @Override
    protected Container createContainer(final int integer, final PlayerInventory playerInventory) {
        return new FurnaceContainer(integer, playerInventory, this, this.propertyDelegate);
    }
}
