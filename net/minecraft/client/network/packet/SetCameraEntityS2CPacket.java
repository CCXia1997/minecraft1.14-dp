package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class SetCameraEntityS2CPacket implements Packet<ClientPlayPacketListener>
{
    public int id;
    
    public SetCameraEntityS2CPacket() {
    }
    
    public SetCameraEntityS2CPacket(final Entity entity) {
        this.id = entity.getEntityId();
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onSetCameraEntity(this);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public Entity getEntity(final World world) {
        return world.getEntityById(this.id);
    }
}
