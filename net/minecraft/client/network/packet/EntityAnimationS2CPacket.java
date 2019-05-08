package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityAnimationS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private int animationId;
    
    public EntityAnimationS2CPacket() {
    }
    
    public EntityAnimationS2CPacket(final Entity entity, final int integer) {
        this.id = entity.getEntityId();
        this.animationId = integer;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.animationId = buf.readUnsignedByte();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeByte(this.animationId);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntityAnimation(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public int getAnimationId() {
        return this.animationId;
    }
}
