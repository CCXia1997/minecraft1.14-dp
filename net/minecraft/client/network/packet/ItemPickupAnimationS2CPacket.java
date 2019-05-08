package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class ItemPickupAnimationS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int entityId;
    private int collectorEntityId;
    private int stackAmount;
    
    public ItemPickupAnimationS2CPacket() {
    }
    
    public ItemPickupAnimationS2CPacket(final int entityId, final int collectorId, final int integer3) {
        this.entityId = entityId;
        this.collectorEntityId = collectorId;
        this.stackAmount = integer3;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.collectorEntityId = buf.readVarInt();
        this.stackAmount = buf.readVarInt();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.collectorEntityId);
        buf.writeVarInt(this.stackAmount);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onItemPickupAnimation(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }
    
    @Environment(EnvType.CLIENT)
    public int getCollectorEntityId() {
        return this.collectorEntityId;
    }
    
    @Environment(EnvType.CLIENT)
    public int getStackAmount() {
        return this.stackAmount;
    }
}
