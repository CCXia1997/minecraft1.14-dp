package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class TeleportConfirmC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int teleportId;
    
    public TeleportConfirmC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public TeleportConfirmC2SPacket(final int teleportId) {
        this.teleportId = teleportId;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.teleportId = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.teleportId);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onTeleportConfirm(this);
    }
    
    public int getTeleportId() {
        return this.teleportId;
    }
}
