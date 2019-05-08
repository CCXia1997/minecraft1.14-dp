package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.registry.Registry;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ParticleS2CPacket implements Packet<ClientPlayPacketListener>
{
    private float x;
    private float y;
    private float z;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float speed;
    private int count;
    private boolean longDistance;
    private ParticleParameters parameters;
    
    public ParticleS2CPacket() {
    }
    
    public <T extends ParticleParameters> ParticleS2CPacket(final T parameters, final boolean longDistance, final float x, final float y, final float z, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int count) {
        this.parameters = parameters;
        this.longDistance = longDistance;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.count = count;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        ParticleType<?> particleType2 = Registry.PARTICLE_TYPE.get(buf.readInt());
        if (particleType2 == null) {
            particleType2 = ParticleTypes.c;
        }
        this.longDistance = buf.readBoolean();
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.offsetX = buf.readFloat();
        this.offsetY = buf.readFloat();
        this.offsetZ = buf.readFloat();
        this.speed = buf.readFloat();
        this.count = buf.readInt();
        this.parameters = this.<ParticleParameters>readParticleParameters(buf, particleType2);
    }
    
    private <T extends ParticleParameters> T readParticleParameters(final PacketByteBuf buf, final ParticleType<T> type) {
        return type.getParametersFactory().read(type, buf);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeInt(Registry.PARTICLE_TYPE.getRawId(this.parameters.getType()));
        buf.writeBoolean(this.longDistance);
        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.offsetX);
        buf.writeFloat(this.offsetY);
        buf.writeFloat(this.offsetZ);
        buf.writeFloat(this.speed);
        buf.writeInt(this.count);
        this.parameters.write(buf);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isLongDistance() {
        return this.longDistance;
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
    public float getOffsetX() {
        return this.offsetX;
    }
    
    @Environment(EnvType.CLIENT)
    public float getOffsetY() {
        return this.offsetY;
    }
    
    @Environment(EnvType.CLIENT)
    public float getOffsetZ() {
        return this.offsetZ;
    }
    
    @Environment(EnvType.CLIENT)
    public float getSpeed() {
        return this.speed;
    }
    
    @Environment(EnvType.CLIENT)
    public int getCount() {
        return this.count;
    }
    
    @Environment(EnvType.CLIENT)
    public ParticleParameters getParameters() {
        return this.parameters;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onParticle(this);
    }
}
