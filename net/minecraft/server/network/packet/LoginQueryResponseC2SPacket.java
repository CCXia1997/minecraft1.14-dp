package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.Packet;

public class LoginQueryResponseC2SPacket implements Packet<ServerLoginPacketListener>
{
    private int queryId;
    private PacketByteBuf response;
    
    public LoginQueryResponseC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public LoginQueryResponseC2SPacket(final int integer, @Nullable final PacketByteBuf packetByteBuf) {
        this.queryId = integer;
        this.response = packetByteBuf;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.queryId = buf.readVarInt();
        if (buf.readBoolean()) {
            final int integer2 = buf.readableBytes();
            if (integer2 < 0 || integer2 > 1048576) {
                throw new IOException("Payload may not be larger than 1048576 bytes");
            }
            this.response = new PacketByteBuf(buf.readBytes(integer2));
        }
        else {
            this.response = null;
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.queryId);
        if (this.response != null) {
            buf.writeBoolean(true);
            buf.writeBytes(this.response.copy());
        }
        else {
            buf.writeBoolean(false);
        }
    }
    
    @Override
    public void apply(final ServerLoginPacketListener listener) {
        listener.onQueryResponse(this);
    }
}
