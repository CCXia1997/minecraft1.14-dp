package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityVelocityUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private int velocityX;
    private int velocityY;
    private int velocityZ;
    
    public EntityVelocityUpdateS2CPacket() {
    }
    
    public EntityVelocityUpdateS2CPacket(final Entity entity) {
        this(entity.getEntityId(), entity.getVelocity());
    }
    
    public EntityVelocityUpdateS2CPacket(final int integer, final Vec3d vec3d) {
        this.id = integer;
        final double double3 = 3.9;
        final double double4 = MathHelper.clamp(vec3d.x, -3.9, 3.9);
        final double double5 = MathHelper.clamp(vec3d.y, -3.9, 3.9);
        final double double6 = MathHelper.clamp(vec3d.z, -3.9, 3.9);
        this.velocityX = (int)(double4 * 8000.0);
        this.velocityY = (int)(double5 * 8000.0);
        this.velocityZ = (int)(double6 * 8000.0);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.velocityX = buf.readShort();
        this.velocityY = buf.readShort();
        this.velocityZ = buf.readShort();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeShort(this.velocityX);
        buf.writeShort(this.velocityY);
        buf.writeShort(this.velocityZ);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onVelocityUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    public int getVelocityX() {
        return this.velocityX;
    }
    
    @Environment(EnvType.CLIENT)
    public int getVelocityY() {
        return this.velocityY;
    }
    
    @Environment(EnvType.CLIENT)
    public int getVelocityZ() {
        return this.velocityZ;
    }
}
