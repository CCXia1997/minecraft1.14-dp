package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class CustomPayloadC2SPacket implements Packet<ServerPlayPacketListener>
{
    public static final Identifier BRAND;
    private Identifier channel;
    private PacketByteBuf data;
    
    public CustomPayloadC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public CustomPayloadC2SPacket(final Identifier channel, final PacketByteBuf packetByteBuf) {
        this.channel = channel;
        this.data = packetByteBuf;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.channel = buf.readIdentifier();
        final int integer2 = buf.readableBytes();
        if (integer2 < 0 || integer2 > 32767) {
            throw new IOException("Payload may not be larger than 32767 bytes");
        }
        this.data = new PacketByteBuf(buf.readBytes(integer2));
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeIdentifier(this.channel);
        buf.writeBytes(this.data);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onCustomPayload(this);
        if (this.data != null) {
            this.data.release();
        }
    }
    
    static {
        BRAND = new Identifier("brand");
    }
}
