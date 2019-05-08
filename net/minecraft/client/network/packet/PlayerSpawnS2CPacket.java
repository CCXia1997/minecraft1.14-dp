package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import net.minecraft.entity.data.DataTracker;
import java.util.UUID;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerSpawnS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private UUID uuid;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private DataTracker dataTracker;
    private List<DataTracker.Entry<?>> trackedValues;
    
    public PlayerSpawnS2CPacket() {
    }
    
    public PlayerSpawnS2CPacket(final PlayerEntity playerEntity) {
        this.id = playerEntity.getEntityId();
        this.uuid = playerEntity.getGameProfile().getId();
        this.x = playerEntity.x;
        this.y = playerEntity.y;
        this.z = playerEntity.z;
        this.yaw = (byte)(playerEntity.yaw * 256.0f / 360.0f);
        this.pitch = (byte)(playerEntity.pitch * 256.0f / 360.0f);
        this.dataTracker = playerEntity.getDataTracker();
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.uuid = buf.readUuid();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readByte();
        this.pitch = buf.readByte();
        this.trackedValues = DataTracker.deserializePacket(buf);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeUuid(this.uuid);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.yaw);
        buf.writeByte(this.pitch);
        this.dataTracker.toPacketByteBuf(buf);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onPlayerSpawn(this);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public List<DataTracker.Entry<?>> getTrackedValues() {
        return this.trackedValues;
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public UUID getPlayerUuid() {
        return this.uuid;
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
}
