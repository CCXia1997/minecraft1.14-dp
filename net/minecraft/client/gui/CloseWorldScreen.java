package net.minecraft.client.gui;

import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CloseWorldScreen extends Screen
{
    public CloseWorldScreen(final TextComponent title) {
        super(title);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderDirtBackground(0);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 16777215);
        super.render(mouseX, mouseY, delta);
    }
}
