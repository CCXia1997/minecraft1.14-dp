package net.minecraft.client.gui.recipebook;

import net.minecraft.item.ItemStack;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.recipe.Recipe;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.container.CraftingContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.recipe.book.RecipeBookGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ToggleButtonWidget;

@Environment(EnvType.CLIENT)
public class GroupButtonWidget extends ToggleButtonWidget
{
    private final RecipeBookGroup category;
    private float bounce;
    
    public GroupButtonWidget(final RecipeBookGroup recipeBookGroup) {
        super(0, 0, 35, 27, false);
        this.category = recipeBookGroup;
        this.setTextureUV(153, 2, 35, 0, RecipeBookGui.TEXTURE);
    }
    
    public void checkForNewRecipes(final MinecraftClient minecraftClient) {
        final ClientRecipeBook clientRecipeBook2 = minecraftClient.player.getRecipeBook();
        final List<RecipeResultCollection> list3 = clientRecipeBook2.getResultsForGroup(this.category);
        if (!(minecraftClient.player.container instanceof CraftingContainer)) {
            return;
        }
        for (final RecipeResultCollection recipeResultCollection5 : list3) {
            for (final Recipe<?> recipe7 : recipeResultCollection5.getResults(clientRecipeBook2.isFilteringCraftable(minecraftClient.player.container))) {
                if (clientRecipeBook2.shouldDisplay(recipe7)) {
                    this.bounce = 15.0f;
                }
            }
        }
    }
    
    @Override
    public void renderButton(final int mouseX, final int mouseY, final float delta) {
        if (this.bounce > 0.0f) {
            final float float4 = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * 3.1415927f);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0f);
            GlStateManager.scalef(1.0f, float4, 1.0f);
            GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0f);
        }
        final MinecraftClient minecraftClient4 = MinecraftClient.getInstance();
        minecraftClient4.getTextureManager().bindTexture(this.texture);
        GlStateManager.disableDepthTest();
        int integer5 = this.u;
        int integer6 = this.v;
        if (this.toggled) {
            integer5 += this.pressedUOffset;
        }
        if (this.isHovered()) {
            integer6 += this.hoverVOffset;
        }
        int integer7 = this.x;
        if (this.toggled) {
            integer7 -= 2;
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit(integer7, this.y, integer5, integer6, this.width, this.height);
        GlStateManager.enableDepthTest();
        GuiLighting.enableForItems();
        GlStateManager.disableLighting();
        this.a(minecraftClient4.getItemRenderer());
        GlStateManager.enableLighting();
        GuiLighting.disable();
        if (this.bounce > 0.0f) {
            GlStateManager.popMatrix();
            this.bounce -= delta;
        }
    }
    
    private void a(final ItemRenderer itemRenderer) {
        final List<ItemStack> list2 = this.category.getIcons();
        final int integer3 = this.toggled ? -2 : 0;
        if (list2.size() == 1) {
            itemRenderer.renderGuiItem(list2.get(0), this.x + 9 + integer3, this.y + 5);
        }
        else if (list2.size() == 2) {
            itemRenderer.renderGuiItem(list2.get(0), this.x + 3 + integer3, this.y + 5);
            itemRenderer.renderGuiItem(list2.get(1), this.x + 14 + integer3, this.y + 5);
        }
    }
    
    public RecipeBookGroup getCategory() {
        return this.category;
    }
    
    public boolean hasKnownRecipes(final ClientRecipeBook clientRecipeBook) {
        final List<RecipeResultCollection> list2 = clientRecipeBook.getResultsForGroup(this.category);
        this.visible = false;
        if (list2 != null) {
            for (final RecipeResultCollection recipeResultCollection4 : list2) {
                if (recipeResultCollection4.isInitialized() && recipeResultCollection4.hasFittableResults()) {
                    this.visible = true;
                    break;
                }
            }
        }
        return this.visible;
    }
}
