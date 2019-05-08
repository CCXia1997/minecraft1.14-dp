package net.minecraft.client.gui;

import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.world.CommandBlockExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CommandBlockMinecartScreen extends AbstractCommandBlockScreen
{
    private final CommandBlockExecutor commandExecutor;
    
    public CommandBlockMinecartScreen(final CommandBlockExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
    
    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }
    
    @Override
    int b() {
        return 150;
    }
    
    @Override
    protected void init() {
        super.init();
        this.trackingOutput = this.getCommandExecutor().isTrackingOutput();
        this.updateTrackedOutput();
        this.consoleCommandTextField.setText(this.getCommandExecutor().getCommand());
    }
    
    @Override
    protected void syncSettingsToServer(final CommandBlockExecutor commandExecutor) {
        if (commandExecutor instanceof CommandBlockMinecartEntity.CommandExecutor) {
            final CommandBlockMinecartEntity.CommandExecutor commandExecutor2 = (CommandBlockMinecartEntity.CommandExecutor)commandExecutor;
            this.minecraft.getNetworkHandler().sendPacket(new UpdateCommandBlockMinecartC2SPacket(commandExecutor2.getMinecart().getEntityId(), this.consoleCommandTextField.getText(), commandExecutor.isTrackingOutput()));
        }
    }
}
