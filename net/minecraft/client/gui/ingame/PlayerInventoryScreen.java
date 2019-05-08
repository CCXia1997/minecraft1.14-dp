package net.minecraft.client.gui.ingame;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.container.SlotActionType;
import net.minecraft.container.Slot;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.RecipeBookButtonWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.container.CraftingContainer;
import net.minecraft.client.gui.Screen;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.PlayerContainer;

@Environment(EnvType.CLIENT)
public class PlayerInventoryScreen extends AbstractPlayerInventoryScreen<PlayerContainer> implements RecipeBookProvider
{
    private static final Identifier RECIPE_BUTTON_TEX;
    private float mouseX;
    private float mouseY;
    private final RecipeBookGui recipeBook;
    private boolean isOpen;
    private boolean isNarrow;
    private boolean isMouseDown;
    
    public PlayerInventoryScreen(final PlayerEntity player) {
        super(player.playerContainer, player.inventory, new TranslatableTextComponent("container.crafting", new Object[0]));
        this.recipeBook = new RecipeBookGui();
        this.passEvents = true;
    }
    
    @Override
    public void tick() {
        if (this.minecraft.interactionManager.hasCreativeInventory()) {
            this.minecraft.openScreen(new CreativePlayerInventoryScreen(this.minecraft.player));
            return;
        }
        this.recipeBook.update();
    }
    
    @Override
    protected void init() {
        if (this.minecraft.interactionManager.hasCreativeInventory()) {
            this.minecraft.openScreen(new CreativePlayerInventoryScreen(this.minecraft.player));
            return;
        }
        super.init();
        this.isNarrow = (this.width < 379);
        this.recipeBook.initialize(this.width, this.height, this.minecraft, this.isNarrow, this.container);
        this.isOpen = true;
        this.left = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
        this.children.add(this.recipeBook);
        this.setInitialFocus(this.recipeBook);
        this.<RecipeBookButtonWidget>addButton(new RecipeBookButtonWidget(this.left + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, PlayerInventoryScreen.RECIPE_BUTTON_TEX, buttonWidget -> {
            this.recipeBook.reset(this.isNarrow);
            this.recipeBook.toggleOpen();
            this.left = this.recipeBook.findLeftEdge(this.isNarrow, this.width, this.containerWidth);
            buttonWidget.setPos(this.left + 104, this.height / 2 - 22);
            this.isMouseDown = true;
        }));
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        this.font.draw(this.title.getFormattedText(), 97.0f, 8.0f, 4210752);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.offsetGuiForEffects = !this.recipeBook.isOpen();
        if (this.recipeBook.isOpen() && this.isNarrow) {
            this.drawBackground(delta, mouseX, mouseY);
            this.recipeBook.render(mouseX, mouseY, delta);
        }
        else {
            this.recipeBook.render(mouseX, mouseY, delta);
            super.render(mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(this.left, this.top, false, delta);
        }
        this.drawMouseoverTooltip(mouseX, mouseY);
        this.recipeBook.drawTooltip(this.left, this.top, mouseX, mouseY);
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        this.focusOn(this.recipeBook);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(PlayerInventoryScreen.BACKGROUND_TEXTURE);
        final int integer4 = this.left;
        final int integer5 = this.top;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        drawEntity(integer4 + 51, integer5 + 75, 30, integer4 + 51 - this.mouseX, integer5 + 75 - 50 - this.mouseY, this.minecraft.player);
    }
    
    public static void drawEntity(final int integer1, final int integer2, final int integer3, final float float4, final float float5, final LivingEntity entity) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)integer1, (float)integer2, 50.0f);
        GlStateManager.scalef((float)(-integer3), (float)integer3, (float)integer3);
        GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        final float float6 = entity.aK;
        final float float7 = entity.yaw;
        final float float8 = entity.pitch;
        final float float9 = entity.prevHeadYaw;
        final float float10 = entity.headYaw;
        GlStateManager.rotatef(135.0f, 0.0f, 1.0f, 0.0f);
        GuiLighting.enable();
        GlStateManager.rotatef(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-(float)Math.atan(float5 / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        entity.aK = (float)Math.atan(float4 / 40.0f) * 20.0f;
        entity.yaw = (float)Math.atan(float4 / 40.0f) * 40.0f;
        entity.pitch = -(float)Math.atan(float5 / 40.0f) * 20.0f;
        entity.headYaw = entity.yaw;
        entity.prevHeadYaw = entity.yaw;
        GlStateManager.translatef(0.0f, 0.0f, 0.0f);
        final EntityRenderDispatcher entityRenderDispatcher12 = MinecraftClient.getInstance().getEntityRenderManager();
        entityRenderDispatcher12.a(180.0f);
        entityRenderDispatcher12.setRenderShadows(false);
        entityRenderDispatcher12.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        entityRenderDispatcher12.setRenderShadows(true);
        entity.aK = float6;
        entity.yaw = float7;
        entity.pitch = float8;
        entity.prevHeadYaw = float9;
        entity.headYaw = float10;
        GlStateManager.popMatrix();
        GuiLighting.disable();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }
    
    @Override
    protected boolean isPointWithinBounds(final int xPosition, final int yPosition, final int width, final int height, final double pointX, final double pointY) {
        return (!this.isNarrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(xPosition, yPosition, width, height, pointX, pointY);
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        return this.recipeBook.mouseClicked(mouseX, mouseY, button) || ((!this.isNarrow || !this.recipeBook.isOpen()) && super.mouseClicked(mouseX, mouseY, button));
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (this.isMouseDown) {
            this.isMouseDown = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    protected boolean isClickOutsideBounds(final double mouseX, final double mouseY, final int left, final int top, final int button) {
        final boolean boolean8 = mouseX < left || mouseY < top || mouseX >= left + this.containerWidth || mouseY >= top + this.containerHeight;
        return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.left, this.top, this.containerWidth, this.containerHeight, button) && boolean8;
    }
    
    @Override
    protected void onMouseClick(final Slot slot, final int invSlot, final int button, final SlotActionType slotActionType) {
        super.onMouseClick(slot, invSlot, button, slotActionType);
        this.recipeBook.slotClicked(slot);
    }
    
    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }
    
    @Override
    public void removed() {
        if (this.isOpen) {
            this.recipeBook.close();
        }
        super.removed();
    }
    
    @Override
    public RecipeBookGui getRecipeBookGui() {
        return this.recipeBook;
    }
    
    static {
        RECIPE_BUTTON_TEX = new Identifier("textures/gui/recipe_button.png");
    }
}
