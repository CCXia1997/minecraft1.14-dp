package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityS2CPacket implements Packet<ClientPlayPacketListener>
{
    protected int id;
    protected short deltaX;
    protected short deltaY;
    protected short deltaZ;
    protected byte yaw;
    protected byte pitch;
    protected boolean onGround;
    protected boolean rotate;
    
    public static long a(final double double1) {
        return MathHelper.lfloor(double1 * 4096.0);
    }
    
    public static Vec3d a(final long long1, final long long3, final long long5) {
        return new Vec3d((double)long1, (double)long3, (double)long5).multiply(2.44140625E-4);
    }
    
    public EntityS2CPacket() {
    }
    
    public EntityS2CPacket(final int integer) {
        this.id = integer;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
    }
    
    public void a(final ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityUpdate(this);
    }
    
    @Override
    public String toString() {
        return "Entity_" + super.toString();
    }
    
    @Environment(EnvType.CLIENT)
    public Entity getEntity(final World world) {
        return world.getEntityById(this.id);
    }
    
    @Environment(EnvType.CLIENT)
    public short getDeltaXShort() {
        return this.deltaX;
    }
    
    @Environment(EnvType.CLIENT)
    public short getDeltaYShort() {
        return this.deltaY;
    }
    
    @Environment(EnvType.CLIENT)
    public short getDeltaZShort() {
        return this.deltaZ;
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
    public boolean hasRotation() {
        return this.rotate;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isOnGround() {
        return this.onGround;
    }
    
    public static class RotateAndMoveRelative extends EntityS2CPacket
    {
        public RotateAndMoveRelative() {
            this.rotate = true;
        }
        
        public RotateAndMoveRelative(final int integer, final short short2, final short short3, final short short4, final byte byte5, final byte byte6, final boolean boolean7) {
            super(integer);
            this.deltaX = short2;
            this.deltaY = short3;
            this.deltaZ = short4;
            this.yaw = byte5;
            this.pitch = byte6;
            this.onGround = boolean7;
            this.rotate = true;
        }
        
        @Override
        public void read(final PacketByteBuf buf) throws IOException {
            super.read(buf);
            this.deltaX = buf.readShort();
            this.deltaY = buf.readShort();
            this.deltaZ = buf.readShort();
            this.yaw = buf.readByte();
            this.pitch = buf.readByte();
            this.onGround = buf.readBoolean();
        }
        
        @Override
        public void write(final PacketByteBuf buf) throws IOException {
            super.write(buf);
            buf.writeShort(this.deltaX);
            buf.writeShort(this.deltaY);
            buf.writeShort(this.deltaZ);
            buf.writeByte(this.yaw);
            buf.writeByte(this.pitch);
            buf.writeBoolean(this.onGround);
        }
    }
    
    public static class MoveRelative extends EntityS2CPacket
    {
        public MoveRelative() {
        }
        
        public MoveRelative(final int integer, final short short2, final short short3, final short short4, final boolean boolean5) {
            super(integer);
            this.deltaX = short2;
            this.deltaY = short3;
            this.deltaZ = short4;
            this.onGround = boolean5;
        }
        
        @Override
        public void read(final PacketByteBuf buf) throws IOException {
            super.read(buf);
            this.deltaX = buf.readShort();
            this.deltaY = buf.readShort();
            this.deltaZ = buf.readShort();
            this.onGround = buf.readBoolean();
        }
        
        @Override
        public void write(final PacketByteBuf buf) throws IOException {
            super.write(buf);
            buf.writeShort(this.deltaX);
            buf.writeShort(this.deltaY);
            buf.writeShort(this.deltaZ);
            buf.writeBoolean(this.onGround);
        }
    }
    
    public static class Rotate extends EntityS2CPacket
    {
        public Rotate() {
            this.rotate = true;
        }
        
        public Rotate(final int integer, final byte byte2, final byte byte3, final boolean boolean4) {
            super(integer);
            this.yaw = byte2;
            this.pitch = byte3;
            this.rotate = true;
            this.onGround = boolean4;
        }
        
        @Override
        public void read(final PacketByteBuf buf) throws IOException {
            super.read(buf);
            this.yaw = buf.readByte();
            this.pitch = buf.readByte();
            this.onGround = buf.readBoolean();
        }
        
        @Override
        public void write(final PacketByteBuf buf) throws IOException {
            super.write(buf);
            buf.writeByte(this.yaw);
            buf.writeByte(this.pitch);
            buf.writeBoolean(this.onGround);
        }
    }
}
