package net.minecraft.realms;

import java.util.Iterator;
import java.util.List;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DisconnectedRealmsScreen extends RealmsScreen
{
    private final String title;
    private final TextComponent reason;
    private List<String> lines;
    private final RealmsScreen parent;
    private int textHeight;
    
    public DisconnectedRealmsScreen(final RealmsScreen realmsScreen, final String string, final TextComponent textComponent) {
        this.parent = realmsScreen;
        this.title = RealmsScreen.getLocalizedString(string);
        this.reason = textComponent;
    }
    
    @Override
    public void init() {
        Realms.setConnectedToRealms(false);
        Realms.clearResourcePack();
        Realms.narrateNow(this.title + ": " + this.reason.getString());
        this.lines = this.fontSplit(this.reason.getFormattedText(), this.width() - 50);
        this.textHeight = this.lines.size() * this.fontLineHeight();
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 2 + this.textHeight / 2 + this.fontLineHeight(), RealmsScreen.getLocalizedString("gui.back")) {
            @Override
            public void onPress() {
                Realms.setScreen(DisconnectedRealmsScreen.this.parent);
            }
        });
    }
    
    @Override
    public boolean keyPressed(final int integer1, final int integer2, final int integer3) {
        if (integer1 == 256) {
            Realms.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(integer1, integer2, integer3);
    }
    
    @Override
    public void render(final int integer1, final int integer2, final float float3) {
        this.renderBackground();
        this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - this.textHeight / 2 - this.fontLineHeight() * 2, 11184810);
        int integer3 = this.height() / 2 - this.textHeight / 2;
        if (this.lines != null) {
            for (final String string6 : this.lines) {
                this.drawCenteredString(string6, this.width() / 2, integer3, 16777215);
                integer3 += this.fontLineHeight();
            }
        }
        super.render(integer1, integer2, float3);
    }
}
