package net.minecraft.client.gui.container;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ContainerProvider;
import net.minecraft.container.GenericContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class ContainerScreen54 extends ContainerScreen<GenericContainer> implements ContainerProvider<GenericContainer>
{
    private static final Identifier TEXTURE;
    private final int rows;
    
    public ContainerScreen54(final GenericContainer genericContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(genericContainer, playerInventory, textComponent);
        this.passEvents = false;
        final int integer4 = 222;
        final int integer5 = 114;
        this.rows = genericContainer.getRows();
        this.containerHeight = 114 + this.rows * 18;
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
        this.minecraft.getTextureManager().bindTexture(ContainerScreen54.TEXTURE);
        final int integer4 = (this.width - this.containerWidth) / 2;
        final int integer5 = (this.height - this.containerHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.rows * 18 + 17);
        this.blit(integer4, integer5 + this.rows * 18 + 17, 0, 126, this.containerWidth, 96);
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    }
}
