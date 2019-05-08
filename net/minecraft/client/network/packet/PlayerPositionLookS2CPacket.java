package net.minecraft.client.network.packet;

import java.util.Iterator;
import java.util.EnumSet;
import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import java.util.Set;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class PlayerPositionLookS2CPacket implements Packet<ClientPlayPacketListener>
{
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private Set<Flag> flags;
    private int teleportId;
    
    public PlayerPositionLookS2CPacket() {
    }
    
    public PlayerPositionLookS2CPacket(final double double1, final double double3, final double double5, final float float7, final float float8, final Set<Flag> set9, final int integer10) {
        this.x = double1;
        this.y = double3;
        this.z = double5;
        this.yaw = float7;
        this.pitch = float8;
        this.flags = set9;
        this.teleportId = integer10;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.flags = Flag.getFlags(buf.readUnsignedByte());
        this.teleportId = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(Flag.getBitfield(this.flags));
        buf.writeVarInt(this.teleportId);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onPlayerPositionLook(this);
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
    
    @Environment(EnvType.CLIENT)
    public int getTeleportId() {
        return this.teleportId;
    }
    
    @Environment(EnvType.CLIENT)
    public Set<Flag> getFlags() {
        return this.flags;
    }
    
    public enum Flag
    {
        X(0), 
        Y(1), 
        Z(2), 
        Y_ROT(3), 
        X_ROT(4);
        
        private final int shift;
        
        private Flag(final int integer1) {
            this.shift = integer1;
        }
        
        private int getMask() {
            return 1 << this.shift;
        }
        
        private boolean isSet(final int integer) {
            return (integer & this.getMask()) == this.getMask();
        }
        
        public static Set<Flag> getFlags(final int integer) {
            final Set<Flag> set2 = EnumSet.<Flag>noneOf(Flag.class);
            for (final Flag flag6 : values()) {
                if (flag6.isSet(integer)) {
                    set2.add(flag6);
                }
            }
            return set2;
        }
        
        public static int getBitfield(final Set<Flag> set) {
            int integer2 = 0;
            for (final Flag flag4 : set) {
                integer2 |= flag4.getMask();
            }
            return integer2;
        }
    }
}
