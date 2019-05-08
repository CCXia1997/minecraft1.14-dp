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

public class EntitySetHeadYawS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int entity;
    private byte headYaw;
    
    public EntitySetHeadYawS2CPacket() {
    }
    
    public EntitySetHeadYawS2CPacket(final Entity entity, final byte byte2) {
        this.entity = entity.getEntityId();
        this.headYaw = byte2;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entity = buf.readVarInt();
        this.headYaw = buf.readByte();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entity);
        buf.writeByte(this.headYaw);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntitySetHeadYaw(this);
    }
    
    @Environment(EnvType.CLIENT)
    public Entity getEntity(final World world) {
        return world.getEntityById(this.entity);
    }
    
    @Environment(EnvType.CLIENT)
    public byte getHeadYaw() {
        return this.headYaw;
    }
}
