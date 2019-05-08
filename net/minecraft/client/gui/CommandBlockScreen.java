package net.minecraft.client.gui;

import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.UpdateCommandBlockC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CommandBlockScreen extends AbstractCommandBlockScreen
{
    private final CommandBlockBlockEntity blockEntity;
    private ButtonWidget modeButton;
    private ButtonWidget conditionalModeButton;
    private ButtonWidget redstoneTriggerButton;
    private CommandBlockBlockEntity.Type mode;
    private boolean conditional;
    private boolean autoActivate;
    
    public CommandBlockScreen(final CommandBlockBlockEntity blockEntity) {
        this.mode = CommandBlockBlockEntity.Type.c;
        this.blockEntity = blockEntity;
    }
    
    @Override
    CommandBlockExecutor getCommandExecutor() {
        return this.blockEntity.getCommandExecutor();
    }
    
    @Override
    int b() {
        return 135;
    }
    
    @Override
    protected void init() {
        super.init();
        this.modeButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 50 - 100 - 4, 165, 100, 20, I18n.translate("advMode.mode.sequence"), buttonWidget -> {
            this.cycleType();
            this.updateMode();
            return;
        }));
        this.conditionalModeButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 50, 165, 100, 20, I18n.translate("advMode.mode.unconditional"), buttonWidget -> {
            this.conditional = !this.conditional;
            this.updateConditionalMode();
            return;
        }));
        this.redstoneTriggerButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 50 + 4, 165, 100, 20, I18n.translate("advMode.mode.redstoneTriggered"), buttonWidget -> {
            this.autoActivate = !this.autoActivate;
            this.updateActivationMode();
            return;
        }));
        this.doneButton.active = false;
        this.toggleTrackingOutputButton.active = false;
        this.modeButton.active = false;
        this.conditionalModeButton.active = false;
        this.redstoneTriggerButton.active = false;
    }
    
    public void g() {
        final CommandBlockExecutor commandBlockExecutor1 = this.blockEntity.getCommandExecutor();
        this.consoleCommandTextField.setText(commandBlockExecutor1.getCommand());
        this.trackingOutput = commandBlockExecutor1.isTrackingOutput();
        this.mode = this.blockEntity.getType();
        this.conditional = this.blockEntity.isConditionalCommandBlock();
        this.autoActivate = this.blockEntity.isAuto();
        this.updateTrackedOutput();
        this.updateMode();
        this.updateConditionalMode();
        this.updateActivationMode();
        this.doneButton.active = true;
        this.toggleTrackingOutputButton.active = true;
        this.modeButton.active = true;
        this.conditionalModeButton.active = true;
        this.redstoneTriggerButton.active = true;
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        super.resize(client, width, height);
        this.updateTrackedOutput();
        this.updateMode();
        this.updateConditionalMode();
        this.updateActivationMode();
        this.doneButton.active = true;
        this.toggleTrackingOutputButton.active = true;
        this.modeButton.active = true;
        this.conditionalModeButton.active = true;
        this.redstoneTriggerButton.active = true;
    }
    
    @Override
    protected void syncSettingsToServer(final CommandBlockExecutor commandExecutor) {
        this.minecraft.getNetworkHandler().sendPacket(new UpdateCommandBlockC2SPacket(new BlockPos(commandExecutor.getPos()), this.consoleCommandTextField.getText(), this.mode, commandExecutor.isTrackingOutput(), this.conditional, this.autoActivate));
    }
    
    private void updateMode() {
        switch (this.mode) {
            case a: {
                this.modeButton.setMessage(I18n.translate("advMode.mode.sequence"));
                break;
            }
            case b: {
                this.modeButton.setMessage(I18n.translate("advMode.mode.auto"));
                break;
            }
            case c: {
                this.modeButton.setMessage(I18n.translate("advMode.mode.redstone"));
                break;
            }
        }
    }
    
    private void cycleType() {
        switch (this.mode) {
            case a: {
                this.mode = CommandBlockBlockEntity.Type.b;
                break;
            }
            case b: {
                this.mode = CommandBlockBlockEntity.Type.c;
                break;
            }
            case c: {
                this.mode = CommandBlockBlockEntity.Type.a;
                break;
            }
        }
    }
    
    private void updateConditionalMode() {
        if (this.conditional) {
            this.conditionalModeButton.setMessage(I18n.translate("advMode.mode.conditional"));
        }
        else {
            this.conditionalModeButton.setMessage(I18n.translate("advMode.mode.unconditional"));
        }
    }
    
    private void updateActivationMode() {
        if (this.autoActivate) {
            this.redstoneTriggerButton.setMessage(I18n.translate("advMode.mode.autoexec.bat"));
        }
        else {
            this.redstoneTriggerButton.setMessage(I18n.translate("advMode.mode.redstoneTriggered"));
        }
    }
}
