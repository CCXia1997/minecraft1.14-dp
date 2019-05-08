package net.minecraft.client.gui.container;

import net.minecraft.entity.LivingEntity;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.gui.Screen;
import net.minecraft.util.DefaultedList;
import net.minecraft.recipe.Ingredient;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.minecraft.recipe.Recipe;
import javax.annotation.Nullable;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.Slot;
import java.util.Set;
import net.minecraft.item.Item;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.recipebook.RecipeBookGui;

@Environment(EnvType.CLIENT)
public abstract class AbstractFurnaceRecipeBookScreen extends RecipeBookGui
{
    private Iterator<Item> i;
    private Set<Item> j;
    private Slot outputSlot;
    private Item l;
    private float m;
    
    @Override
    protected boolean toggleFilteringCraftable() {
        final boolean boolean1 = !this.isFilteringCraftable();
        this.setFilteringCraftable(boolean1);
        return boolean1;
    }
    
    protected abstract boolean isFilteringCraftable();
    
    protected abstract void setFilteringCraftable(final boolean arg1);
    
    @Override
    public boolean isOpen() {
        return this.isGuiOpen();
    }
    
    protected abstract boolean isGuiOpen();
    
    @Override
    protected void setOpen(final boolean boolean1) {
        this.setGuiOpen(boolean1);
        if (!boolean1) {
            this.recipesArea.hideAlternates();
        }
        this.sendBookDataPacket();
    }
    
    protected abstract void setGuiOpen(final boolean arg1);
    
    @Override
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextureUV(152, 182, 28, 18, AbstractFurnaceRecipeBookScreen.TEXTURE);
    }
    
    @Override
    protected String getCraftableButtonText() {
        return I18n.translate(this.toggleCraftableButton.isToggled() ? this.getToggleCraftableButtonText() : "gui.recipebook.toggleRecipes.all");
    }
    
    protected abstract String getToggleCraftableButtonText();
    
    @Override
    public void slotClicked(@Nullable final Slot slot) {
        super.slotClicked(slot);
        if (slot != null && slot.id < this.craftingContainer.getCraftingSlotCount()) {
            this.outputSlot = null;
        }
    }
    
    @Override
    public void showGhostRecipe(final Recipe<?> recipe, final List<Slot> list) {
        final ItemStack itemStack3 = recipe.getOutput();
        this.ghostSlots.setRecipe(recipe);
        this.ghostSlots.addSlot(Ingredient.ofStacks(itemStack3), list.get(2).xPosition, list.get(2).yPosition);
        final DefaultedList<Ingredient> defaultedList4 = recipe.getPreviewInputs();
        this.outputSlot = list.get(1);
        if (this.j == null) {
            this.j = this.getAllowedFuels();
        }
        this.i = this.j.iterator();
        this.l = null;
        final Iterator<Ingredient> iterator5 = defaultedList4.iterator();
        for (int integer6 = 0; integer6 < 2; ++integer6) {
            if (!iterator5.hasNext()) {
                return;
            }
            final Ingredient ingredient7 = iterator5.next();
            if (!ingredient7.isEmpty()) {
                final Slot slot8 = list.get(integer6);
                this.ghostSlots.addSlot(ingredient7, slot8.xPosition, slot8.yPosition);
            }
        }
    }
    
    protected abstract Set<Item> getAllowedFuels();
    
    @Override
    public void drawGhostSlots(final int left, final int top, final boolean boolean3, final float lastFrameDuration) {
        super.drawGhostSlots(left, top, boolean3, lastFrameDuration);
        if (this.outputSlot == null) {
            return;
        }
        if (!Screen.hasControlDown()) {
            this.m += lastFrameDuration;
        }
        GuiLighting.enableForItems();
        GlStateManager.disableLighting();
        final int integer5 = this.outputSlot.xPosition + left;
        final int integer6 = this.outputSlot.yPosition + top;
        DrawableHelper.fill(integer5, integer6, integer5 + 16, integer6 + 16, 822018048);
        this.client.getItemRenderer().renderGuiItem(this.client.player, this.n().getDefaultStack(), integer5, integer6);
        GlStateManager.depthFunc(516);
        DrawableHelper.fill(integer5, integer6, integer5 + 16, integer6 + 16, 822083583);
        GlStateManager.depthFunc(515);
        GlStateManager.enableLighting();
        GuiLighting.disable();
    }
    
    private Item n() {
        if (this.l == null || this.m > 30.0f) {
            this.m = 0.0f;
            if (this.i == null || !this.i.hasNext()) {
                if (this.j == null) {
                    this.j = this.getAllowedFuels();
                }
                this.i = this.j.iterator();
            }
            this.l = this.i.next();
        }
        return this.l;
    }
}
