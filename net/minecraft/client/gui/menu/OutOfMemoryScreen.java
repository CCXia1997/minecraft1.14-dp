package net.minecraft.client.gui.menu;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class OutOfMemoryScreen extends Screen
{
    public OutOfMemoryScreen() {
        super(new StringTextComponent("Out of memory!"));
    }
    
    @Override
    protected void init() {
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.toTitle"), buttonWidget -> this.minecraft.openScreen(new MainMenuScreen())));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20, I18n.translate("menu.quit"), buttonWidget -> this.minecraft.scheduleStop()));
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawString(this.font, "Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
        this.drawString(this.font, "This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 18, 10526880);
        this.drawString(this.font, "Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 27, 10526880);
        this.drawString(this.font, "memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 10526880);
        this.drawString(this.font, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 54, 10526880);
        this.drawString(this.font, "We've tried to free up enough memory to let you go back to", this.width / 2 - 140, this.height / 4 - 60 + 60 + 63, 10526880);
        this.drawString(this.font, "the main menu and back to playing, but this may not have worked.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 72, 10526880);
        this.drawString(this.font, "Please restart the game if you see this message again.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 81, 10526880);
        super.render(mouseX, mouseY, delta);
    }
}
