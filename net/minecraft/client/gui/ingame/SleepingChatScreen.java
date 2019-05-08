package net.minecraft.client.gui.ingame;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SleepingChatScreen extends ChatScreen
{
    public SleepingChatScreen() {
        super("");
    }
    
    @Override
    protected void init() {
        super.init();
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height - 40, 200, 20, I18n.translate("multiplayer.stopSleeping"), buttonWidget -> this.stopSleeping()));
    }
    
    @Override
    public void onClose() {
        this.stopSleeping();
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == 256) {
            this.stopSleeping();
        }
        else if (keyCode == 257 || keyCode == 335) {
            final String string4 = this.chatField.getText().trim();
            if (!string4.isEmpty()) {
                this.minecraft.player.sendChatMessage(string4);
            }
            this.chatField.setText("");
            this.minecraft.inGameHud.getChatHud().c();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private void stopSleeping() {
        final ClientPlayNetworkHandler clientPlayNetworkHandler1 = this.minecraft.player.networkHandler;
        clientPlayNetworkHandler1.sendPacket(new ClientCommandC2SPacket(this.minecraft.player, ClientCommandC2SPacket.Mode.c));
    }
}
