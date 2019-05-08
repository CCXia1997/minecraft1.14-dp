package net.minecraft.client.gui.menu.options;

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
public class ChatOptionsScreen extends Screen
{
    private static final GameOption[] OPTIONS;
    private final Screen parent;
    private final GameOptions options;
    private AbstractButtonWidget narratorOptionButton;
    
    public ChatOptionsScreen(final Screen parent, final GameOptions options) {
        super(new TranslatableTextComponent("options.chat.title", new Object[0]));
        this.parent = parent;
        this.options = options;
    }
    
    @Override
    protected void init() {
        int integer1 = 0;
        for (final GameOption gameOption5 : ChatOptionsScreen.OPTIONS) {
            final int integer2 = this.width / 2 - 155 + integer1 % 2 * 160;
            final int integer3 = this.height / 6 + 24 * (integer1 >> 1);
            final AbstractButtonWidget abstractButtonWidget8 = this.<AbstractButtonWidget>addButton(gameOption5.createOptionButton(this.minecraft.options, integer2, integer3, 150));
            if (gameOption5 == GameOption.NARRATOR) {
                this.narratorOptionButton = abstractButtonWidget8;
                abstractButtonWidget8.active = NarratorManager.INSTANCE.isActive();
            }
            ++integer1;
        }
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24 * (integer1 + 1) / 2, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.parent)));
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
        this.narratorOptionButton.setMessage(GameOption.NARRATOR.get(this.options));
    }
    
    static {
        OPTIONS = new GameOption[] { GameOption.VISIBILITY, GameOption.CHAT_COLOR, GameOption.CHAT_LINKS, GameOption.CHAT_LINKS_PROMPT, GameOption.CHAT_OPACITY, GameOption.TEXT_BACKGROUND_OPACITY, GameOption.CHAT_SCALE, GameOption.CHAT_WIDTH, GameOption.CHAT_HEIGHT_FOCUSED, GameOption.SATURATION, GameOption.REDUCED_DEBUG_INFO, GameOption.AUTO_SUGGESTIONS, GameOption.NARRATOR };
    }
}
