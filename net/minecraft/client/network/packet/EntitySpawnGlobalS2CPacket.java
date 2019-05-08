package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntitySpawnGlobalS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private double x;
    private double y;
    private double z;
    private int entityTypeId;
    
    public EntitySpawnGlobalS2CPacket() {
    }
    
    public EntitySpawnGlobalS2CPacket(final Entity entity) {
        this.id = entity.getEntityId();
        this.x = entity.x;
        this.y = entity.y;
        this.z = entity.z;
        if (entity instanceof LightningEntity) {
            this.entityTypeId = 1;
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.entityTypeId = buf.readByte();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeByte(this.entityTypeId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntitySpawnGlobal(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public double getX() {
        return this.x;
    }
    
    @Environment(EnvType.CLIENT)
    public double getY() {
        return this.y;
    }
    
    @Environment(EnvType.CLIENT)
    public double getZ() {
        return this.z;
    }
    
    @Environment(EnvType.CLIENT)
    public int getEntityTypeId() {
        return this.entityTypeId;
    }
}
