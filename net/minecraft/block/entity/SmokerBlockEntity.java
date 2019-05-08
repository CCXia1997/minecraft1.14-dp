package net.minecraft.block.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.container.SmokerContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.recipe.RecipeType;

public class SmokerBlockEntity extends AbstractFurnaceBlockEntity
{
    public SmokerBlockEntity() {
        super(BlockEntityType.SMOKER, RecipeType.SMOKING);
    }
    
    @Override
    protected TextComponent getContainerName() {
        return new TranslatableTextComponent("container.smoker", new Object[0]);
    }
    
    @Override
    protected int getFuelTime(final ItemStack fuel) {
        return super.getFuelTime(fuel) / 2;
    }
    
    @Override
    protected Container createContainer(final int integer, final PlayerInventory playerInventory) {
        return new SmokerContainer(integer, playerInventory, this, this.propertyDelegate);
    }
}
