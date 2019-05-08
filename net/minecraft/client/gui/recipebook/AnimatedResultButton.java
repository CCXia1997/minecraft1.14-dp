package net.minecraft.client.gui.recipebook;

import net.minecraft.client.resource.language.I18n;
import java.util.Collection;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.gui.Screen;
import java.util.Iterator;
import java.util.List;
import net.minecraft.recipe.Recipe;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.container.CraftingContainer;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

@Environment(EnvType.CLIENT)
public class AnimatedResultButton extends AbstractButtonWidget
{
    private static final Identifier BG_TEX;
    private CraftingContainer<?> craftingContainer;
    private RecipeBook recipeBook;
    private RecipeResultCollection results;
    private float time;
    private float bounce;
    private int currentResultIndex;
    
    public AnimatedResultButton() {
        super(0, 0, 25, 25, "");
    }
    
    public void showResultCollection(final RecipeResultCollection recipeResultCollection, final RecipeBookGuiResults recipeBookGuiResults) {
        this.results = recipeResultCollection;
        this.craftingContainer = recipeBookGuiResults.getMinecraftClient().player.container;
        this.recipeBook = recipeBookGuiResults.getRecipeBook();
        final List<Recipe<?>> list3 = recipeResultCollection.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer));
        for (final Recipe<?> recipe5 : list3) {
            if (this.recipeBook.shouldDisplay(recipe5)) {
                recipeBookGuiResults.onRecipesDisplayed(list3);
                this.bounce = 15.0f;
                break;
            }
        }
    }
    
    public RecipeResultCollection getResultCollection() {
        return this.results;
    }
    
    public void setPos(final int integer1, final int integer2) {
        this.x = integer1;
        this.y = integer2;
    }
    
    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        if (!Screen.hasControlDown()) {
            this.time += delta;
        }
        GuiLighting.enableForItems();
        final MinecraftClient minecraftClient4 = MinecraftClient.getInstance();
        minecraftClient4.getTextureManager().bindTexture(AnimatedResultButton.BG_TEX);
        GlStateManager.disableLighting();
        int integer5 = 29;
        if (!this.results.hasCraftableResults()) {
            integer5 += 25;
        }
        int integer6 = 206;
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer)).size() > 1) {
            integer6 += 25;
        }
        final boolean boolean7 = this.bounce > 0.0f;
        if (boolean7) {
            final float float8 = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * 3.1415927f);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0f);
            GlStateManager.scalef(float8, float8, 1.0f);
            GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0f);
            this.bounce -= delta;
        }
        this.blit(this.x, this.y, integer5, integer6, this.width, this.height);
        final List<Recipe<?>> list8 = this.getResults();
        this.currentResultIndex = MathHelper.floor(this.time / 30.0f) % list8.size();
        final ItemStack itemStack9 = list8.get(this.currentResultIndex).getOutput();
        int integer7 = 4;
        if (this.results.e() && this.getResults().size() > 1) {
            minecraftClient4.getItemRenderer().renderGuiItem(itemStack9, this.x + integer7 + 1, this.y + integer7 + 1);
            --integer7;
        }
        minecraftClient4.getItemRenderer().renderGuiItem(itemStack9, this.x + integer7, this.y + integer7);
        if (boolean7) {
            GlStateManager.popMatrix();
        }
        GlStateManager.enableLighting();
        GuiLighting.disable();
    }
    
    private List<Recipe<?>> getResults() {
        final List<Recipe<?>> list1 = this.results.getResultsExclusive(true);
        if (!this.recipeBook.isFilteringCraftable(this.craftingContainer)) {
            list1.addAll(this.results.getResultsExclusive(false));
        }
        return list1;
    }
    
    public boolean hasResults() {
        return this.getResults().size() == 1;
    }
    
    public Recipe<?> currentRecipe() {
        final List<Recipe<?>> list1 = this.getResults();
        return list1.get(this.currentResultIndex);
    }
    
    public List<String> a(final Screen screen) {
        final ItemStack itemStack2 = this.getResults().get(this.currentResultIndex).getOutput();
        final List<String> list3 = screen.getTooltipFromItem(itemStack2);
        if (this.results.getResults(this.recipeBook.isFilteringCraftable(this.craftingContainer)).size() > 1) {
            list3.add(I18n.translate("gui.recipebook.moreRecipes"));
        }
        return list3;
    }
    
    @Override
    public int getWidth() {
        return 25;
    }
    
    @Override
    protected boolean isValidClickButton(final int integer) {
        return integer == 0 || integer == 1;
    }
    
    static {
        BG_TEX = new Identifier("textures/gui/recipe_book.png");
    }
}
