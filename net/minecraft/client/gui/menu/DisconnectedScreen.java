package net.minecraft.client.gui.menu;

import java.util.Iterator;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;
import java.util.List;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class DisconnectedScreen extends Screen
{
    private final TextComponent reason;
    private List<String> reasonFormatted;
    private final Screen parent;
    private int reasonHeight;
    
    public DisconnectedScreen(final Screen parent, final String title, final TextComponent reason) {
        super(new TranslatableTextComponent(title, new Object[0]));
        this.parent = parent;
        this.reason = reason;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    protected void init() {
        this.reasonFormatted = this.font.wrapStringToWidthAsList(this.reason.getFormattedText(), this.width - 50);
        final int size = this.reasonFormatted.size();
        this.font.getClass();
        this.reasonHeight = size * 9;
        final int x = this.width / 2 - 100;
        final int n = this.height / 2 + this.reasonHeight / 2;
        this.font.getClass();
        this.<ButtonWidget>addButton(new ButtonWidget(x, Math.min(n + 9, this.height - 30), 200, 20, I18n.translate("gui.toMenu"), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        final TextRenderer font = this.font;
        final String formattedText = this.title.getFormattedText();
        final int centerX = this.width / 2;
        final int n = this.height / 2 - this.reasonHeight / 2;
        this.font.getClass();
        this.drawCenteredString(font, formattedText, centerX, n - 9 * 2, 11184810);
        int integer4 = this.height / 2 - this.reasonHeight / 2;
        if (this.reasonFormatted != null) {
            for (final String string6 : this.reasonFormatted) {
                this.drawCenteredString(this.font, string6, this.width / 2, integer4, 16777215);
                final int n2 = integer4;
                this.font.getClass();
                integer4 = n2 + 9;
            }
        }
        super.render(mouseX, mouseY, delta);
    }
}
