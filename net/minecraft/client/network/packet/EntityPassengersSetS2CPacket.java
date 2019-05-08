package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntityPassengersSetS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int id;
    private int[] passengerIds;
    
    public EntityPassengersSetS2CPacket() {
    }
    
    public EntityPassengersSetS2CPacket(final Entity entity) {
        this.id = entity.getEntityId();
        final List<Entity> list2 = entity.getPassengerList();
        this.passengerIds = new int[list2.size()];
        for (int integer3 = 0; integer3 < list2.size(); ++integer3) {
            this.passengerIds[integer3] = list2.get(integer3).getEntityId();
        }
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.id = buf.readVarInt();
        this.passengerIds = buf.readIntArray();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        buf.writeIntArray(this.passengerIds);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntityPassengersSet(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int[] getPassengerIds() {
        return this.passengerIds;
    }
    
    @Environment(EnvType.CLIENT)
    public int getId() {
        return this.id;
    }
}
