package net.minecraft.recipe.cooking;

import net.minecraft.util.DefaultedList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.recipe.RecipeType;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;

public abstract class CookingRecipe implements Recipe<Inventory>
{
    protected final RecipeType<?> type;
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final float experience;
    protected final int cookTime;
    
    public CookingRecipe(final RecipeType<?> type, final Identifier identifier, final String string, final Ingredient ingredient, final ItemStack itemStack, final float float6, final int integer) {
        this.type = type;
        this.id = identifier;
        this.group = string;
        this.input = ingredient;
        this.output = itemStack;
        this.experience = float6;
        this.cookTime = integer;
    }
    
    @Override
    public boolean matches(final Inventory inv, final World world) {
        return this.input.a(inv.getInvStack(0));
    }
    
    @Override
    public ItemStack craft(final Inventory inv) {
        return this.output.copy();
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(final int width, final int height) {
        return true;
    }
    
    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        final DefaultedList<Ingredient> defaultedList1 = DefaultedList.<Ingredient>create();
        defaultedList1.add(this.input);
        return defaultedList1;
    }
    
    public float getExperience() {
        return this.experience;
    }
    
    @Override
    public ItemStack getOutput() {
        return this.output;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public String getGroup() {
        return this.group;
    }
    
    public int getCookTime() {
        return this.cookTime;
    }
    
    @Override
    public Identifier getId() {
        return this.id;
    }
    
    @Override
    public RecipeType<?> getType() {
        return this.type;
    }
}
