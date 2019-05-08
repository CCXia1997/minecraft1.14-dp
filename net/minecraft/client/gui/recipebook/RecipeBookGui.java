package net.minecraft.client.gui.recipebook;

import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.client.gui.Screen;
import net.minecraft.item.ItemStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.GuiLighting;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Locale;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.client.search.SearchManager;
import net.minecraft.recipe.book.RecipeBook;
import javax.annotation.Nullable;
import net.minecraft.container.Slot;
import java.util.Iterator;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.recipe.book.RecipeBookGroup;
import net.minecraft.client.resource.language.I18n;
import com.google.common.collect.Lists;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.CraftingContainer;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import java.util.List;
import net.minecraft.client.gui.widget.RecipeBookGhostSlots;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.client.recipe.book.RecipeDisplayListener;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class RecipeBookGui extends DrawableHelper implements Drawable, Element, RecipeDisplayListener, RecipeGridAligner<Ingredient>
{
    protected static final Identifier TEXTURE;
    private int leftOffset;
    private int parentWidth;
    private int parentHeight;
    protected final RecipeBookGhostSlots ghostSlots;
    private final List<GroupButtonWidget> tabButtons;
    private GroupButtonWidget currentTab;
    protected ToggleButtonWidget toggleCraftableButton;
    protected CraftingContainer<?> craftingContainer;
    protected MinecraftClient client;
    private TextFieldWidget searchField;
    private String searchText;
    protected ClientRecipeBook recipeBook;
    protected final RecipeBookGuiResults recipesArea;
    protected final RecipeFinder recipeFinder;
    private int cachedInvChangeCount;
    private boolean q;
    
    public RecipeBookGui() {
        this.ghostSlots = new RecipeBookGhostSlots();
        this.tabButtons = Lists.newArrayList();
        this.searchText = "";
        this.recipesArea = new RecipeBookGuiResults();
        this.recipeFinder = new RecipeFinder();
    }
    
    public void initialize(final int parentWidth, final int parentHeight, final MinecraftClient client, final boolean isNarrow, final CraftingContainer<?> craftingContainer) {
        this.client = client;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.craftingContainer = craftingContainer;
        client.player.container = craftingContainer;
        this.recipeBook = client.player.getRecipeBook();
        this.cachedInvChangeCount = client.player.inventory.getChangeCount();
        if (this.isOpen()) {
            this.reset(isNarrow);
        }
        client.keyboard.enableRepeatEvents(true);
    }
    
    public void reset(final boolean isNarrow) {
        this.leftOffset = (isNarrow ? 0 : 86);
        final int integer2 = (this.parentWidth - 147) / 2 - this.leftOffset;
        final int integer3 = (this.parentHeight - 166) / 2;
        this.recipeFinder.clear();
        this.client.player.inventory.populateRecipeFinder(this.recipeFinder);
        this.craftingContainer.populateRecipeFinder(this.recipeFinder);
        final String string4 = (this.searchField != null) ? this.searchField.getText() : "";
        final TextRenderer textRenderer = this.client.textRenderer;
        final int x = integer2 + 25;
        final int y = integer3 + 14;
        final int width = 80;
        this.client.textRenderer.getClass();
        (this.searchField = new TextFieldWidget(textRenderer, x, y, width, 9 + 5, I18n.translate("itemGroup.search"))).setMaxLength(50);
        this.searchField.setHasBorder(false);
        this.searchField.setVisible(true);
        this.searchField.h(16777215);
        this.searchField.setText(string4);
        this.recipesArea.initialize(this.client, integer2, integer3);
        this.recipesArea.setGui(this);
        this.toggleCraftableButton = new ToggleButtonWidget(integer2 + 110, integer3 + 12, 26, 16, this.recipeBook.isFilteringCraftable(this.craftingContainer));
        this.setBookButtonTexture();
        this.tabButtons.clear();
        for (final RecipeBookGroup recipeBookGroup6 : ClientRecipeBook.getGroupsForContainer(this.craftingContainer)) {
            this.tabButtons.add(new GroupButtonWidget(recipeBookGroup6));
        }
        if (this.currentTab != null) {
            this.currentTab = this.tabButtons.stream().filter(groupButtonWidget -> groupButtonWidget.getCategory().equals(this.currentTab.getCategory())).findFirst().orElse(null);
        }
        if (this.currentTab == null) {
            this.currentTab = this.tabButtons.get(0);
        }
        this.currentTab.setToggled(true);
        this.refreshResults(false);
        this.refreshTabButtons();
    }
    
    @Override
    public boolean changeFocus(final boolean lookForwards) {
        return false;
    }
    
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextureUV(152, 41, 28, 18, RecipeBookGui.TEXTURE);
    }
    
    public void close() {
        this.searchField = null;
        this.currentTab = null;
        this.client.keyboard.enableRepeatEvents(false);
    }
    
    public int findLeftEdge(final boolean narrow, final int width, final int containerWidth) {
        int integer4;
        if (this.isOpen() && !narrow) {
            integer4 = 177 + (width - containerWidth - 200) / 2;
        }
        else {
            integer4 = (width - containerWidth) / 2;
        }
        return integer4;
    }
    
    public void toggleOpen() {
        this.setOpen(!this.isOpen());
    }
    
    public boolean isOpen() {
        return this.recipeBook.isGuiOpen();
    }
    
    protected void setOpen(final boolean boolean1) {
        this.recipeBook.setGuiOpen(boolean1);
        if (!boolean1) {
            this.recipesArea.hideAlternates();
        }
        this.sendBookDataPacket();
    }
    
    public void slotClicked(@Nullable final Slot slot) {
        if (slot != null && slot.id < this.craftingContainer.getCraftingSlotCount()) {
            this.ghostSlots.reset();
            if (this.isOpen()) {
                this.refreshInputs();
            }
        }
    }
    
    private void refreshResults(final boolean resetCurrentPage) {
        final List<RecipeResultCollection> list2 = this.recipeBook.getResultsForGroup(this.currentTab.getCategory());
        list2.forEach(recipeResultCollection -> recipeResultCollection.computeCraftables(this.recipeFinder, this.craftingContainer.getCraftingWidth(), this.craftingContainer.getCraftingHeight(), this.recipeBook));
        final List<RecipeResultCollection> list3 = Lists.newArrayList(list2);
        list3.removeIf(recipeResultCollection -> !recipeResultCollection.isInitialized());
        list3.removeIf(recipeResultCollection -> !recipeResultCollection.hasFittableResults());
        final String string4 = this.searchField.getText();
        if (!string4.isEmpty()) {
            final ObjectSet<RecipeResultCollection> objectSet5 = (ObjectSet<RecipeResultCollection>)new ObjectLinkedOpenHashSet((Collection)this.client.<RecipeResultCollection>getSearchableContainer(SearchManager.RECIPE_OUTPUT).findAll(string4.toLowerCase(Locale.ROOT)));
            list3.removeIf(recipeResultCollection -> !objectSet5.contains(recipeResultCollection));
        }
        if (this.recipeBook.isFilteringCraftable(this.craftingContainer)) {
            list3.removeIf(recipeResultCollection -> !recipeResultCollection.hasCraftableResults());
        }
        this.recipesArea.setResults(list3, resetCurrentPage);
    }
    
    private void refreshTabButtons() {
        final int integer1 = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
        final int integer2 = (this.parentHeight - 166) / 2 + 3;
        final int integer3 = 27;
        int integer4 = 0;
        for (final GroupButtonWidget groupButtonWidget6 : this.tabButtons) {
            final RecipeBookGroup recipeBookGroup7 = groupButtonWidget6.getCategory();
            if (recipeBookGroup7 == RecipeBookGroup.a || recipeBookGroup7 == RecipeBookGroup.f) {
                groupButtonWidget6.visible = true;
                groupButtonWidget6.setPos(integer1, integer2 + 27 * integer4++);
            }
            else {
                if (!groupButtonWidget6.hasKnownRecipes(this.recipeBook)) {
                    continue;
                }
                groupButtonWidget6.setPos(integer1, integer2 + 27 * integer4++);
                groupButtonWidget6.checkForNewRecipes(this.client);
            }
        }
    }
    
    public void update() {
        if (!this.isOpen()) {
            return;
        }
        if (this.cachedInvChangeCount != this.client.player.inventory.getChangeCount()) {
            this.refreshInputs();
            this.cachedInvChangeCount = this.client.player.inventory.getChangeCount();
        }
    }
    
    private void refreshInputs() {
        this.recipeFinder.clear();
        this.client.player.inventory.populateRecipeFinder(this.recipeFinder);
        this.craftingContainer.populateRecipeFinder(this.recipeFinder);
        this.refreshResults(false);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        if (!this.isOpen()) {
            return;
        }
        GuiLighting.enableForItems();
        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 100.0f);
        this.client.getTextureManager().bindTexture(RecipeBookGui.TEXTURE);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int integer4 = (this.parentWidth - 147) / 2 - this.leftOffset;
        final int integer5 = (this.parentHeight - 166) / 2;
        this.blit(integer4, integer5, 1, 1, 147, 166);
        this.searchField.render(mouseX, mouseY, delta);
        GuiLighting.disable();
        for (final GroupButtonWidget groupButtonWidget7 : this.tabButtons) {
            groupButtonWidget7.render(mouseX, mouseY, delta);
        }
        this.toggleCraftableButton.render(mouseX, mouseY, delta);
        this.recipesArea.draw(integer4, integer5, mouseX, mouseY, delta);
        GlStateManager.popMatrix();
    }
    
    public void drawTooltip(final int left, final int top, final int mouseX, final int mouseY) {
        if (!this.isOpen()) {
            return;
        }
        this.recipesArea.drawTooltip(mouseX, mouseY);
        if (this.toggleCraftableButton.isHovered()) {
            final String string5 = this.getCraftableButtonText();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.renderTooltip(string5, mouseX, mouseY);
            }
        }
        this.drawGhostSlotTooltip(left, top, mouseX, mouseY);
    }
    
    protected String getCraftableButtonText() {
        return I18n.translate(this.toggleCraftableButton.isToggled() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
    }
    
    private void drawGhostSlotTooltip(final int integer1, final int integer2, final int integer3, final int integer4) {
        ItemStack itemStack5 = null;
        for (int integer5 = 0; integer5 < this.ghostSlots.getSlotCount(); ++integer5) {
            final RecipeBookGhostSlots.GhostInputSlot ghostInputSlot7 = this.ghostSlots.getSlot(integer5);
            final int integer6 = ghostInputSlot7.getX() + integer1;
            final int integer7 = ghostInputSlot7.getY() + integer2;
            if (integer3 >= integer6 && integer4 >= integer7 && integer3 < integer6 + 16 && integer4 < integer7 + 16) {
                itemStack5 = ghostInputSlot7.getCurrentItemStack();
            }
        }
        if (itemStack5 != null && this.client.currentScreen != null) {
            this.client.currentScreen.renderTooltip(this.client.currentScreen.getTooltipFromItem(itemStack5), integer3, integer4);
        }
    }
    
    public void drawGhostSlots(final int left, final int top, final boolean boolean3, final float lastFrameDuration) {
        this.ghostSlots.draw(this.client, left, top, boolean3, lastFrameDuration);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (!this.isOpen() || this.client.player.isSpectator()) {
            return false;
        }
        if (this.recipesArea.mouseClicked(mouseX, mouseY, button, (this.parentWidth - 147) / 2 - this.leftOffset, (this.parentHeight - 166) / 2, 147, 166)) {
            final Recipe<?> recipe6 = this.recipesArea.getLastClickedRecipe();
            final RecipeResultCollection recipeResultCollection7 = this.recipesArea.getLastClickedResults();
            if (recipe6 != null && recipeResultCollection7 != null) {
                if (!recipeResultCollection7.isCraftable(recipe6) && this.ghostSlots.getRecipe() == recipe6) {
                    return false;
                }
                this.ghostSlots.reset();
                this.client.interactionManager.clickRecipe(this.client.player.container.syncId, recipe6, Screen.hasShiftDown());
                if (!this.isWide()) {
                    this.setOpen(false);
                }
            }
            return true;
        }
        if (this.searchField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.toggleCraftableButton.mouseClicked(mouseX, mouseY, button)) {
            final boolean boolean6 = this.toggleFilteringCraftable();
            this.toggleCraftableButton.setToggled(boolean6);
            this.sendBookDataPacket();
            this.refreshResults(false);
            return true;
        }
        for (final GroupButtonWidget groupButtonWidget7 : this.tabButtons) {
            if (groupButtonWidget7.mouseClicked(mouseX, mouseY, button)) {
                if (this.currentTab != groupButtonWidget7) {
                    this.currentTab.setToggled(false);
                    (this.currentTab = groupButtonWidget7).setToggled(true);
                    this.refreshResults(true);
                }
                return true;
            }
        }
        return false;
    }
    
    protected boolean toggleFilteringCraftable() {
        final boolean boolean1 = !this.recipeBook.isFilteringCraftable();
        this.recipeBook.setFilteringCraftable(boolean1);
        return boolean1;
    }
    
    public boolean isClickOutsideBounds(final double double1, final double double3, final int integer5, final int integer6, final int integer7, final int integer8, final int integer9) {
        if (!this.isOpen()) {
            return true;
        }
        final boolean boolean10 = double1 < integer5 || double3 < integer6 || double1 >= integer5 + integer7 || double3 >= integer6 + integer8;
        final boolean boolean11 = integer5 - 147 < double1 && double1 < integer5 && integer6 < double3 && double3 < integer6 + integer8;
        return boolean10 && !boolean11 && !this.currentTab.isHovered();
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        this.q = false;
        if (!this.isOpen() || this.client.player.isSpectator()) {
            return false;
        }
        if (keyCode == 256 && !this.isWide()) {
            this.setOpen(false);
            return true;
        }
        if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            this.refreshSearchResults();
            return true;
        }
        if (this.searchField.isFocused() && this.searchField.isVisible() && keyCode != 256) {
            return true;
        }
        if (this.client.options.keyChat.matchesKey(keyCode, scanCode) && !this.searchField.isFocused()) {
            this.q = true;
            this.searchField.a(true);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        this.q = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(final char chr, final int keyCode) {
        if (this.q) {
            return false;
        }
        if (!this.isOpen() || this.client.player.isSpectator()) {
            return false;
        }
        if (this.searchField.charTyped(chr, keyCode)) {
            this.refreshSearchResults();
            return true;
        }
        return super.charTyped(chr, keyCode);
    }
    
    @Override
    public boolean isMouseOver(final double mouseX, final double mouseY) {
        return false;
    }
    
    private void refreshSearchResults() {
        final String string1 = this.searchField.getText().toLowerCase(Locale.ROOT);
        this.triggerPirateSpeakEasterEgg(string1);
        if (!string1.equals(this.searchText)) {
            this.refreshResults(false);
            this.searchText = string1;
        }
    }
    
    private void triggerPirateSpeakEasterEgg(final String string) {
        if ("excitedze".equals(string)) {
            final LanguageManager languageManager2 = this.client.getLanguageManager();
            final LanguageDefinition languageDefinition3 = languageManager2.getLanguage("en_pt");
            if (languageManager2.getLanguage().a(languageDefinition3) == 0) {
                return;
            }
            languageManager2.setLanguage(languageDefinition3);
            this.client.options.language = languageDefinition3.getCode();
            this.client.reloadResources();
            this.client.textRenderer.setRightToLeft(languageManager2.isRightToLeft());
            this.client.options.write();
        }
    }
    
    private boolean isWide() {
        return this.leftOffset == 86;
    }
    
    public void refresh() {
        this.refreshTabButtons();
        if (this.isOpen()) {
            this.refreshResults(false);
        }
    }
    
    @Override
    public void onRecipesDisplayed(final List<Recipe<?>> list) {
        for (final Recipe<?> recipe3 : list) {
            this.client.player.onRecipeDisplayed(recipe3);
        }
    }
    
    public void showGhostRecipe(final Recipe<?> recipe, final List<Slot> list) {
        final ItemStack itemStack3 = recipe.getOutput();
        this.ghostSlots.setRecipe(recipe);
        this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack3), list.get(0).xPosition, list.get(0).yPosition);
        this.alignRecipeToGrid(this.craftingContainer.getCraftingWidth(), this.craftingContainer.getCraftingHeight(), this.craftingContainer.getCraftingResultSlotIndex(), recipe, recipe.getPreviewInputs().iterator(), 0);
    }
    
    @Override
    public void acceptAlignedInput(final Iterator<Ingredient> inputs, final int slot, final int amount, final int gridX, final int gridY) {
        final Ingredient ingredient6 = inputs.next();
        if (!ingredient6.isEmpty()) {
            final Slot slot2 = this.craftingContainer.slotList.get(slot);
            this.ghostSlots.addSlot(ingredient6, slot2.xPosition, slot2.yPosition);
        }
    }
    
    protected void sendBookDataPacket() {
        if (this.client.getNetworkHandler() != null) {
            this.client.getNetworkHandler().sendPacket(new RecipeBookDataC2SPacket(this.recipeBook.isGuiOpen(), this.recipeBook.isFilteringCraftable(), this.recipeBook.isFurnaceGuiOpen(), this.recipeBook.isFurnaceFilteringCraftable(), this.recipeBook.isBlastFurnaceGuiOpen(), this.recipeBook.isBlastFurnaceFilteringCraftable()));
        }
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/recipe_book.png");
    }
}
