package net.minecraft.client.toast;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.resource.language.I18n;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.recipe.Recipe;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RecipeToast implements Toast
{
    private final List<Recipe<?>> recipes;
    private long startTime;
    private boolean justUpdated;
    
    public RecipeToast(final Recipe<?> recipe) {
        (this.recipes = Lists.newArrayList()).add(recipe);
    }
    
    @Override
    public Visibility draw(final ToastManager manager, final long currentTime) {
        if (this.justUpdated) {
            this.startTime = currentTime;
            this.justUpdated = false;
        }
        if (this.recipes.isEmpty()) {
            return Visibility.b;
        }
        manager.getGame().getTextureManager().bindTexture(RecipeToast.TOASTS_TEX);
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        manager.blit(0, 0, 0, 32, 160, 32);
        manager.getGame().textRenderer.draw(I18n.translate("recipe.toast.title"), 30.0f, 7.0f, -11534256);
        manager.getGame().textRenderer.draw(I18n.translate("recipe.toast.description"), 30.0f, 18.0f, -16777216);
        GuiLighting.enableForItems();
        final Recipe<?> recipe4 = this.recipes.get((int)(currentTime / (5000L / this.recipes.size()) % this.recipes.size()));
        final ItemStack itemStack5 = recipe4.getRecipeKindIcon();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.6f, 0.6f, 1.0f);
        manager.getGame().getItemRenderer().renderGuiItem(null, itemStack5, 3, 3);
        GlStateManager.popMatrix();
        manager.getGame().getItemRenderer().renderGuiItem(null, recipe4.getOutput(), 8, 8);
        return (currentTime - this.startTime >= 5000L) ? Visibility.b : Visibility.a;
    }
    
    public void addRecipe(final Recipe<?> recipe) {
        if (this.recipes.add(recipe)) {
            this.justUpdated = true;
        }
    }
    
    public static void show(final ToastManager toastManager, final Recipe<?> recipe) {
        final RecipeToast recipeToast3 = toastManager.<RecipeToast>getToast(RecipeToast.class, RecipeToast.b);
        if (recipeToast3 == null) {
            toastManager.add(new RecipeToast(recipe));
        }
        else {
            recipeToast3.addRecipe(recipe);
        }
    }
}
