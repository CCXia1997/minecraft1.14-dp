package net.minecraft.client.gui.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import java.net.IDN;
import net.minecraft.util.ChatUtil;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.function.Predicate;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerEntry;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class AddServerScreen extends Screen
{
    private ButtonWidget buttonAdd;
    private final BooleanConsumer callback;
    private final ServerEntry serverEntry;
    private TextFieldWidget addressField;
    private TextFieldWidget serverNameField;
    private ButtonWidget resourcePackOptionButton;
    private final Predicate<String> g;
    
    public AddServerScreen(final BooleanConsumer callback, final ServerEntry serverEntry) {
        super(new TranslatableTextComponent("addServer.title", new Object[0]));
        String[] arr2;
        String string2;
        this.g = (string -> {
            if (ChatUtil.isEmpty(string)) {
                return true;
            }
            else {
                arr2 = string.split(":");
                if (arr2.length == 0) {
                    return true;
                }
                else {
                    try {
                        string2 = IDN.toASCII(arr2[0]);
                        return true;
                    }
                    catch (IllegalArgumentException illegalArgumentException3) {
                        return false;
                    }
                }
            }
        });
        this.callback = callback;
        this.serverEntry = serverEntry;
    }
    
    @Override
    public void tick() {
        this.serverNameField.tick();
        this.addressField.tick();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        (this.serverNameField = new TextFieldWidget(this.font, this.width / 2 - 100, 66, 200, 20, I18n.translate("addServer.enterName"))).a(true);
        this.serverNameField.setText(this.serverEntry.name);
        this.serverNameField.setChangedListener(this::onClose);
        this.children.add(this.serverNameField);
        (this.addressField = new TextFieldWidget(this.font, this.width / 2 - 100, 106, 200, 20, I18n.translate("addServer.enterIp"))).setMaxLength(128);
        this.addressField.setText(this.serverEntry.address);
        this.addressField.a(this.g);
        this.addressField.setChangedListener(this::onClose);
        this.children.add(this.addressField);
        this.resourcePackOptionButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72, 200, 20, I18n.translate("addServer.resourcePack") + ": " + this.serverEntry.getResourcePack().getComponent().getFormattedText(), buttonWidget -> {
            this.serverEntry.setResourcePackState(ServerEntry.ResourcePackState.values()[(this.serverEntry.getResourcePack().ordinal() + 1) % ServerEntry.ResourcePackState.values().length]);
            this.resourcePackOptionButton.setMessage(I18n.translate("addServer.resourcePack") + ": " + this.serverEntry.getResourcePack().getComponent().getFormattedText());
            return;
        }));
        this.buttonAdd = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, I18n.translate("addServer.add"), buttonWidget -> this.addAndClose()));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> this.callback.accept(false)));
        this.onClose();
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.addressField.getText();
        final String string5 = this.serverNameField.getText();
        this.init(client, width, height);
        this.addressField.setText(string4);
        this.serverNameField.setText(string5);
    }
    
    private void onClose(final String unused) {
        this.onClose();
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    private void addAndClose() {
        this.serverEntry.name = this.serverNameField.getText();
        this.serverEntry.address = this.addressField.getText();
        this.callback.accept(true);
    }
    
    @Override
    public void onClose() {
        this.buttonAdd.active = (!this.addressField.getText().isEmpty() && this.addressField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty());
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 17, 16777215);
        this.drawString(this.font, I18n.translate("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
        this.drawString(this.font, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
        this.serverNameField.render(mouseX, mouseY, delta);
        this.addressField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }
}
