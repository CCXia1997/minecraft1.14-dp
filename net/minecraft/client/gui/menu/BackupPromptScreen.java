package net.minecraft.client.gui.menu;

import java.util.Iterator;
import net.minecraft.client.gui.widget.ButtonWidget;
import java.util.Collection;
import net.minecraft.client.resource.language.I18n;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.widget.CheckboxWidget;
import java.util.List;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class BackupPromptScreen extends Screen
{
    private final Screen parent;
    protected final Callback callback;
    private final TextComponent subtitle;
    private final boolean showEraseCacheCheckbox;
    private final List<String> wrappedText;
    private final String eraseCacheText;
    private final String confirmText;
    private final String skipText;
    private final String cancelText;
    private CheckboxWidget eraseCacheCheckbox;
    
    public BackupPromptScreen(final Screen parent, final Callback callback, final TextComponent title, final TextComponent subtitle, final boolean showEraseCacheCheckBox) {
        super(title);
        this.wrappedText = Lists.newArrayList();
        this.parent = parent;
        this.callback = callback;
        this.subtitle = subtitle;
        this.showEraseCacheCheckbox = showEraseCacheCheckBox;
        this.eraseCacheText = I18n.translate("selectWorld.backupEraseCache");
        this.confirmText = I18n.translate("selectWorld.backupJoinConfirmButton");
        this.skipText = I18n.translate("selectWorld.backupJoinSkipButton");
        this.cancelText = I18n.translate("gui.cancel");
    }
    
    @Override
    protected void init() {
        super.init();
        this.wrappedText.clear();
        this.wrappedText.addAll(this.font.wrapStringToWidthAsList(this.subtitle.getFormattedText(), this.width - 50));
        final int n = this.wrappedText.size() + 1;
        this.font.getClass();
        final int integer1 = n * 9;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, 100 + integer1, 150, 20, this.confirmText, buttonWidget -> this.callback.proceed(true, this.eraseCacheCheckbox.isChecked())));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155 + 160, 100 + integer1, 150, 20, this.skipText, buttonWidget -> this.callback.proceed(false, this.eraseCacheCheckbox.isChecked())));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155 + 80, 124 + integer1, 150, 20, this.cancelText, buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.eraseCacheCheckbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + integer1, 150, 20, this.eraseCacheText, false);
        if (this.showEraseCacheCheckbox) {
            this.<CheckboxWidget>addButton(this.eraseCacheCheckbox);
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 50, 16777215);
        int integer4 = 70;
        for (final String string6 : this.wrappedText) {
            this.drawCenteredString(this.font, string6, this.width / 2, integer4, 16777215);
            final int n = integer4;
            this.font.getClass();
            integer4 = n + 9;
        }
        super.render(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == 256) {
            this.minecraft.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Environment(EnvType.CLIENT)
    public interface Callback
    {
        void proceed(final boolean arg1, final boolean arg2);
    }
}
