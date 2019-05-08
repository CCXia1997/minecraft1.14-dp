package net.minecraft.client.gui.menu;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.GameOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class AccessibilityScreen extends Screen
{
    private static final GameOption[] OPTIONS;
    private final Screen parent;
    private final GameOptions gameOptions;
    private AbstractButtonWidget narratorButton;
    
    public AccessibilityScreen(final Screen parent, final GameOptions gameOptions) {
        super(new TranslatableTextComponent("options.accessibility.title", new Object[0]));
        this.parent = parent;
        this.gameOptions = gameOptions;
    }
    
    @Override
    protected void init() {
        int integer1 = 0;
        for (final GameOption gameOption5 : AccessibilityScreen.OPTIONS) {
            final int integer2 = this.width / 2 - 155 + integer1 % 2 * 160;
            final int integer3 = this.height / 6 + 24 * (integer1 >> 1);
            final AbstractButtonWidget abstractButtonWidget8 = this.<AbstractButtonWidget>addButton(gameOption5.createOptionButton(this.minecraft.options, integer2, integer3, 150));
            if (gameOption5 == GameOption.NARRATOR) {
                this.narratorButton = abstractButtonWidget8;
                abstractButtonWidget8.active = NarratorManager.INSTANCE.isActive();
            }
            ++integer1;
        }
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 144, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }
    
    @Override
    public void removed() {
        this.minecraft.options.write();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
        super.render(mouseX, mouseY, delta);
    }
    
    public void a() {
        this.narratorButton.setMessage(GameOption.NARRATOR.get(this.gameOptions));
    }
    
    static {
        OPTIONS = new GameOption[] { GameOption.NARRATOR, GameOption.SUBTITLES, GameOption.TEXT_BACKGROUND_OPACITY, GameOption.TEXT_BACKGROUND, GameOption.CHAT_OPACITY, GameOption.AUTO_JUMP };
    }
}
