package net.minecraft.client.gui.container;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.container.SlotActionType;
import net.minecraft.container.Slot;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.container.CraftingContainer;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.container.AbstractFurnaceContainer;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceContainer> extends ContainerScreen<T> implements RecipeBookProvider
{
    private static final Identifier RECIPE_BUTTON_TEXTURE;
    public final AbstractFurnaceRecipeBookScreen recipeBook;
    private boolean narrow;
    private final Identifier n;
    
    public AbstractFurnaceScreen(final T abstractFurnaceContainer, final AbstractFurnaceRecipeBookScreen abstractFurnaceRecipeBookScreen, final PlayerInventory playerInventory, final TextComponent textComponent, final Identifier identifier) {
        super(abstractFurnaceContainer, playerInventory, textComponent);
        this.recipeBook = abstractFurnaceRecipeBookScreen;
        this.n = identifier;
    }
    
    public void init() {
        super.init();
        this.narrow = (this.width < 379);
        this.recipeBook.initialize(this.width, this.height, this.minecraft, this.narrow, this.container);
        this.left = this.recipeBook.findLeftEdge(this.narrow, this.width, this.containerWidth);
        this.<RecipeBookButtonWidget>addButton(new RecipeBookButtonWidget(this.left + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, AbstractFurnaceScreen.RECIPE_BUTTON_TEXTURE, buttonWidget -> {
            this.recipeBook.reset(this.narrow);
            this.recipeBook.toggleOpen();
            this.left = this.recipeBook.findLeftEdge(this.narrow, this.width, this.containerWidth);
            buttonWidget.setPos(this.left + 20, this.height / 2 - 49);
        }));
    }
    
    @Override
    public void tick() {
        super.tick();
        this.recipeBook.update();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(delta, mouseX, mouseY);
            this.recipeBook.render(mouseX, mouseY, delta);
        }
        else {
            this.recipeBook.render(mouseX, mouseY, delta);
            super.render(mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(this.left, this.top, true, delta);
        }
        this.drawMouseoverTooltip(mouseX, mouseY);
        this.recipeBook.drawTooltip(this.left, this.top, mouseX, mouseY);
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        final String string3 = this.title.getFormattedText();
        this.font.draw(string3, (float)(this.containerWidth / 2 - this.font.getStringWidth(string3) / 2), 6.0f, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, (float)(this.containerHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(this.n);
        final int integer4 = this.left;
        final int integer5 = this.top;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        if (this.container.isBurning()) {
            final int integer6 = this.container.getFuelProgress();
            this.blit(integer4 + 56, integer5 + 36 + 12 - integer6, 176, 12 - integer6, 14, integer6 + 1);
        }
        final int integer6 = this.container.getCookProgress();
        this.blit(integer4 + 79, integer5 + 34, 176, 14, integer6 + 1, 16);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return this.recipeBook.mouseClicked(mouseX, mouseY, button) || (this.narrow && this.recipeBook.isOpen()) || super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void onMouseClick(final Slot slot, final int invSlot, final int button, final SlotActionType slotActionType) {
        super.onMouseClick(slot, invSlot, button, slotActionType);
        this.recipeBook.slotClicked(slot);
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        return !this.recipeBook.keyPressed(keyCode, scanCode, modifiers) && super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    protected boolean isClickOutsideBounds(final double mouseX, final double mouseY, final int left, final int top, final int button) {
        final boolean boolean8 = mouseX < left || mouseY < top || mouseX >= left + this.containerWidth || mouseY >= top + this.containerHeight;
        return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.left, this.top, this.containerWidth, this.containerHeight, button) && boolean8;
    }
    
    @Override
    public boolean charTyped(final char chr, final int keyCode) {
        return this.recipeBook.charTyped(chr, keyCode) || super.charTyped(chr, keyCode);
    }
    
    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }
    
    @Override
    public RecipeBookGui getRecipeBookGui() {
        return this.recipeBook;
    }
    
    @Override
    public void removed() {
        this.recipeBook.close();
        super.removed();
    }
    
    static {
        RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    }
}
