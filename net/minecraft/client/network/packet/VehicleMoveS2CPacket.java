package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class VehicleMoveS2CPacket implements Packet<ClientPlayPacketListener>
{
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    
    public VehicleMoveS2CPacket() {
    }
    
    public VehicleMoveS2CPacket(final Entity entity) {
        this.x = entity.x;
        this.y = entity.y;
        this.z = entity.z;
        this.yaw = entity.yaw;
        this.pitch = entity.pitch;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onVehicleMove(this);
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
    public float getYaw() {
        return this.yaw;
    }
    
    @Environment(EnvType.CLIENT)
    public float getPitch() {
        return this.pitch;
    }
}
