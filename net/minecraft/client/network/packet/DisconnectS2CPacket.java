package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class DisconnectS2CPacket implements Packet<ClientPlayPacketListener>
{
    private TextComponent reason;
    
    public DisconnectS2CPacket() {
    }
    
    public DisconnectS2CPacket(final TextComponent textComponent) {
        this.reason = textComponent;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.reason = buf.readTextComponent();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeTextComponent(this.reason);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onDisconnect(this);
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getReason() {
        return this.reason;
    }
}
