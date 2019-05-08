package net.minecraft.client.gui.menu.options;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.gui.widget.SoundSliderWidget;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.options.GameOptions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class AudioOptionsScreen extends Screen
{
    private final Screen parent;
    private final GameOptions options;
    
    public AudioOptionsScreen(final Screen parent, final GameOptions options) {
        super(new TranslatableTextComponent("options.sounds.title", new Object[0]));
        this.parent = parent;
        this.options = options;
    }
    
    @Override
    protected void init() {
        int integer1 = 0;
        this.<SoundSliderWidget>addButton(new SoundSliderWidget(this.minecraft, this.width / 2 - 155 + integer1 % 2 * 160, this.height / 6 - 12 + 24 * (integer1 >> 1), SoundCategory.a, 310));
        integer1 += 2;
        for (final SoundCategory soundCategory5 : SoundCategory.values()) {
            if (soundCategory5 != SoundCategory.a) {
                this.<SoundSliderWidget>addButton(new SoundSliderWidget(this.minecraft, this.width / 2 - 155 + integer1 % 2 * 160, this.height / 6 - 12 + 24 * (integer1 >> 1), soundCategory5, 150));
                ++integer1;
            }
        }
        this.<OptionButtonWidget>addButton(new OptionButtonWidget(this.width / 2 - 75, this.height / 6 - 12 + 24 * (++integer1 >> 1), 150, 20, GameOption.SUBTITLES, GameOption.SUBTITLES.getDisplayString(this.options), buttonWidget -> {
            GameOption.SUBTITLES.set(this.minecraft.options);
            buttonWidget.setMessage(GameOption.SUBTITLES.getDisplayString(this.minecraft.options));
            this.minecraft.options.write();
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }
    
    @Override
    public void removed() {
        this.minecraft.options.write();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 16777215);
        super.render(mouseX, mouseY, delta);
    }
}
