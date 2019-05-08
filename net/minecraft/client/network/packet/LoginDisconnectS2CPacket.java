package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.Packet;

public class LoginDisconnectS2CPacket implements Packet<ClientLoginPacketListener>
{
    private TextComponent reason;
    
    public LoginDisconnectS2CPacket() {
    }
    
    public LoginDisconnectS2CPacket(final TextComponent textComponent) {
        this.reason = textComponent;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.reason = TextComponent.Serializer.fromLenientJsonString(buf.readString(262144));
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeTextComponent(this.reason);
    }
    
    @Override
    public void apply(final ClientLoginPacketListener listener) {
        listener.onDisconnect(this);
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getReason() {
        return this.reason;
    }
}
