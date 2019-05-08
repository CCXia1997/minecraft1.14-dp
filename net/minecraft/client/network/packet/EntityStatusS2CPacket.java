package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityStatusS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private byte status;
    
    public EntityStatusS2CPacket() {
    }
    
    public EntityStatusS2CPacket(final Entity entity, final byte byte2) {
        this.id = entity.getEntityId();
        this.status = byte2;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readInt();
        this.status = buf.readByte();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeInt(this.id);
        buf.writeByte(this.status);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntityStatus(this);
    }
    
    @Environment(EnvType.CLIENT)
    public Entity getEntity(final World world) {
        return world.getEntityById(this.id);
    }
    
    @Environment(EnvType.CLIENT)
    public byte getStatus() {
        return this.status;
    }
}
