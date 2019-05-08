package net.minecraft.client.gui.menu;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.client.gui.widget.ButtonWidget;
import com.google.common.collect.Lists;
import net.minecraft.client.resource.language.I18n;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class YesNoScreen extends Screen
{
    private final TextComponent message;
    private final List<String> messageSplit;
    protected String yesTranslated;
    protected String noTranslated;
    private int buttonEnableTimer;
    protected final BooleanConsumer callback;
    
    public YesNoScreen(final BooleanConsumer booleanConsumer, final TextComponent textComponent2, final TextComponent textComponent3) {
        this(booleanConsumer, textComponent2, textComponent3, I18n.translate("gui.yes"), I18n.translate("gui.no"));
    }
    
    public YesNoScreen(final BooleanConsumer booleanConsumer, final TextComponent textComponent2, final TextComponent textComponent3, final String string4, final String string5) {
        super(textComponent2);
        this.messageSplit = Lists.newArrayList();
        this.callback = booleanConsumer;
        this.message = textComponent3;
        this.yesTranslated = string4;
        this.noTranslated = string5;
    }
    
    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.message.getString();
    }
    
    @Override
    protected void init() {
        super.init();
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesTranslated, buttonWidget -> this.callback.accept(true)));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noTranslated, buttonWidget -> this.callback.accept(false)));
        this.messageSplit.clear();
        this.messageSplit.addAll(this.font.wrapStringToWidthAsList(this.message.getFormattedText(), this.width - 50));
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 16777215);
        int integer4 = 90;
        for (final String string6 : this.messageSplit) {
            this.drawCenteredString(this.font, string6, this.width / 2, integer4, 16777215);
            final int n = integer4;
            this.font.getClass();
            integer4 = n + 9;
        }
        super.render(mouseX, mouseY, delta);
    }
    
    public void disableButtons(final int integer) {
        this.buttonEnableTimer = integer;
        for (final AbstractButtonWidget abstractButtonWidget3 : this.buttons) {
            abstractButtonWidget3.active = false;
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        final int buttonEnableTimer = this.buttonEnableTimer - 1;
        this.buttonEnableTimer = buttonEnableTimer;
        if (buttonEnableTimer == 0) {
            for (final AbstractButtonWidget abstractButtonWidget2 : this.buttons) {
                abstractButtonWidget2.active = true;
            }
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
