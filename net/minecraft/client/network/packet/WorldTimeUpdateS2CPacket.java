package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class WorldTimeUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private long time;
    private long timeOfDay;
    
    public WorldTimeUpdateS2CPacket() {
    }
    
    public WorldTimeUpdateS2CPacket(final long time, final long long3, final boolean boolean5) {
        this.time = time;
        this.timeOfDay = long3;
        if (!boolean5) {
            this.timeOfDay = -this.timeOfDay;
            if (this.timeOfDay == 0L) {
                this.timeOfDay = -1L;
            }
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.time = buf.readLong();
        this.timeOfDay = buf.readLong();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeLong(this.time);
        buf.writeLong(this.timeOfDay);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onWorldTimeUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public long getTime() {
        return this.time;
    }
    
    @Environment(EnvType.CLIENT)
    public long getTimeOfDay() {
        return this.timeOfDay;
    }
}
