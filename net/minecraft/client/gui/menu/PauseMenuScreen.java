package net.minecraft.client.gui.menu;

import net.minecraft.util.SystemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.CloseWorldScreen;
import net.minecraft.client.gui.ingame.ConfirmChatLinkScreen;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class PauseMenuScreen extends Screen
{
    public PauseMenuScreen() {
        super(new TranslatableTextComponent("menu.game", new Object[0]));
    }
    
    @Override
    protected void init() {
        final int integer1 = -16;
        final int integer2 = 98;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 24 - 16, 204, 20, I18n.translate("menu.returnToGame"), buttonWidget -> {
            this.minecraft.openScreen(null);
            this.minecraft.mouse.lockCursor();
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 - 16, 98, 20, I18n.translate("gui.advancements"), buttonWidget -> this.minecraft.openScreen(new AdvancementsScreen(this.minecraft.player.networkHandler.getAdvancementHandler()))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 48 - 16, 98, 20, I18n.translate("gui.stats"), buttonWidget -> this.minecraft.openScreen(new StatsScreen(this, this.minecraft.player.getStats()))));
        final String string3 = SharedConstants.getGameVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";
        final String link;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 72 - 16, 98, 20, I18n.translate("menu.sendFeedback"), buttonWidget -> this.minecraft.openScreen(new ConfirmChatLinkScreen(boolean2 -> {
            if (boolean2) {
                SystemUtil.getOperatingSystem().open(link);
            }
            this.minecraft.openScreen(this);
        }, link, true))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 72 - 16, 98, 20, I18n.translate("menu.reportBugs"), buttonWidget -> this.minecraft.openScreen(new ConfirmChatLinkScreen(boolean1 -> {
            if (boolean1) {
                SystemUtil.getOperatingSystem().open("https://aka.ms/snapshotbugs?ref=game");
            }
            this.minecraft.openScreen(this);
        }, "https://aka.ms/snapshotbugs?ref=game", true))));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 96 - 16, 98, 20, I18n.translate("menu.options"), buttonWidget -> this.minecraft.openScreen(new SettingsScreen(this, this.minecraft.options))));
        final ButtonWidget buttonWidget2 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 96 - 16, 98, 20, I18n.translate("menu.shareToLan"), buttonWidget -> this.minecraft.openScreen(new OpenToLanScreen(this))));
        buttonWidget2.active = (this.minecraft.isIntegratedServerRunning() && !this.minecraft.getServer().isRemote());
        final boolean boolean2;
        final boolean boolean3;
        MinecraftClient minecraft;
        final CloseWorldScreen screen;
        RealmsBridge realmsBridge4;
        MinecraftClient minecraft2;
        final MultiplayerScreen screen2;
        final ButtonWidget buttonWidget3 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 - 16, 204, 20, I18n.translate("menu.returnToMenu"), buttonWidget -> {
            boolean2 = this.minecraft.isInSingleplayer();
            boolean3 = this.minecraft.isConnectedToRealms();
            buttonWidget.active = false;
            this.minecraft.world.disconnect();
            if (boolean2) {
                minecraft = this.minecraft;
                new CloseWorldScreen(new TranslatableTextComponent("menu.savingLevel", new Object[0]));
                minecraft.disconnect(screen);
            }
            else {
                this.minecraft.disconnect();
            }
            if (boolean2) {
                this.minecraft.openScreen(new MainMenuScreen());
            }
            else if (boolean3) {
                realmsBridge4 = new RealmsBridge();
                realmsBridge4.switchToRealms(new MainMenuScreen());
            }
            else {
                minecraft2 = this.minecraft;
                new MultiplayerScreen(new MainMenuScreen());
                minecraft2.openScreen(screen2);
            }
            return;
        }));
        if (!this.minecraft.isInSingleplayer()) {
            buttonWidget3.setMessage(I18n.translate("menu.disconnect"));
        }
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
        super.render(mouseX, mouseY, delta);
    }
}
