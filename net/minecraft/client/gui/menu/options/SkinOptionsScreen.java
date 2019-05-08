package net.minecraft.client.gui.menu.options;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class SkinOptionsScreen extends Screen
{
    private final Screen parent;
    
    public SkinOptionsScreen(final Screen parent) {
        super(new TranslatableTextComponent("options.skinCustomisation.title", new Object[0]));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        int integer1 = 0;
        for (final PlayerModelPart playerModelPart5 : PlayerModelPart.values()) {
            final PlayerModelPart playerModelPart6;
            this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155 + integer1 % 2 * 160, this.height / 6 + 24 * (integer1 >> 1), 150, 20, this.getPlayerModelPartDisplayString(playerModelPart5), buttonWidget -> {
                this.minecraft.options.togglePlayerModelPart(playerModelPart6);
                buttonWidget.setMessage(this.getPlayerModelPartDisplayString(playerModelPart6));
                return;
            }));
            ++integer1;
        }
        this.<OptionButtonWidget>addButton(new OptionButtonWidget(this.width / 2 - 155 + integer1 % 2 * 160, this.height / 6 + 24 * (integer1 >> 1), 150, 20, GameOption.MAIN_HAND, GameOption.MAIN_HAND.get(this.minecraft.options), buttonWidget -> {
            GameOption.MAIN_HAND.a(this.minecraft.options, 1);
            this.minecraft.options.write();
            buttonWidget.setMessage(GameOption.MAIN_HAND.get(this.minecraft.options));
            this.minecraft.options.onPlayerModelPartChange();
            return;
        }));
        if (++integer1 % 2 == 1) {
            ++integer1;
        }
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24 * (integer1 >> 1), 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.parent)));
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
    
    private String getPlayerModelPartDisplayString(final PlayerModelPart part) {
        String string2;
        if (this.minecraft.options.getEnabledPlayerModelParts().contains(part)) {
            string2 = I18n.translate("options.on");
        }
        else {
            string2 = I18n.translate("options.off");
        }
        return part.getLocalizedName().getFormattedText() + ": " + string2;
    }
}
