package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.Packet;

public class HandshakeC2SPacket implements Packet<ServerHandshakePacketListener>
{
    private int version;
    private String address;
    private int port;
    private NetworkState state;
    
    public HandshakeC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public HandshakeC2SPacket(final String address, final int port, final NetworkState networkState) {
        this.version = SharedConstants.getGameVersion().getProtocolVersion();
        this.address = address;
        this.port = port;
        this.state = networkState;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.version = buf.readVarInt();
        this.address = buf.readString(255);
        this.port = buf.readUnsignedShort();
        this.state = NetworkState.byId(buf.readVarInt());
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.version);
        buf.writeString(this.address);
        buf.writeShort(this.port);
        buf.writeVarInt(this.state.getId());
    }
    
    @Override
    public void apply(final ServerHandshakePacketListener listener) {
        listener.onHandshake(this);
    }
    
    public NetworkState getIntendedState() {
        return this.state;
    }
    
    public int getProtocolVersion() {
        return this.version;
    }
}
