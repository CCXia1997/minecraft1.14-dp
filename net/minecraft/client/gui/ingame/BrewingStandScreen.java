package net.minecraft.client.gui.ingame;

import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class BrewingStandScreen extends ContainerScreen<BrewingStandContainer>
{
    private static final Identifier TEXTURE;
    private static final int[] l;
    
    public BrewingStandScreen(final BrewingStandContainer brewingStandContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(brewingStandContainer, playerInventory, textComponent);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        this.font.draw(this.title.getFormattedText(), (float)(this.containerWidth / 2 - this.font.getStringWidth(this.title.getFormattedText()) / 2), 6.0f, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, (float)(this.containerHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BrewingStandScreen.TEXTURE);
        final int integer4 = (this.width - this.containerWidth) / 2;
        final int integer5 = (this.height - this.containerHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        final int integer6 = ((BrewingStandContainer)this.container).getFuel();
        final int integer7 = MathHelper.clamp((18 * integer6 + 20 - 1) / 20, 0, 18);
        if (integer7 > 0) {
            this.blit(integer4 + 60, integer5 + 44, 176, 29, integer7, 4);
        }
        final int integer8 = ((BrewingStandContainer)this.container).getBrewTime();
        if (integer8 > 0) {
            int integer9 = (int)(28.0f * (1.0f - integer8 / 400.0f));
            if (integer9 > 0) {
                this.blit(integer4 + 97, integer5 + 16, 176, 0, 9, integer9);
            }
            integer9 = BrewingStandScreen.l[integer8 / 2 % 7];
            if (integer9 > 0) {
                this.blit(integer4 + 63, integer5 + 14 + 29 - integer9, 185, 29 - integer9, 12, integer9);
            }
        }
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/brewing_stand.png");
        l = new int[] { 29, 24, 20, 16, 11, 6, 0 };
    }
}
