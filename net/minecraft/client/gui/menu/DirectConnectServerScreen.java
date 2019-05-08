package net.minecraft.client.gui.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class DirectConnectServerScreen extends Screen
{
    private ButtonWidget selectServerButton;
    private final ServerEntry serverEntry;
    private TextFieldWidget addressField;
    private final BooleanConsumer callback;
    
    public DirectConnectServerScreen(final BooleanConsumer callback, final ServerEntry serverEntry) {
        super(new TranslatableTextComponent("selectServer.direct", new Object[0]));
        this.serverEntry = serverEntry;
        this.callback = callback;
    }
    
    @Override
    public void tick() {
        this.addressField.tick();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.selectServerButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, I18n.translate("selectServer.select"), buttonWidget -> this.saveAndClose()));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> this.callback.accept(false)));
        (this.addressField = new TextFieldWidget(this.font, this.width / 2 - 100, 116, 200, 20, I18n.translate("addServer.enterIp"))).setMaxLength(128);
        this.addressField.a(true);
        this.addressField.setText(this.minecraft.options.lastServer);
        this.addressField.setChangedListener(text -> this.onAddressFieldChanged());
        this.children.add(this.addressField);
        this.setInitialFocus(this.addressField);
        this.onAddressFieldChanged();
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.addressField.getText();
        this.init(client, width, height);
        this.addressField.setText(string4);
    }
    
    private void saveAndClose() {
        this.serverEntry.address = this.addressField.getText();
        this.callback.accept(true);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
        this.minecraft.options.lastServer = this.addressField.getText();
        this.minecraft.options.write();
    }
    
    private void onAddressFieldChanged() {
        this.selectServerButton.active = (!this.addressField.getText().isEmpty() && this.addressField.getText().split(":").length > 0);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
        this.drawString(this.font, I18n.translate("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
        this.addressField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }
}
