package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class QueryBlockNbtC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int transactionId;
    private BlockPos pos;
    
    public QueryBlockNbtC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public QueryBlockNbtC2SPacket(final int integer, final BlockPos blockPos) {
        this.transactionId = integer;
        this.pos = blockPos;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.transactionId = buf.readVarInt();
        this.pos = buf.readBlockPos();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.transactionId);
        buf.writeBlockPos(this.pos);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onQueryBlockNbt(this);
    }
    
    public int getTransactionId() {
        return this.transactionId;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
}
