package net.minecraft.client.gui.container;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.container.SlotActionType;
import net.minecraft.container.Slot;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.container.CraftingContainer;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class CraftingTableScreen extends ContainerScreen<CraftingTableContainer> implements RecipeBookProvider
{
    private static final Identifier BG_TEX;
    private static final Identifier RECIPE_BUTTON_TEX;
    private final RecipeBookGui recipeBookGui;
    private boolean isNarrow;
    
    public CraftingTableScreen(final CraftingTableContainer craftingTableContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(craftingTableContainer, playerInventory, textComponent);
        this.recipeBookGui = new RecipeBookGui();
    }
    
    @Override
    protected void init() {
        super.init();
        this.isNarrow = (this.width < 379);
        this.recipeBookGui.initialize(this.width, this.height, this.minecraft, this.isNarrow, this.container);
        this.left = this.recipeBookGui.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
        this.children.add(this.recipeBookGui);
        this.setInitialFocus(this.recipeBookGui);
        this.<RecipeBookButtonWidget>addButton(new RecipeBookButtonWidget(this.left + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, CraftingTableScreen.RECIPE_BUTTON_TEX, buttonWidget -> {
            this.recipeBookGui.reset(this.isNarrow);
            this.recipeBookGui.toggleOpen();
            this.left = this.recipeBookGui.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
            buttonWidget.setPos(this.left + 5, this.height / 2 - 49);
        }));
    }
    
    @Override
    public void tick() {
        super.tick();
        this.recipeBookGui.update();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        if (this.recipeBookGui.isOpen() && this.isNarrow) {
            this.drawBackground(delta, mouseX, mouseY);
            this.recipeBookGui.render(mouseX, mouseY, delta);
        }
        else {
            this.recipeBookGui.render(mouseX, mouseY, delta);
            super.render(mouseX, mouseY, delta);
            this.recipeBookGui.drawGhostSlots(this.left, this.top, true, delta);
        }
        this.drawMouseoverTooltip(mouseX, mouseY);
        this.recipeBookGui.drawTooltip(this.left, this.top, mouseX, mouseY);
        this.focusOn(this.recipeBookGui);
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        this.font.draw(this.title.getFormattedText(), 28.0f, 6.0f, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, (float)(this.containerHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(CraftingTableScreen.BG_TEX);
        final int integer4 = this.left;
        final int integer5 = (this.height - this.containerHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
    }
    
    @Override
    protected boolean isPointWithinBounds(final int xPosition, final int yPosition, final int width, final int height, final double pointX, final double pointY) {
        return (!this.isNarrow || !this.recipeBookGui.isOpen()) && super.isPointWithinBounds(xPosition, yPosition, width, height, pointX, pointY);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return this.recipeBookGui.mouseClicked(mouseX, mouseY, button) || (this.isNarrow && this.recipeBookGui.isOpen()) || super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected boolean isClickOutsideBounds(final double mouseX, final double mouseY, final int left, final int top, final int button) {
        final boolean boolean8 = mouseX < left || mouseY < top || mouseX >= left + this.containerWidth || mouseY >= top + this.containerHeight;
        return this.recipeBookGui.isClickOutsideBounds(mouseX, mouseY, this.left, this.top, this.containerWidth, this.containerHeight, button) && boolean8;
    }
    
    @Override
    protected void onMouseClick(final Slot slot, final int invSlot, final int button, final SlotActionType slotActionType) {
        super.onMouseClick(slot, invSlot, button, slotActionType);
        this.recipeBookGui.slotClicked(slot);
    }
    
    @Override
    public void refreshRecipeBook() {
        this.recipeBookGui.refresh();
    }
    
    @Override
    public void removed() {
        this.recipeBookGui.close();
        super.removed();
    }
    
    @Override
    public RecipeBookGui getRecipeBookGui() {
        return this.recipeBookGui;
    }
    
    static {
        BG_TEX = new Identifier("textures/gui/container/crafting_table.png");
        RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
    }
}
