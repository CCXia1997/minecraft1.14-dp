package net.minecraft.client.gui.ingame;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class JigsawBlockScreen extends Screen
{
    private final JigsawBlockEntity jigsaw;
    private TextFieldWidget attachmentTypeField;
    private TextFieldWidget targetPoolField;
    private TextFieldWidget finalStateField;
    private ButtonWidget e;
    
    public JigsawBlockScreen(final JigsawBlockEntity jigsaw) {
        super(NarratorManager.a);
        this.jigsaw = jigsaw;
    }
    
    @Override
    public void tick() {
        this.attachmentTypeField.tick();
        this.targetPoolField.tick();
        this.finalStateField.tick();
    }
    
    private void onDone() {
        this.updateServer();
        this.minecraft.openScreen(null);
    }
    
    private void onCancel() {
        this.minecraft.openScreen(null);
    }
    
    private void updateServer() {
        this.minecraft.getNetworkHandler().sendPacket(new UpdateJigsawC2SPacket(this.jigsaw.getPos(), new Identifier(this.attachmentTypeField.getText()), new Identifier(this.targetPoolField.getText()), this.finalStateField.getText()));
    }
    
    @Override
    public void onClose() {
        this.onCancel();
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.e = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done"), buttonWidget -> this.onDone()));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.onCancel()));
        (this.targetPoolField = new TextFieldWidget(this.font, this.width / 2 - 152, 40, 300, 20, I18n.translate("jigsaw_block.target_pool"))).setMaxLength(128);
        this.targetPoolField.setText(this.jigsaw.getTargetPool().toString());
        this.targetPoolField.setChangedListener(string -> this.a());
        this.children.add(this.targetPoolField);
        (this.attachmentTypeField = new TextFieldWidget(this.font, this.width / 2 - 152, 80, 300, 20, I18n.translate("jigsaw_block.attachement_type"))).setMaxLength(128);
        this.attachmentTypeField.setText(this.jigsaw.getAttachmentType().toString());
        this.attachmentTypeField.setChangedListener(string -> this.a());
        this.children.add(this.attachmentTypeField);
        (this.finalStateField = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 300, 20, I18n.translate("jigsaw_block.final_state"))).setMaxLength(256);
        this.finalStateField.setText(this.jigsaw.getFinalState());
        this.children.add(this.finalStateField);
        this.setInitialFocus(this.targetPoolField);
        this.a();
    }
    
    protected void a() {
        this.e.active = (Identifier.isValidIdentifier(this.attachmentTypeField.getText()) & Identifier.isValidIdentifier(this.targetPoolField.getText()));
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.attachmentTypeField.getText();
        final String string5 = this.targetPoolField.getText();
        final String string6 = this.finalStateField.getText();
        this.init(client, width, height);
        this.attachmentTypeField.setText(string4);
        this.targetPoolField.setText(string5);
        this.finalStateField.setText(string6);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.onDone();
            return true;
        }
        return false;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawString(this.font, I18n.translate("jigsaw_block.target_pool"), this.width / 2 - 153, 30, 10526880);
        this.targetPoolField.render(mouseX, mouseY, delta);
        this.drawString(this.font, I18n.translate("jigsaw_block.attachement_type"), this.width / 2 - 153, 70, 10526880);
        this.attachmentTypeField.render(mouseX, mouseY, delta);
        this.drawString(this.font, I18n.translate("jigsaw_block.final_state"), this.width / 2 - 153, 110, 10526880);
        this.finalStateField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }
}
