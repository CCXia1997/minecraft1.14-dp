package net.minecraft.client.gui.widget;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.MinecraftClient;
import javax.annotation.Nullable;
import net.minecraft.recipe.Ingredient;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.recipe.Recipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RecipeBookGhostSlots
{
    private Recipe<?> recipe;
    private final List<GhostInputSlot> slots;
    private float time;
    
    public RecipeBookGhostSlots() {
        this.slots = Lists.newArrayList();
    }
    
    public void reset() {
        this.recipe = null;
        this.slots.clear();
        this.time = 0.0f;
    }
    
    public void addSlot(final Ingredient ingredient, final int x, final int y) {
        this.slots.add(new GhostInputSlot(ingredient, x, y));
    }
    
    public GhostInputSlot getSlot(final int integer) {
        return this.slots.get(integer);
    }
    
    public int getSlotCount() {
        return this.slots.size();
    }
    
    @Nullable
    public Recipe<?> getRecipe() {
        return this.recipe;
    }
    
    public void setRecipe(final Recipe<?> recipe) {
        this.recipe = recipe;
    }
    
    public void draw(final MinecraftClient minecraftClient, final int integer2, final int integer3, final boolean boolean4, final float lastFrameDuration) {
        if (!Screen.hasControlDown()) {
            this.time += lastFrameDuration;
        }
        GuiLighting.enableForItems();
        GlStateManager.disableLighting();
        for (int integer4 = 0; integer4 < this.slots.size(); ++integer4) {
            final GhostInputSlot ghostInputSlot7 = this.slots.get(integer4);
            final int integer5 = ghostInputSlot7.getX() + integer2;
            final int integer6 = ghostInputSlot7.getY() + integer3;
            if (integer4 == 0 && boolean4) {
                DrawableHelper.fill(integer5 - 4, integer6 - 4, integer5 + 20, integer6 + 20, 822018048);
            }
            else {
                DrawableHelper.fill(integer5, integer6, integer5 + 16, integer6 + 16, 822018048);
            }
            final ItemStack itemStack10 = ghostInputSlot7.getCurrentItemStack();
            final ItemRenderer itemRenderer11 = minecraftClient.getItemRenderer();
            itemRenderer11.renderGuiItem(minecraftClient.player, itemStack10, integer5, integer6);
            GlStateManager.depthFunc(516);
            DrawableHelper.fill(integer5, integer6, integer5 + 16, integer6 + 16, 822083583);
            GlStateManager.depthFunc(515);
            if (integer4 == 0) {
                itemRenderer11.renderGuiItemOverlay(minecraftClient.textRenderer, itemStack10, integer5, integer6);
            }
            GlStateManager.enableLighting();
        }
        GuiLighting.disable();
    }
    
    @Environment(EnvType.CLIENT)
    public class GhostInputSlot
    {
        private final Ingredient ingredient;
        private final int x;
        private final int y;
        
        public GhostInputSlot(final Ingredient ingredient, final int x, final int y) {
            this.ingredient = ingredient;
            this.x = x;
            this.y = y;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public ItemStack getCurrentItemStack() {
            final ItemStack[] arr1 = this.ingredient.getStackArray();
            return arr1[MathHelper.floor(RecipeBookGhostSlots.this.time / 30.0f) % arr1.length];
        }
    }
}
