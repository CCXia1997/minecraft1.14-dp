package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class SelectVillagerTradeC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int tradeId;
    
    public SelectVillagerTradeC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public SelectVillagerTradeC2SPacket(final int integer) {
        this.tradeId = integer;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.tradeId = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.tradeId);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onVillagerTradeSelect(this);
    }
    
    public int b() {
        return this.tradeId;
    }
}
