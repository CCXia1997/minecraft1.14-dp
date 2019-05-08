package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class QueryEntityNbtC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int transactionId;
    private int entityId;
    
    public QueryEntityNbtC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public QueryEntityNbtC2SPacket(final int integer1, final int integer2) {
        this.transactionId = integer1;
        this.entityId = integer2;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.transactionId = buf.readVarInt();
        this.entityId = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.transactionId);
        buf.writeVarInt(this.entityId);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onQueryEntityNbt(this);
    }
    
    public int getTransactionId() {
        return this.transactionId;
    }
    
    public int getEntityId() {
        return this.entityId;
    }
}
