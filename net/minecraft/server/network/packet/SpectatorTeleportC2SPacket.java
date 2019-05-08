package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import java.util.UUID;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class SpectatorTeleportC2SPacket implements Packet<ServerPlayPacketListener>
{
    private UUID targetUuid;
    
    public SpectatorTeleportC2SPacket() {
    }
    
    public SpectatorTeleportC2SPacket(final UUID targetUuid) {
        this.targetUuid = targetUuid;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.targetUuid = buf.readUuid();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeUuid(this.targetUuid);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onSpectatorTeleport(this);
    }
    
    @Nullable
    public Entity getTarget(final ServerWorld world) {
        return world.getEntity(this.targetUuid);
    }
}
