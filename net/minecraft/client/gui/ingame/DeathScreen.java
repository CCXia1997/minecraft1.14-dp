package net.minecraft.client.gui.ingame;

import net.minecraft.text.event.ClickEvent;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.text.TextFormat;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.CloseWorldScreen;
import java.util.Iterator;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.StringTextComponent;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableTextComponent;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class DeathScreen extends Screen
{
    private int ticksSinceDeath;
    private final TextComponent message;
    private final boolean c;
    
    public DeathScreen(@Nullable final TextComponent message, final boolean boolean2) {
        super(new TranslatableTextComponent(boolean2 ? "deathScreen.title.hardcore" : "deathScreen.title", new Object[0]));
        this.message = message;
        this.c = boolean2;
    }
    
    @Override
    protected void init() {
        this.ticksSinceDeath = 0;
        String string1;
        String string2;
        if (this.c) {
            string1 = I18n.translate("deathScreen.spectate");
            string2 = I18n.translate("deathScreen." + (this.minecraft.isInSingleplayer() ? "deleteWorld" : "leaveServer"));
        }
        else {
            string1 = I18n.translate("deathScreen.respawn");
            string2 = I18n.translate("deathScreen.titleScreen");
        }
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, string1, buttonWidget -> {
            this.minecraft.player.requestRespawn();
            this.minecraft.openScreen(null);
            return;
        }));
        final YesNoScreen yesNoScreen3;
        YesNoScreen yesNoScreen2;
        final ButtonWidget buttonWidget3 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96, 200, 20, string2, buttonWidget -> {
            if (this.c) {
                this.minecraft.openScreen(new MainMenuScreen());
                return;
            }
            else {
                new YesNoScreen(this::a, new TranslatableTextComponent("deathScreen.quit.confirm", new Object[0]), new StringTextComponent(""), I18n.translate("deathScreen.titleScreen"), I18n.translate("deathScreen.respawn"));
                yesNoScreen2 = yesNoScreen3;
                this.minecraft.openScreen(yesNoScreen2);
                yesNoScreen2.disableButtons(20);
                return;
            }
        }));
        if (!this.c && this.minecraft.getSession() == null) {
            buttonWidget3.active = false;
        }
        for (final AbstractButtonWidget abstractButtonWidget5 : this.buttons) {
            abstractButtonWidget5.active = false;
        }
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    private void a(final boolean boolean1) {
        if (boolean1) {
            if (this.minecraft.world != null) {
                this.minecraft.world.disconnect();
            }
            this.minecraft.disconnect(new CloseWorldScreen(new TranslatableTextComponent("menu.savingLevel", new Object[0])));
            this.minecraft.openScreen(new MainMenuScreen());
        }
        else {
            this.minecraft.player.requestRespawn();
            this.minecraft.openScreen(null);
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2 / 2, 30, 16777215);
        GlStateManager.popMatrix();
        if (this.message != null) {
            this.drawCenteredString(this.font, this.message.getFormattedText(), this.width / 2, 85, 16777215);
        }
        this.drawCenteredString(this.font, I18n.translate("deathScreen.score") + ": " + TextFormat.o + this.minecraft.player.getScore(), this.width / 2, 100, 16777215);
        if (this.message != null && mouseY > 85) {
            final int n = 85;
            this.font.getClass();
            if (mouseY < n + 9) {
                final TextComponent textComponent4 = this.a(mouseX);
                if (textComponent4 != null && textComponent4.getStyle().getHoverEvent() != null) {
                    this.renderComponentHoverEffect(textComponent4, mouseX, mouseY);
                }
            }
        }
        super.render(mouseX, mouseY, delta);
    }
    
    @Nullable
    public TextComponent a(final int integer) {
        if (this.message == null) {
            return null;
        }
        final int integer2 = this.minecraft.textRenderer.getStringWidth(this.message.getFormattedText());
        final int integer3 = this.width / 2 - integer2 / 2;
        final int integer4 = this.width / 2 + integer2 / 2;
        int integer5 = integer3;
        if (integer < integer3 || integer > integer4) {
            return null;
        }
        for (final TextComponent textComponent7 : this.message) {
            integer5 += this.minecraft.textRenderer.getStringWidth(TextComponentUtil.a(textComponent7.getText(), false));
            if (integer5 > integer) {
                return textComponent7;
            }
        }
        return null;
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (this.message != null && mouseY > 85.0) {
            final int n = 85;
            this.font.getClass();
            if (mouseY < n + 9) {
                final TextComponent textComponent6 = this.a((int)mouseX);
                if (textComponent6 != null && textComponent6.getStyle().getClickEvent() != null && textComponent6.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
                    this.handleComponentClicked(textComponent6);
                    return false;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        ++this.ticksSinceDeath;
        if (this.ticksSinceDeath == 20) {
            for (final AbstractButtonWidget abstractButtonWidget2 : this.buttons) {
                abstractButtonWidget2.active = true;
            }
        }
    }
}
