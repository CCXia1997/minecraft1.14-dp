package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityPositionS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private boolean onGround;
    
    public EntityPositionS2CPacket() {
    }
    
    public EntityPositionS2CPacket(final Entity entity) {
        this.id = entity.getEntityId();
        this.x = entity.x;
        this.y = entity.y;
        this.z = entity.z;
        this.yaw = (byte)(entity.yaw * 256.0f / 360.0f);
        this.pitch = (byte)(entity.pitch * 256.0f / 360.0f);
        this.onGround = entity.onGround;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.onGround = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        buf.writeBoolean(this.onGround);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntityPosition(this);
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
    public byte getYaw() {
        return this.yaw;
    }
    
    @Environment(EnvType.CLIENT)
    public byte getPitch() {
        return this.pitch;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isOnGround() {
        return this.onGround;
    }
}
