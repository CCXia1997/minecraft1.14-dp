package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.text.ChatMessageType;
import net.minecraft.text.TextComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ChatMessageS2CPacket implements Packet<ClientPlayPacketListener>
{
    private TextComponent message;
    private ChatMessageType location;
    
    public ChatMessageS2CPacket() {
    }
    
    public ChatMessageS2CPacket(final TextComponent textComponent) {
        this(textComponent, ChatMessageType.b);
    }
    
    public ChatMessageS2CPacket(final TextComponent textComponent, final ChatMessageType chatMessageType) {
        this.message = textComponent;
        this.location = chatMessageType;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.message = buf.readTextComponent();
        this.location = ChatMessageType.byId(buf.readByte());
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeTextComponent(this.message);
        buf.writeByte(this.location.getId());
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onChatMessage(this);
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getMessage() {
        return this.message;
    }
    
    public boolean isNonChat() {
        return this.location == ChatMessageType.b || this.location == ChatMessageType.c;
    }
    
    public ChatMessageType getLocation() {
        return this.location;
    }
    
    @Override
    public boolean isErrorFatal() {
        return true;
    }
}
