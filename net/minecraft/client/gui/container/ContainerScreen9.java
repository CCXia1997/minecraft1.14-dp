package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class ContainerScreen9 extends ContainerScreen<Generic3x3Container>
{
    private static final Identifier TEXTURE;
    
    public ContainerScreen9(final Generic3x3Container generic3x3Container, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(generic3x3Container, playerInventory, textComponent);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
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
        this.minecraft.getTextureManager().bindTexture(ContainerScreen9.TEXTURE);
        final int integer4 = (this.width - this.containerWidth) / 2;
        final int integer5 = (this.height - this.containerHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/dispenser.png");
    }
}
