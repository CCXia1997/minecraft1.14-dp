package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerSpawnPositionS2CPacket implements Packet<ClientPlayPacketListener>
{
    private BlockPos pos;
    
    public PlayerSpawnPositionS2CPacket() {
    }
    
    public PlayerSpawnPositionS2CPacket(final BlockPos blockPos) {
        this.pos = blockPos;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.pos = buf.readBlockPos();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeBlockPos(this.pos);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onPlayerSpawnPosition(this);
    }
    
    @Environment(EnvType.CLIENT)
    public BlockPos getPos() {
        return this.pos;
    }
}
