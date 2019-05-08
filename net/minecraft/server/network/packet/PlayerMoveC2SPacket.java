package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerMoveC2SPacket implements Packet<ServerPlayPacketListener>
{
    protected double x;
    protected double y;
    protected double z;
    protected float yaw;
    protected float pitch;
    protected boolean onGround;
    protected boolean changePosition;
    protected boolean changeLook;
    
    public PlayerMoveC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public PlayerMoveC2SPacket(final boolean onGround) {
        this.onGround = onGround;
    }
    
    @Override
    public void apply(final ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPlayerMove(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.onGround = (buf.readUnsignedByte() != 0);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.onGround ? 1 : 0);
    }
    
    public double getX(final double currentX) {
        return this.changePosition ? this.x : currentX;
    }
    
    public double getY(final double currentY) {
        return this.changePosition ? this.y : currentY;
    }
    
    public double getZ(final double currentZ) {
        return this.changePosition ? this.z : currentZ;
    }
    
    public float getYaw(final float currentYaw) {
        return this.changeLook ? this.yaw : currentYaw;
    }
    
    public float getPitch(final float currentPitch) {
        return this.changeLook ? this.pitch : currentPitch;
    }
    
    public boolean isOnGround() {
        return this.onGround;
    }
    
    public static class Both extends PlayerMoveC2SPacket
    {
        public Both() {
            this.changePosition = true;
            this.changeLook = true;
        }
        
        @Environment(EnvType.CLIENT)
        public Both(final double double1, final double double3, final double double5, final float float7, final float float8, final boolean boolean9) {
            this.x = double1;
            this.y = double3;
            this.z = double5;
            this.yaw = float7;
            this.pitch = float8;
            this.onGround = boolean9;
            this.changeLook = true;
            this.changePosition = true;
        }
        
        @Override
        public void read(final PacketByteBuf buf) throws IOException {
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
            this.yaw = buf.readFloat();
            this.pitch = buf.readFloat();
            super.read(buf);
        }
        
        @Override
        public void write(final PacketByteBuf buf) throws IOException {
            buf.writeDouble(this.x);
            buf.writeDouble(this.y);
            buf.writeDouble(this.z);
            buf.writeFloat(this.yaw);
            buf.writeFloat(this.pitch);
            super.write(buf);
        }
    }
    
    public static class PositionOnly extends PlayerMoveC2SPacket
    {
        public PositionOnly() {
            this.changePosition = true;
        }
        
        @Environment(EnvType.CLIENT)
        public PositionOnly(final double x, final double y, final double z, final boolean onGround) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.onGround = onGround;
            this.changePosition = true;
        }
        
        @Override
        public void read(final PacketByteBuf buf) throws IOException {
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
            super.read(buf);
        }
        
        @Override
        public void write(final PacketByteBuf buf) throws IOException {
            buf.writeDouble(this.x);
            buf.writeDouble(this.y);
            buf.writeDouble(this.z);
            super.write(buf);
        }
    }
    
    public static class LookOnly extends PlayerMoveC2SPacket
    {
        public LookOnly() {
            this.changeLook = true;
        }
        
        @Environment(EnvType.CLIENT)
        public LookOnly(final float float1, final float float2, final boolean boolean3) {
            this.yaw = float1;
            this.pitch = float2;
            this.onGround = boolean3;
            this.changeLook = true;
        }
        
        @Override
        public void read(final PacketByteBuf buf) throws IOException {
            this.yaw = buf.readFloat();
            this.pitch = buf.readFloat();
            super.read(buf);
        }
        
        @Override
        public void write(final PacketByteBuf buf) throws IOException {
            buf.writeFloat(this.yaw);
            buf.writeFloat(this.pitch);
            super.write(buf);
        }
    }
}
