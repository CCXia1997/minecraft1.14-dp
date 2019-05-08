package net.minecraft.recipe;

import net.minecraft.util.math.MathHelper;
import net.minecraft.recipe.crafting.ShapedRecipe;
import java.util.Iterator;

public interface RecipeGridAligner<T>
{
    default void alignRecipeToGrid(final int gridWidth, final int gridHeight, final int gridOutputSlot, final Recipe<?> recipe, final Iterator<T> inputs, final int amount) {
        int integer7 = gridWidth;
        int integer8 = gridHeight;
        if (recipe instanceof ShapedRecipe) {
            final ShapedRecipe shapedRecipe9 = (ShapedRecipe)recipe;
            integer7 = shapedRecipe9.getWidth();
            integer8 = shapedRecipe9.getHeight();
        }
        int integer9 = 0;
        for (int integer10 = 0; integer10 < gridHeight; ++integer10) {
            if (integer9 == gridOutputSlot) {
                ++integer9;
            }
            boolean boolean11 = integer8 < gridHeight / 2.0f;
            int integer11 = MathHelper.floor(gridHeight / 2.0f - integer8 / 2.0f);
            if (boolean11 && integer11 > integer10) {
                integer9 += gridWidth;
                ++integer10;
            }
            for (int integer12 = 0; integer12 < gridWidth; ++integer12) {
                if (!inputs.hasNext()) {
                    return;
                }
                boolean11 = (integer7 < gridWidth / 2.0f);
                integer11 = MathHelper.floor(gridWidth / 2.0f - integer7 / 2.0f);
                int integer13 = integer7;
                boolean boolean12 = integer12 < integer7;
                if (boolean11) {
                    integer13 = integer11 + integer7;
                    boolean12 = (integer11 <= integer12 && integer12 < integer11 + integer7);
                }
                if (boolean12) {
                    this.acceptAlignedInput(inputs, integer9, amount, integer10, integer12);
                }
                else if (integer13 == integer12) {
                    integer9 += gridWidth - integer12;
                    break;
                }
                ++integer9;
            }
        }
    }
    
    void acceptAlignedInput(final Iterator<T> arg1, final int arg2, final int arg3, final int arg4, final int arg5);
}
