package net.minecraft.client.gui.recipebook;

import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.GuiLighting;
import java.util.Iterator;
import net.minecraft.util.math.MathHelper;
import java.util.Collections;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.AbstractFurnaceContainer;
import com.google.common.collect.Lists;
import net.minecraft.recipe.Recipe;
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.client.MinecraftClient;
import java.util.List;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;

@Environment(EnvType.CLIENT)
public class RecipeAlternatesWidget extends DrawableHelper implements Drawable, Element
{
    private static final Identifier BG_TEX;
    private final List<AlternateButtonWidget> alternateButtons;
    private boolean visible;
    private int buttonX;
    private int buttonY;
    private MinecraftClient client;
    private RecipeResultCollection resultCollection;
    private Recipe<?> lastClickedRecipe;
    private float time;
    private boolean furnace;
    
    public RecipeAlternatesWidget() {
        this.alternateButtons = Lists.newArrayList();
    }
    
    public void showAlternatesForResult(final MinecraftClient minecraftClient, final RecipeResultCollection recipeResultCollection, final int buttonX, final int buttonY, final int areaCenterX, final int areaCenterY, final float delta) {
        this.client = minecraftClient;
        this.resultCollection = recipeResultCollection;
        if (minecraftClient.player.container instanceof AbstractFurnaceContainer) {
            this.furnace = true;
        }
        final boolean boolean8 = minecraftClient.player.getRecipeBook().isFilteringCraftable(minecraftClient.player.container);
        final List<Recipe<?>> list9 = recipeResultCollection.getResultsExclusive(true);
        final List<Recipe<?>> list10 = boolean8 ? Collections.<Recipe<?>>emptyList() : recipeResultCollection.getResultsExclusive(false);
        final int integer11 = list9.size();
        final int integer12 = integer11 + list10.size();
        final int integer13 = (integer12 <= 16) ? 4 : 5;
        final int integer14 = (int)Math.ceil(integer12 / (float)integer13);
        this.buttonX = buttonX;
        this.buttonY = buttonY;
        final int integer15 = 25;
        final float float16 = (float)(this.buttonX + Math.min(integer12, integer13) * 25);
        final float float17 = (float)(areaCenterX + 50);
        if (float16 > float17) {
            this.buttonX -= (int)(delta * (int)((float16 - float17) / delta));
        }
        final float float18 = (float)(this.buttonY + integer14 * 25);
        final float float19 = (float)(areaCenterY + 50);
        if (float18 > float19) {
            this.buttonY -= (int)(delta * MathHelper.ceil((float18 - float19) / delta));
        }
        final float float20 = (float)this.buttonY;
        final float float21 = (float)(areaCenterY - 100);
        if (float20 < float21) {
            this.buttonY -= (int)(delta * MathHelper.ceil((float20 - float21) / delta));
        }
        this.visible = true;
        this.alternateButtons.clear();
        for (int integer16 = 0; integer16 < integer12; ++integer16) {
            final boolean boolean9 = integer16 < integer11;
            final Recipe<?> recipe24 = boolean9 ? list9.get(integer16) : list10.get(integer16 - integer11);
            final int integer17 = this.buttonX + 4 + 25 * (integer16 % integer13);
            final int integer18 = this.buttonY + 5 + 25 * (integer16 / integer13);
            if (this.furnace) {
                this.alternateButtons.add(new b(integer17, integer18, recipe24, boolean9));
            }
            else {
                this.alternateButtons.add(new AlternateButtonWidget(integer17, integer18, recipe24, boolean9));
            }
        }
        this.lastClickedRecipe = null;
    }
    
    @Override
    public boolean changeFocus(final boolean lookForwards) {
        return false;
    }
    
