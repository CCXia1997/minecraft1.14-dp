package net.minecraft.client.gui.recipebook;

import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.client.render.GuiLighting;
import com.google.common.collect.Lists;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.client.recipe.book.RecipeDisplayListener;
import net.minecraft.client.MinecraftClient;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RecipeBookGuiResults
{
    private final List<AnimatedResultButton> resultButtons;
    private AnimatedResultButton hoveredResultButton;
    private final RecipeAlternatesWidget alternatesWidget;
    private MinecraftClient client;
    private final List<RecipeDisplayListener> recipeDisplayListeners;
    private List<RecipeResultCollection> resultCollections;
    private ToggleButtonWidget nextPageButton;
    private ToggleButtonWidget prevPageButton;
    private int pageCount;
    private int currentPage;
    private RecipeBook recipeBook;
    private Recipe<?> lastClickedRecipe;
    private RecipeResultCollection resultCollection;
    
    public RecipeBookGuiResults() {
        this.resultButtons = Lists.newArrayListWithCapacity(20);
        this.alternatesWidget = new RecipeAlternatesWidget();
        this.recipeDisplayListeners = Lists.newArrayList();
        for (int integer1 = 0; integer1 < 20; ++integer1) {
            this.resultButtons.add(new AnimatedResultButton());
        }
    }
    
    public void initialize(final MinecraftClient minecraftClient, final int parentLeft, final int parentTop) {
        this.client = minecraftClient;
        this.recipeBook = minecraftClient.player.getRecipeBook();
        for (int integer4 = 0; integer4 < this.resultButtons.size(); ++integer4) {
            this.resultButtons.get(integer4).setPos(parentLeft + 11 + 25 * (integer4 % 5), parentTop + 31 + 25 * (integer4 / 5));
        }
        (this.nextPageButton = new ToggleButtonWidget(parentLeft + 93, parentTop + 137, 12, 17, false)).setTextureUV(1, 208, 13, 18, RecipeBookGui.TEXTURE);
        (this.prevPageButton = new ToggleButtonWidget(parentLeft + 38, parentTop + 137, 12, 17, true)).setTextureUV(1, 208, 13, 18, RecipeBookGui.TEXTURE);
    }
    
    public void setGui(final RecipeBookGui recipeBookGui) {
        this.recipeDisplayListeners.remove(recipeBookGui);
        this.recipeDisplayListeners.add(recipeBookGui);
    }
    
    public void setResults(final List<RecipeResultCollection> list, final boolean resetCurrentPage) {
        this.resultCollections = list;
        this.pageCount = (int)Math.ceil(list.size() / 20.0);
        if (this.pageCount <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }
        this.refreshResultButtons();
    }
    
    private void refreshResultButtons() {
        final int integer1 = 20 * this.currentPage;
        for (int integer2 = 0; integer2 < this.resultButtons.size(); ++integer2) {
            final AnimatedResultButton animatedResultButton3 = this.resultButtons.get(integer2);
            if (integer1 + integer2 < this.resultCollections.size()) {
                final RecipeResultCollection recipeResultCollection4 = this.resultCollections.get(integer1 + integer2);
                animatedResultButton3.showResultCollection(recipeResultCollection4, this);
                animatedResultButton3.visible = true;
            }
            else {
                animatedResultButton3.visible = false;
            }
        }
        this.hideShowPageButtons();
    }
    
    private void hideShowPageButtons() {
        this.nextPageButton.visible = (this.pageCount > 1 && this.currentPage < this.pageCount - 1);
        this.prevPageButton.visible = (this.pageCount > 1 && this.currentPage > 0);
    }
    
    public void draw(final int left, final int top, final int mouseX, final int mouseY, final float delta) {
        if (this.pageCount > 1) {
            final String string6 = this.currentPage + 1 + "/" + this.pageCount;
            final int integer7 = this.client.textRenderer.getStringWidth(string6);
            this.client.textRenderer.draw(string6, (float)(left - integer7 / 2 + 73), (float)(top + 141), -1);
        }
        GuiLighting.disable();
        this.hoveredResultButton = null;
        for (final AnimatedResultButton animatedResultButton7 : this.resultButtons) {
            animatedResultButton7.render(mouseX, mouseY, delta);
            if (animatedResultButton7.visible && animatedResultButton7.isHovered()) {
                this.hoveredResultButton = animatedResultButton7;
            }
        }
        this.prevPageButton.render(mouseX, mouseY, delta);
        this.nextPageButton.render(mouseX, mouseY, delta);
        this.alternatesWidget.render(mouseX, mouseY, delta);
    }
    
    public void drawTooltip(final int integer1, final int integer2) {
        if (this.client.currentScreen != null && this.hoveredResultButton != null && !this.alternatesWidget.isVisible()) {
            this.client.currentScreen.renderTooltip(this.hoveredResultButton.a(this.client.currentScreen), integer1, integer2);
        }
    }
    
    @Nullable
    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }
    
    @Nullable
    public RecipeResultCollection getLastClickedResults() {
        return this.resultCollection;
    }
    
    public void hideAlternates() {
        this.alternatesWidget.setVisible(false);
    }
    
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button, final int areaLeft, final int areaTop, final int areaWidth, final int areaHeight) {
        this.lastClickedRecipe = null;
        this.resultCollection = null;
        if (this.alternatesWidget.isVisible()) {
            if (this.alternatesWidget.mouseClicked(mouseX, mouseY, button)) {
                this.lastClickedRecipe = this.alternatesWidget.getLastClickedRecipe();
                this.resultCollection = this.alternatesWidget.getResults();
            }
            else {
                this.alternatesWidget.setVisible(false);
            }
            return true;
        }
        if (this.nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            ++this.currentPage;
            this.refreshResultButtons();
            return true;
        }
        if (this.prevPageButton.mouseClicked(mouseX, mouseY, button)) {
            --this.currentPage;
            this.refreshResultButtons();
            return true;
        }
        for (final AnimatedResultButton animatedResultButton11 : this.resultButtons) {
            if (animatedResultButton11.mouseClicked(mouseX, mouseY, button)) {
                if (button == 0) {
                    this.lastClickedRecipe = animatedResultButton11.currentRecipe();
                    this.resultCollection = animatedResultButton11.getResultCollection();
                }
                else if (button == 1 && !this.alternatesWidget.isVisible() && !animatedResultButton11.hasResults()) {
                    this.alternatesWidget.showAlternatesForResult(this.client, animatedResultButton11.getResultCollection(), animatedResultButton11.x, animatedResultButton11.y, areaLeft + areaWidth / 2, areaTop + 13 + areaHeight / 2, (float)animatedResultButton11.getWidth());
                }
                return true;
            }
        }
        return false;
    }
    
    public void onRecipesDisplayed(final List<Recipe<?>> list) {
        for (final RecipeDisplayListener recipeDisplayListener3 : this.recipeDisplayListeners) {
            recipeDisplayListener3.onRecipesDisplayed(list);
        }
    }
    
    public MinecraftClient getMinecraftClient() {
        return this.client;
    }
    
    public RecipeBook getRecipeBook() {
        return this.recipeBook;
    }
}
