package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.HopperContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class HopperScreen extends ContainerScreen<HopperContainer>
{
    private static final Identifier TEXTURE;
    
    public HopperScreen(final HopperContainer hopperContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(hopperContainer, playerInventory, textComponent);
        this.passEvents = false;
        this.containerHeight = 133;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        this.font.draw(this.title.getFormattedText(), 8.0f, 6.0f, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, (float)(this.containerHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(HopperScreen.TEXTURE);
        final int integer4 = (this.width - this.containerWidth) / 2;
        final int integer5 = (this.height - this.containerHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/hopper.png");
    }
}
