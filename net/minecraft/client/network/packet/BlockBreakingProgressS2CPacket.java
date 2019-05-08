package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class BlockBreakingProgressS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int entityId;
    private BlockPos pos;
    private int progress;
    
    public BlockBreakingProgressS2CPacket() {
    }
    
    public BlockBreakingProgressS2CPacket(final int entityId, final BlockPos pos, final int integer3) {
        this.entityId = entityId;
        this.pos = pos;
        this.progress = integer3;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.pos = buf.readBlockPos();
        this.progress = buf.readUnsignedByte();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeBlockPos(this.pos);
        buf.writeByte(this.progress);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onBlockDestroyProgress(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
    
    @Environment(EnvType.CLIENT)
    public int getProgress() {
        return this.progress;
    }
}
