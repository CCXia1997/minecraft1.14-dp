package net.minecraft.client.gui.container;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.StonecutterContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class StonecutterScreen extends ContainerScreen<StonecutterContainer>
{
    private static final Identifier TEXTURE;
    private float scrollAmount;
    private boolean mouseClicked;
    private int scrollOffset;
    private boolean canCraft;
    
    public StonecutterScreen(final StonecutterContainer stonecutterContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(stonecutterContainer, playerInventory, textComponent);
        stonecutterContainer.setContentsChangedListener(this::onInventoryChange);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        this.font.draw(this.title.getFormattedText(), 8.0f, 4.0f, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, (float)(this.containerHeight - 94), 4210752);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(StonecutterScreen.TEXTURE);
        final int integer4 = this.left;
        final int integer5 = this.top;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        final int integer6 = (int)(41.0f * this.scrollAmount);
        this.blit(integer4 + 119, integer5 + 15 + integer6, 176 + (this.shouldScroll() ? 0 : 12), 0, 12, 15);
        final int integer7 = this.left + 52;
        final int integer8 = this.top + 14;
        final int integer9 = this.scrollOffset + 12;
        this.a(mouseX, mouseY, integer7, integer8, integer9);
        this.b(integer7, integer8, integer9);
    }
    
    private void a(final int integer1, final int integer2, final int integer3, final int integer4, final int integer5) {
        for (int integer6 = this.scrollOffset; integer6 < integer5 && integer6 < ((StonecutterContainer)this.container).getAvailableRecipeCount(); ++integer6) {
            final int integer7 = integer6 - this.scrollOffset;
            final int integer8 = integer3 + integer7 % 4 * 16;
            final int integer9 = integer7 / 4;
            final int integer10 = integer4 + integer9 * 18 + 2;
            int integer11 = this.containerHeight;
            if (integer6 == ((StonecutterContainer)this.container).getSelectedRecipe()) {
                integer11 += 18;
            }
            else if (integer1 >= integer8 && integer2 >= integer10 && integer1 < integer8 + 16 && integer2 < integer10 + 18) {
                integer11 += 36;
            }
            this.blit(integer8, integer10 - 1, 0, integer11, 16, 18);
        }
    }
    
    private void b(final int integer1, final int integer2, final int integer3) {
        GuiLighting.enableForItems();
        final List<StonecuttingRecipe> list4 = ((StonecutterContainer)this.container).getAvailableRecipes();
        for (int integer4 = this.scrollOffset; integer4 < integer3 && integer4 < ((StonecutterContainer)this.container).getAvailableRecipeCount(); ++integer4) {
            final int integer5 = integer4 - this.scrollOffset;
            final int integer6 = integer1 + integer5 % 4 * 16;
            final int integer7 = integer5 / 4;
            final int integer8 = integer2 + integer7 * 18 + 2;
            this.minecraft.getItemRenderer().renderGuiItem(list4.get(integer4).getOutput(), integer6, integer8);
        }
        GuiLighting.disable();
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        this.mouseClicked = false;
        if (this.canCraft) {
            int integer6 = this.left + 52;
            int integer7 = this.top + 14;
            for (int integer8 = this.scrollOffset + 12, integer9 = this.scrollOffset; integer9 < integer8; ++integer9) {
                final int integer10 = integer9 - this.scrollOffset;
                final double double11 = mouseX - (integer6 + integer10 % 4 * 16);
                final double double12 = mouseY - (integer7 + integer10 / 4 * 18);
                if (double11 >= 0.0 && double12 >= 0.0 && double11 < 16.0 && double12 < 18.0 && ((StonecutterContainer)this.container).onButtonClick(this.minecraft.player, integer9)) {
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.mm, 1.0f));
                    this.minecraft.interactionManager.clickButton(((StonecutterContainer)this.container).syncId, integer9);
                    return true;
                }
            }
            integer6 = this.left + 119;
            integer7 = this.top + 9;
            if (mouseX >= integer6 && mouseX < integer6 + 12 && mouseY >= integer7 && mouseY < integer7 + 54) {
                this.mouseClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (this.mouseClicked && this.shouldScroll()) {
            final int integer10 = this.top + 14;
            final int integer11 = integer10 + 54;
            this.scrollAmount = ((float)mouseY - integer10 - 7.5f) / (integer11 - integer10 - 15.0f);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0f, 1.0f);
            this.scrollOffset = (int)(this.scrollAmount * this.getMaxScroll() + 0.5) * 4;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (this.shouldScroll()) {
            final int integer7 = this.getMaxScroll();
            this.scrollAmount -= (float)(amount / integer7);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0f, 1.0f);
            this.scrollOffset = (int)(this.scrollAmount * integer7 + 0.5) * 4;
        }
        return true;
    }
    
    private boolean shouldScroll() {
        return this.canCraft && ((StonecutterContainer)this.container).getAvailableRecipeCount() > 12;
    }
    
    protected int getMaxScroll() {
        return (((StonecutterContainer)this.container).getAvailableRecipeCount() + 4 - 1) / 4 - 3;
    }
    
    private void onInventoryChange() {
        if (!(this.canCraft = ((StonecutterContainer)this.container).canCraft())) {
            this.scrollAmount = 0.0f;
            this.scrollOffset = 0;
        }
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/stonecutter.png");
    }
}
