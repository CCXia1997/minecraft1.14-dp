package net.minecraft.client.gui.menu;

import net.minecraft.world.GameMode;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class OpenToLanScreen extends Screen
{
    private final Screen parent;
    private ButtonWidget buttonAllowCommands;
    private ButtonWidget buttonGameMode;
    private String gameMode;
    private boolean allowCommands;
    
    public OpenToLanScreen(final Screen parent) {
        super(new TranslatableTextComponent("lanServer.title", new Object[0]));
        this.gameMode = "survival";
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        final int integer2;
        final TranslatableTextComponent translatableTextComponent;
        TextComponent textComponent3;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("lanServer.start"), buttonWidget -> {
            this.minecraft.openScreen(null);
            integer2 = NetworkUtils.findLocalPort();
            if (this.minecraft.getServer().openToLan(GameMode.byName(this.gameMode), this.allowCommands, integer2)) {
                new TranslatableTextComponent("commands.publish.started", new Object[] { integer2 });
                textComponent3 = translatableTextComponent;
            }
            else {
                textComponent3 = new TranslatableTextComponent("commands.publish.failed", new Object[0]);
            }
            this.minecraft.inGameHud.getChatHud().addMessage(textComponent3);
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.buttonGameMode = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, I18n.translate("selectWorld.gameMode"), buttonWidget -> {
            if ("spectator".equals(this.gameMode)) {
                this.gameMode = "creative";
            }
            else if ("creative".equals(this.gameMode)) {
                this.gameMode = "adventure";
            }
            else if ("adventure".equals(this.gameMode)) {
                this.gameMode = "survival";
            }
            else {
                this.gameMode = "spectator";
            }
            this.updateButtonText();
            return;
        }));
        this.buttonAllowCommands = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, I18n.translate("selectWorld.allowCommands"), buttonWidget -> {
            this.allowCommands = !this.allowCommands;
            this.updateButtonText();
            return;
        }));
        this.updateButtonText();
    }
    
    private void updateButtonText() {
        this.buttonGameMode.setMessage(I18n.translate("selectWorld.gameMode") + ": " + I18n.translate("selectWorld.gameMode." + this.gameMode));
        this.buttonAllowCommands.setMessage(I18n.translate("selectWorld.allowCommands") + ' ' + I18n.translate(this.allowCommands ? "options.on" : "options.off"));
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 50, 16777215);
        this.drawCenteredString(this.font, I18n.translate("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
        super.render(mouseX, mouseY, delta);
    }
}
