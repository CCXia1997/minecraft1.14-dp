package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.entity.LivingEntity;
import java.util.List;
import net.minecraft.entity.data.DataTracker;
import java.util.UUID;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class MobSpawnS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private UUID uuid;
    private int entityTypeId;
    private double x;
    private double y;
    private double z;
    private int yaw;
    private int pitch;
    private int headPitch;
    private byte velocityX;
    private byte velocityY;
    private byte velocityZ;
    private DataTracker dataTracker;
    private List<DataTracker.Entry<?>> trackedValues;
    
    public MobSpawnS2CPacket() {
    }
    
    public MobSpawnS2CPacket(final LivingEntity livingEntity) {
        this.id = livingEntity.getEntityId();
        this.uuid = livingEntity.getUuid();
        this.entityTypeId = Registry.ENTITY_TYPE.getRawId(livingEntity.getType());
        this.x = livingEntity.x;
        this.y = livingEntity.y;
        this.z = livingEntity.z;
        this.velocityX = (byte)(livingEntity.yaw * 256.0f / 360.0f);
        this.velocityY = (byte)(livingEntity.pitch * 256.0f / 360.0f);
        this.velocityZ = (byte)(livingEntity.headYaw * 256.0f / 360.0f);
        final double double2 = 3.9;
        final Vec3d vec3d4 = livingEntity.getVelocity();
        final double double3 = MathHelper.clamp(vec3d4.x, -3.9, 3.9);
        final double double4 = MathHelper.clamp(vec3d4.y, -3.9, 3.9);
        final double double5 = MathHelper.clamp(vec3d4.z, -3.9, 3.9);
        this.yaw = (int)(double3 * 8000.0);
        this.pitch = (int)(double4 * 8000.0);
        this.headPitch = (int)(double5 * 8000.0);
        this.dataTracker = livingEntity.getDataTracker();
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.uuid = buf.readUuid();
        this.entityTypeId = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.velocityX = buf.readByte();
        this.velocityY = buf.readByte();
        this.velocityZ = buf.readByte();
        this.yaw = buf.readShort();
        this.pitch = buf.readShort();
        this.headPitch = buf.readShort();
        this.trackedValues = DataTracker.deserializePacket(buf);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeUuid(this.uuid);
        buf.writeVarInt(this.entityTypeId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.velocityX);
        buf.writeByte(this.velocityY);
        buf.writeByte(this.velocityZ);
        buf.writeShort(this.yaw);
        buf.writeShort(this.pitch);
        buf.writeShort(this.headPitch);
        this.dataTracker.toPacketByteBuf(buf);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onMobSpawn(this);
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
    public UUID getUuid() {
        return this.uuid;
    }
    
    @Environment(EnvType.CLIENT)
    public int getEntityTypeId() {
        return this.entityTypeId;
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
    public int getYaw() {
        return this.yaw;
    }
    
    @Environment(EnvType.CLIENT)
    public int getPitch() {
        return this.pitch;
    }
    
    @Environment(EnvType.CLIENT)
    public int getHeadPitch() {
        return this.headPitch;
    }
    
    @Environment(EnvType.CLIENT)
    public byte getVelocityX() {
        return this.velocityX;
    }
    
    @Environment(EnvType.CLIENT)
    public byte getVelocityY() {
        return this.velocityY;
    }
    
    @Environment(EnvType.CLIENT)
    public byte getVelocityZ() {
        return this.velocityZ;
    }
}
