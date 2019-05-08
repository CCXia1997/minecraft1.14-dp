package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.Packet;

public class LoginQueryRequestS2CPacket implements Packet<ClientLoginPacketListener>
{
    private int queryId;
    private Identifier channel;
    private PacketByteBuf payload;
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.queryId = buf.readVarInt();
        this.channel = buf.readIdentifier();
        final int integer2 = buf.readableBytes();
        if (integer2 < 0 || integer2 > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.payload = new PacketByteBuf(buf.readBytes(integer2));
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.queryId);
        buf.writeIdentifier(this.channel);
        buf.writeBytes(this.payload.copy());
    }
    
    @Override
    public void apply(final ClientLoginPacketListener listener) {
        listener.onQueryRequest(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getQueryId() {
        return this.queryId;
    }
}
