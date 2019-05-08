package net.minecraft.client.gui.menu;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class DownloadingTerrainScreen extends Screen
{
    public DownloadingTerrainScreen() {
        super(NarratorManager.a);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderDirtBackground(0);
        this.drawCenteredString(this.font, I18n.translate("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
        super.render(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
