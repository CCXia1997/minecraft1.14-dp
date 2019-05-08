package net.minecraft.client.gui.menu;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class NoticeScreen extends Screen
{
    private final Runnable actionHandler;
    protected final TextComponent notice;
    private final List<String> noticeLines;
    protected final String buttonString;
    private int e;
    
    public NoticeScreen(final Runnable actionHandler, final TextComponent title, final TextComponent notice) {
        this(actionHandler, title, notice, "gui.back");
    }
    
    public NoticeScreen(final Runnable actionHandler, final TextComponent title, final TextComponent notice, final String buttonString) {
        super(title);
        this.noticeLines = Lists.newArrayList();
        this.actionHandler = actionHandler;
        this.notice = notice;
        this.buttonString = I18n.translate(buttonString);
    }
    
    @Override
    protected void init() {
        super.init();
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.buttonString, buttonWidget -> this.actionHandler.run()));
        this.noticeLines.clear();
        this.noticeLines.addAll(this.font.wrapStringToWidthAsList(this.notice.getFormattedText(), this.width - 50));
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 16777215);
        int integer4 = 90;
        for (final String string6 : this.noticeLines) {
            this.drawCenteredString(this.font, string6, this.width / 2, integer4, 16777215);
            final int n = integer4;
            this.font.getClass();
            integer4 = n + 9;
        }
        super.render(mouseX, mouseY, delta);
    }
    
    @Override
    public void tick() {
        super.tick();
        final int e = this.e - 1;
        this.e = e;
        if (e == 0) {
            for (final AbstractButtonWidget abstractButtonWidget2 : this.buttons) {
                abstractButtonWidget2.active = true;
            }
        }
    }
}
