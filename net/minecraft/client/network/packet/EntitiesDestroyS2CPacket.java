package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class EntitiesDestroyS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int[] entityIds;
    
    public EntitiesDestroyS2CPacket() {
    }
    
    public EntitiesDestroyS2CPacket(final int... arr) {
        this.entityIds = arr;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entityIds = new int[buf.readVarInt()];
        for (int integer2 = 0; integer2 < this.entityIds.length; ++integer2) {
            this.entityIds[integer2] = buf.readVarInt();
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityIds.length);
        for (final int integer5 : this.entityIds) {
            buf.writeVarInt(integer5);
        }
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onEntitiesDestroy(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int[] getEntityIds() {
        return this.entityIds;
    }
}