    public RecipeResultCollection getResults() {
        return this.resultCollection;
    }
    
    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button != 0) {
            return false;
        }
        for (final AlternateButtonWidget alternateButtonWidget7 : this.alternateButtons) {
            if (alternateButtonWidget7.mouseClicked(mouseX, mouseY, button)) {
                this.lastClickedRecipe = alternateButtonWidget7.recipe;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isMouseOver(final double mouseX, final double mouseY) {
        return false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        if (!this.visible) {
            return;
        }
        this.time += delta;
        GuiLighting.enableForItems();
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(RecipeAlternatesWidget.BG_TEX);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(0.0f, 0.0f, 170.0f);
        final int integer4 = (this.alternateButtons.size() <= 16) ? 4 : 5;
        final int integer5 = Math.min(this.alternateButtons.size(), integer4);
        final int integer6 = MathHelper.ceil(this.alternateButtons.size() / (float)integer4);
        final int integer7 = 24;
        final int integer8 = 4;
        final int integer9 = 82;
        final int integer10 = 208;
        this.a(integer5, integer6, 24, 4, 82, 208);
        GlStateManager.disableBlend();
        GuiLighting.disable();
        for (final AlternateButtonWidget alternateButtonWidget12 : this.alternateButtons) {
            alternateButtonWidget12.render(mouseX, mouseY, delta);
        }
        GlStateManager.popMatrix();
    }
    
    private void a(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5, final int integer6) {
        this.blit(this.buttonX, this.buttonY, integer5, integer6, integer4, integer4);
        this.blit(this.buttonX + integer4 * 2 + integer1 * integer3, this.buttonY, integer5 + integer3 + integer4, integer6, integer4, integer4);
        this.blit(this.buttonX, this.buttonY + integer4 * 2 + integer2 * integer3, integer5, integer6 + integer3 + integer4, integer4, integer4);
        this.blit(this.buttonX + integer4 * 2 + integer1 * integer3, this.buttonY + integer4 * 2 + integer2 * integer3, integer5 + integer3 + integer4, integer6 + integer3 + integer4, integer4, integer4);
        for (int integer7 = 0; integer7 < integer1; ++integer7) {
            this.blit(this.buttonX + integer4 + integer7 * integer3, this.buttonY, integer5 + integer4, integer6, integer3, integer4);
            this.blit(this.buttonX + integer4 + (integer7 + 1) * integer3, this.buttonY, integer5 + integer4, integer6, integer4, integer4);
            for (int integer8 = 0; integer8 < integer2; ++integer8) {
                if (integer7 == 0) {
                    this.blit(this.buttonX, this.buttonY + integer4 + integer8 * integer3, integer5, integer6 + integer4, integer4, integer3);
                    this.blit(this.buttonX, this.buttonY + integer4 + (integer8 + 1) * integer3, integer5, integer6 + integer4, integer4, integer4);
                }
                this.blit(this.buttonX + integer4 + integer7 * integer3, this.buttonY + integer4 + integer8 * integer3, integer5 + integer4, integer6 + integer4, integer3, integer3);
                this.blit(this.buttonX + integer4 + (integer7 + 1) * integer3, this.buttonY + integer4 + integer8 * integer3, integer5 + integer4, integer6 + integer4, integer4, integer3);
                this.blit(this.buttonX + integer4 + integer7 * integer3, this.buttonY + integer4 + (integer8 + 1) * integer3, integer5 + integer4, integer6 + integer4, integer3, integer4);
                this.blit(this.buttonX + integer4 + (integer7 + 1) * integer3 - 1, this.buttonY + integer4 + (integer8 + 1) * integer3 - 1, integer5 + integer4, integer6 + integer4, integer4 + 1, integer4 + 1);
                if (integer7 == integer1 - 1) {
                    this.blit(this.buttonX + integer4 * 2 + integer1 * integer3, this.buttonY + integer4 + integer8 * integer3, integer5 + integer3 + integer4, integer6 + integer4, integer4, integer3);
                    this.blit(this.buttonX + integer4 * 2 + integer1 * integer3, this.buttonY + integer4 + (integer8 + 1) * integer3, integer5 + integer3 + integer4, integer6 + integer4, integer4, integer4);
                }
            }
            this.blit(this.buttonX + integer4 + integer7 * integer3, this.buttonY + integer4 * 2 + integer2 * integer3, integer5 + integer4, integer6 + integer3 + integer4, integer3, integer4);
            this.blit(this.buttonX + integer4 + (integer7 + 1) * integer3, this.buttonY + integer4 * 2 + integer2 * integer3, integer5 + integer4, integer6 + integer3 + integer4, integer4, integer4);
        }
    }
    
    public void setVisible(final boolean boolean1) {
        this.visible = boolean1;
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    static {
        BG_TEX = new Identifier("textures/gui/recipe_book.png");
    }
    
    @Environment(EnvType.CLIENT)
    class b extends AlternateButtonWidget
    {
        public b(final int x, final int y, final Recipe<?> recipe, final boolean isCraftable) {
            super(x, y, recipe, isCraftable);
        }
        
        @Override
        protected void alignRecipe(final Recipe<?> recipe) {
            final ItemStack[] arr2 = recipe.getPreviewInputs().get(0).getStackArray();
            this.slots.add(new InputSlot(10, 10, arr2));
        }
    }
    
    @Environment(EnvType.CLIENT)
    class AlternateButtonWidget extends AbstractButtonWidget implements RecipeGridAligner<Ingredient>
    {
        private final Recipe<?> recipe;
        private final boolean isCraftable;
        protected final List<InputSlot> slots;
        
        public AlternateButtonWidget(final int x, final int y, final Recipe<?> recipe, final boolean isCraftable) {
            super(x, y, 200, 20, "");
            this.slots = Lists.newArrayList();
            this.width = 24;
            this.height = 24;
            this.recipe = recipe;
            this.isCraftable = isCraftable;
            this.alignRecipe(recipe);
        }
        
        protected void alignRecipe(final Recipe<?> recipe) {
            this.alignRecipeToGrid(3, 3, -1, recipe, recipe.getPreviewInputs().iterator(), 0);
        }
        
        @Override
        public void acceptAlignedInput(final Iterator<Ingredient> inputs, final int slot, final int amount, final int gridX, final int gridY) {
            final ItemStack[] arr6 = inputs.next().getStackArray();
            if (arr6.length != 0) {
                this.slots.add(new InputSlot(3 + gridY * 7, 3 + gridX * 7, arr6));
            }
        }
        
        @Override
        public void renderButton(final int mouseX, final int mouseY, final float delta) {
            GuiLighting.enableForItems();
            GlStateManager.enableAlphaTest();
            RecipeAlternatesWidget.this.client.getTextureManager().bindTexture(RecipeAlternatesWidget.BG_TEX);
            int integer4 = 152;
            if (!this.isCraftable) {
                integer4 += 26;
            }
            int integer5 = RecipeAlternatesWidget.this.furnace ? 130 : 78;
            if (this.isHovered()) {
                integer5 += 26;
            }
            this.blit(this.x, this.y, integer4, integer5, this.width, this.height);
            for (final InputSlot inputSlot7 : this.slots) {
                GlStateManager.pushMatrix();
                final float float8 = 0.42f;
                final int integer6 = (int)((this.x + inputSlot7.b) / 0.42f - 3.0f);
                final int integer7 = (int)((this.y + inputSlot7.c) / 0.42f - 3.0f);
                GlStateManager.scalef(0.42f, 0.42f, 1.0f);
                GlStateManager.enableLighting();
                RecipeAlternatesWidget.this.client.getItemRenderer().renderGuiItem(inputSlot7.a[MathHelper.floor(RecipeAlternatesWidget.this.time / 30.0f) % inputSlot7.a.length], integer6, integer7);
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();
            }
            GlStateManager.disableAlphaTest();
            GuiLighting.disable();
        }
        
        @Environment(EnvType.CLIENT)
        public class InputSlot
        {
            public final ItemStack[] a;
            public final int b;
            public final int c;
            
            public InputSlot(final int integer2, final int integer3, final ItemStack[] arr) {
                this.b = integer2;
                this.c = integer3;
                this.a = arr;
            }
        }
    }
}
