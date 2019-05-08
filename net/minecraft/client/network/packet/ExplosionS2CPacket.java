package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import com.google.common.collect.Lists;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ExplosionS2CPacket implements Packet<ClientPlayPacketListener>
{
    private double x;
    private double y;
    private double z;
    private float radius;
    private List<BlockPos> affectedBlocks;
    private float playerVelocityX;
    private float playerVelocityY;
    private float playerVelocityZ;
    
    public ExplosionS2CPacket() {
    }
    
    public ExplosionS2CPacket(final double x, final double y, final double z, final float float7, final List<BlockPos> list8, final Vec3d vec3d9) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = float7;
        this.affectedBlocks = Lists.newArrayList(list8);
        if (vec3d9 != null) {
            this.playerVelocityX = (float)vec3d9.x;
            this.playerVelocityY = (float)vec3d9.y;
            this.playerVelocityZ = (float)vec3d9.z;
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.radius = buf.readFloat();
        final int integer2 = buf.readInt();
        this.affectedBlocks = Lists.newArrayListWithCapacity(integer2);
        final int integer3 = (int)this.x;
        final int integer4 = (int)this.y;
        final int integer5 = (int)this.z;
        for (int integer6 = 0; integer6 < integer2; ++integer6) {
            final int integer7 = buf.readByte() + integer3;
            final int integer8 = buf.readByte() + integer4;
            final int integer9 = buf.readByte() + integer5;
            this.affectedBlocks.add(new BlockPos(integer7, integer8, integer9));
        }
        this.playerVelocityX = buf.readFloat();
        this.playerVelocityY = buf.readFloat();
        this.playerVelocityZ = buf.readFloat();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeFloat((float)this.x);
        buf.writeFloat((float)this.y);
        buf.writeFloat((float)this.z);
        buf.writeFloat(this.radius);
        buf.writeInt(this.affectedBlocks.size());
        final int integer2 = (int)this.x;
        final int integer3 = (int)this.y;
        final int integer4 = (int)this.z;
        for (final BlockPos blockPos6 : this.affectedBlocks) {
            final int integer5 = blockPos6.getX() - integer2;
            final int integer6 = blockPos6.getY() - integer3;
            final int integer7 = blockPos6.getZ() - integer4;
            buf.writeByte(integer5);
            buf.writeByte(integer6);
            buf.writeByte(integer7);
        }
        buf.writeFloat(this.playerVelocityX);
        buf.writeFloat(this.playerVelocityY);
        buf.writeFloat(this.playerVelocityZ);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onExplosion(this);
    }
    
    @Environment(EnvType.CLIENT)
    public float getPlayerVelocityX() {
        return this.playerVelocityX;
    }
    
    @Environment(EnvType.CLIENT)
    public float getPlayerVelocityY() {
        return this.playerVelocityY;
    }
    
    @Environment(EnvType.CLIENT)
    public float getPlayerVelocityZ() {
        return this.playerVelocityZ;
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
    public float getRadius() {
        return this.radius;
    }
    
    @Environment(EnvType.CLIENT)
    public List<BlockPos> getAffectedBlocks() {
        return this.affectedBlocks;
    }
}
