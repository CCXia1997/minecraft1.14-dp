package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class WorldEventS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int eventId;
    private BlockPos pos;
    private int data;
    private boolean global;
    
    public WorldEventS2CPacket() {
    }
    
    public WorldEventS2CPacket(final int eventId, final BlockPos pos, final int data, final boolean boolean4) {
        this.eventId = eventId;
        this.pos = pos.toImmutable();
        this.data = data;
        this.global = boolean4;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.eventId = buf.readInt();
        this.pos = buf.readBlockPos();
        this.data = buf.readInt();
        this.global = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeInt(this.eventId);
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.data);
        buf.writeBoolean(this.global);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onWorldEvent(this);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isGlobal() {
        return this.global;
    }
    
    @Environment(EnvType.CLIENT)
    public int getEventId() {
        return this.eventId;
    }
    
    @Environment(EnvType.CLIENT)
    public int getEffectData() {
        return this.data;
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
}
