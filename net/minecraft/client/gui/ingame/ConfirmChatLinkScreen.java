package net.minecraft.client.gui.ingame;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.menu.YesNoScreen;

@Environment(EnvType.CLIENT)
public class ConfirmChatLinkScreen extends YesNoScreen
{
    private final String warning;
    private final String copy;
    private final String link;
    private final boolean drawWarning;
    
    public ConfirmChatLinkScreen(final BooleanConsumer booleanConsumer, final String link, final boolean boolean3) {
        super(booleanConsumer, new TranslatableTextComponent(boolean3 ? "chat.link.confirmTrusted" : "chat.link.confirm", new Object[0]), new StringTextComponent(link));
        this.yesTranslated = I18n.translate(boolean3 ? "chat.link.open" : "gui.yes");
        this.noTranslated = I18n.translate(boolean3 ? "gui.cancel" : "gui.no");
        this.copy = I18n.translate("chat.copy");
        this.warning = I18n.translate("chat.link.warning");
        this.drawWarning = !boolean3;
        this.link = link;
    }
    
    @Override
    protected void init() {
        super.init();
        this.buttons.clear();
        this.children.clear();
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesTranslated, buttonWidget -> this.callback.accept(true)));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copy, buttonWidget -> {
            this.copyToClipboard();
            this.callback.accept(false);
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noTranslated, buttonWidget -> this.callback.accept(false)));
    }
    
    public void copyToClipboard() {
        this.minecraft.keyboard.setClipboard(this.link);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        super.render(mouseX, mouseY, delta);
        if (this.drawWarning) {
            this.drawCenteredString(this.font, this.warning, this.width / 2, 110, 16764108);
        }
    }
}
