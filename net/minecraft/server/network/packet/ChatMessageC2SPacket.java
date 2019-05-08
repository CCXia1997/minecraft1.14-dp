package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class ChatMessageC2SPacket implements Packet<ServerPlayPacketListener>
{
    private String chatMessage;
    
    public ChatMessageC2SPacket() {
    }
    
    public ChatMessageC2SPacket(String chatMessage) {
        if (chatMessage.length() > 256) {
            chatMessage = chatMessage.substring(0, 256);
        }
        this.chatMessage = chatMessage;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.chatMessage = buf.readString(256);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeString(this.chatMessage);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onChatMessage(this);
    }
    
    public String getChatMessage() {
        return this.chatMessage;
    }
}
