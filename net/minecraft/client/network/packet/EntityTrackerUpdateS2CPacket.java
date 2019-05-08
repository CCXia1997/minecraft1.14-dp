package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.entity.data.DataTracker;
import java.util.List;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityTrackerUpdateS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private List<DataTracker.Entry<?>> trackedValues;
    
    public EntityTrackerUpdateS2CPacket() {
    }
    
    public EntityTrackerUpdateS2CPacket(final int id, final DataTracker tracker, final boolean boolean3) {
        this.id = id;
        if (boolean3) {
            this.trackedValues = tracker.getAllEntries();
            tracker.clearDirty();
        }
        else {
            this.trackedValues = tracker.getDirtyEntries();
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.trackedValues = DataTracker.deserializePacket(buf);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        DataTracker.entriesToPacket(this.trackedValues, buf);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntityTrackerUpdate(this);
    }
    
    @Environment(EnvType.CLIENT)
    public List<DataTracker.Entry<?>> getTrackedValues() {
        return this.trackedValues;
    }
    
    @Environment(EnvType.CLIENT)
    public int id() {
        return this.id;
    }
}
