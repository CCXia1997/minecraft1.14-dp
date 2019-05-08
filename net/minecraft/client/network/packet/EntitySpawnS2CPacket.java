package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityType;
import java.util.UUID;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntitySpawnS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private UUID uuid;
    private double x;
    private double y;
    private double z;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    private int pitch;
    private int yaw;
    private EntityType<?> entityTypeId;
    private int entityData;
    
    public EntitySpawnS2CPacket() {
    }
    
    public EntitySpawnS2CPacket(final int integer1, final UUID uUID, final double double3, final double double5, final double double7, final float float9, final float float10, final EntityType<?> entityType11, final int integer12, final Vec3d vec3d13) {
        this.id = integer1;
        this.uuid = uUID;
        this.x = double3;
        this.y = double5;
        this.z = double7;
        this.pitch = MathHelper.floor(float9 * 256.0f / 360.0f);
        this.yaw = MathHelper.floor(float10 * 256.0f / 360.0f);
        this.entityTypeId = entityType11;
        this.entityData = integer12;
        this.velocityX = (int)(MathHelper.clamp(vec3d13.x, -3.9, 3.9) * 8000.0);
        this.velocityY = (int)(MathHelper.clamp(vec3d13.y, -3.9, 3.9) * 8000.0);
        this.velocityZ = (int)(MathHelper.clamp(vec3d13.z, -3.9, 3.9) * 8000.0);
    }
    
    public EntitySpawnS2CPacket(final Entity entity) {
        this(entity, 0);
    }
    
    public EntitySpawnS2CPacket(final Entity entity, final int integer) {
        this(entity.getEntityId(), entity.getUuid(), entity.x, entity.y, entity.z, entity.pitch, entity.yaw, entity.getType(), integer, entity.getVelocity());
    }
    
    public EntitySpawnS2CPacket(final Entity entity, final EntityType<?> entityType, final int data, final BlockPos blockPos) {
        this(entity.getEntityId(), entity.getUuid(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), entity.pitch, entity.yaw, entityType, data, entity.getVelocity());
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.uuid = buf.readUuid();
        this.entityTypeId = Registry.ENTITY_TYPE.get(buf.readVarInt());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.pitch = buf.readByte();
        this.yaw = buf.readByte();
        this.entityData = buf.readInt();
        this.velocityX = buf.readShort();
        this.velocityY = buf.readShort();
        this.velocityZ = buf.readShort();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeUuid(this.uuid);
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(this.entityTypeId));
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.pitch);
        buf.writeByte(this.yaw);
        buf.writeInt(this.entityData);
        buf.writeShort(this.velocityX);
        buf.writeShort(this.velocityY);
        buf.writeShort(this.velocityZ);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntitySpawn(this);
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
    public double getVelocityX() {
        return this.velocityX / 8000.0;
    }
    
    @Environment(EnvType.CLIENT)
    public double getVelocityY() {
        return this.velocityY / 8000.0;
    }
    
    @Environment(EnvType.CLIENT)
    public double getVelocityz() {
        return this.velocityZ / 8000.0;
    }
    
    @Environment(EnvType.CLIENT)
    public int getPitch() {
        return this.pitch;
    }
    
    @Environment(EnvType.CLIENT)
    public int getYaw() {
        return this.yaw;
    }
    
    @Environment(EnvType.CLIENT)
    public EntityType<?> getEntityTypeId() {
        return this.entityTypeId;
    }
    
    @Environment(EnvType.CLIENT)
    public int getEntityData() {
        return this.entityData;
    }
}
